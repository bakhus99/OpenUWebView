package com.student.openuwebview.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.student.openuwebview.R
import com.student.openuwebview.databinding.FragmentCoursesBinding
import com.student.openuwebview.databinding.FragmentOpenuBinding

private  lateinit var  progressBar: ProgressBar


class CoursesFragment : Fragment(R.layout.fragment_courses) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCoursesBinding.bind(view)
        progressBar = binding.pbBar
        binding.webView.loadUrl("https://openu.psu.kz/categories")
        binding.webView.settings.javaScriptEnabled = true
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


//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val webView: WebView = findViewById(R.id.webView)
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            webView.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }

}