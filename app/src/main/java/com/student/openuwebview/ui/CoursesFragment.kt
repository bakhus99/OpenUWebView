package com.student.openuwebview.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import com.student.openuwebview.BaseFragment
import com.student.openuwebview.ManagePermissions
import com.student.openuwebview.R
import com.student.openuwebview.databinding.FragmentCoursesBinding


private lateinit var progressBar: ProgressBar
lateinit var fullScreen: View
private var _binding: FragmentCoursesBinding? = null
private val binding get() = _binding!!
private lateinit var managePermissions: ManagePermissions


class CoursesFragment : BaseFragment() {

    companion object {
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
        managePermissions = ManagePermissions(requireActivity(), list, REQUEST_CODE)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.webChromeClient = MyChromeWebView()
        binding.webView.webViewClient = MyWebViewClient()
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
        //getting file name from url
        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
        //Alertdialog
        val builder = AlertDialog.Builder(requireContext())
        //title for AlertDialog
        builder.setTitle("Download")
        //message of AlertDialog
        builder.setMessage("Do you want to save $filename")
        //if YES button clicks
        builder.setPositiveButton("Yes") { dialog, which ->
            //DownloadManager.Request created with url.
            val request = DownloadManager.Request(Uri.parse(url))
            //cookie
            val cookie = CookieManager.getInstance().getCookie(url)
            //Add cookie and User-Agent to request
            request.addRequestHeader("Cookie",cookie)
            request.addRequestHeader("User-Agent",userAgent)
            //file scanned by MediaScannar
            request.allowScanningByMediaScanner()
            //Download is visible and its progress, after completion too.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            //DownloadManager created
            val downloadmanager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            //Saving file in Download folder
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename)
            //download enqued
            downloadmanager.enqueue(request)
        }
        //If Cancel button clicks
        builder.setNegativeButton("Cancel")
        {dialog, which ->
            //cancel the dialog if Cancel clicks
            dialog.cancel()
        }
        val dialog:AlertDialog = builder.create()
        //alertdialog shows
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}