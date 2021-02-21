package com.student.openuwebview.ui

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.student.openuwebview.BaseFragment
import com.student.openuwebview.databinding.FragmentCoursesBinding


private lateinit var progressBar: ProgressBar
lateinit var fullScreen: View
private var _binding: FragmentCoursesBinding? = null
private val binding get() = _binding!!


class CoursesFragment : BaseFragment() {

    companion object {
        private const val FILECHOOSER_RESULTCODE = 123
        private const val REQUEST_CODE = 1499
    }

    override var bottomNavigationViewVisibility = View.VISIBLE

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        progressBar = binding.pbBar
        binding.webView.loadUrl("https://openu.psu.kz/courses/mycourses")
        val list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            webChromeClient = MyChromeWebView()
            webViewClient = MyWebViewClient()
        }
        WebView.setWebContentsDebuggingEnabled(false)
        binding.webView.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && !binding.webView.canGoBack()) {
                return@setOnKeyListener false
            } else if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP) {
                binding.webView.goBack()
                return@setOnKeyListener true
            } else return@setOnKeyListener true
        }

        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    downloadDialog(url, userAgent, contentDisposition, mimetype)
                }else{
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                }
            }else{
                downloadDialog(url, userAgent, contentDisposition, mimetype)
            }
        }
    }

    fun downloadDialog(url:String,userAgent:String,contentDisposition:String,mimetype:String)
    {
        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Download")
        builder.setMessage("Do you want to save $filename")
        builder.setPositiveButton("Yes") { dialog, which ->
            val request = DownloadManager.Request(Uri.parse(url))
            val cookie = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("Cookie",cookie)
            request.addRequestHeader("User-Agent",userAgent)
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadmanager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename)
            downloadmanager.enqueue(request)
        }
        builder.setNegativeButton("Cancel")
        {dialog, which ->
            dialog.cancel()
        }
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host == "openu.psu.kz") {
                return false
            }
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
                return true
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            progressBar.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            progressBar.visibility = View.GONE
            super.onPageFinished(view, url)
        }
    }

    private inner class MyChromeWebView : WebChromeClient() {

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)

            if (view is FrameLayout) {
                fullScreen = view
                binding.fullScreenContainer.addView(fullScreen)
                binding.fullScreenContainer.visibility = View.VISIBLE
                binding.mainContainer.visibility = View.GONE
            }

        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            binding.fullScreenContainer.removeView(fullScreen)
            binding.fullScreenContainer.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE

        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(null)
                uploadMessage = null
            }

            uploadMessage = filePathCallback
            openChooserActivity()
            return true
        }
    }

    private fun openChooserActivity() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == uploadMessage && null == uploadMessage) return
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (uploadMessage != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (uploadMessage != null) {
                //uploadMessage!!.onReceiveValue(result)
                uploadMessage = null
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILECHOOSER_RESULTCODE || uploadMessage == null)
            return
        var results: Array<Uri>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = Array(clipData.itemCount) { i ->
                        clipData.getItemAt(i).uri
                    }
                }
                if (dataString != null)
                    results = arrayOf(Uri.parse(dataString))
            }
        }
        uploadMessage!!.onReceiveValue(results)
        uploadMessage = null
    }

}
