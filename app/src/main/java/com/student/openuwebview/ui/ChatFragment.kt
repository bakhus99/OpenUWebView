package com.student.openuwebview.ui

import android.Manifest
import android.Manifest.permission.*
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
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.student.openuwebview.R
import com.student.openuwebview.databinding.FragmentChatBinding

var uploadMessage: ValueCallback<Array<Uri>>? = null


class ChatFragment : Fragment(R.layout.fragment_forum) {

    private lateinit var progressBar: ProgressBar

    companion object {
        private const val FILECHOOSER_RESULTCODE = 1
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChatBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.hide()
        progressBar = binding.pbBar
        binding.webView.apply {
            loadUrl("https://openu.psu.kz/chat")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            webViewClient = MyWebViewClient()
            webChromeClient = MyChromeClient()
        }

        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    downloadDialog(url, userAgent, contentDisposition, mimetype)
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
            } else {
                downloadDialog(url, userAgent, contentDisposition, mimetype)
            }
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
    }

    private fun downloadDialog(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimetype: String
    ) {
        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.download))
        builder.setMessage("Do you want to save $filename")
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            val request = DownloadManager.Request(Uri.parse(url))
            val cookie = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("Cookie", cookie)
            request.addRequestHeader("User-Agent", userAgent)
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadmanager =
                activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            downloadmanager.enqueue(request)
        }
        builder.setNegativeButton(getString(R.string.cancel))
        { dialog, which ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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

    private inner class MyChromeClient : WebChromeClient() {

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
            //val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (uploadMessage != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (uploadMessage != null) {
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