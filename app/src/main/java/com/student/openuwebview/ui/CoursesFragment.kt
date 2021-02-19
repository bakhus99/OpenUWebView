package com.student.openuwebview.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.student.openuwebview.BaseFragment
import com.student.openuwebview.databinding.FragmentCoursesBinding


private lateinit var progressBar: ProgressBar
lateinit var fullScreen: View
private var _binding: FragmentCoursesBinding? = null
private val binding get() = _binding!!

class CoursesFragment : BaseFragment() {

    override var bottomNavigationViewVisibility = View.VISIBLE

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        progressBar = binding.pbBar
        binding.webView.loadUrl("https://openu.psu.kz/courses/mycourses")
        binding.webView.settings.javaScriptEnabled = true
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