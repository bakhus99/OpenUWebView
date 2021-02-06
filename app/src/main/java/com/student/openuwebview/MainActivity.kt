package com.student.openuwebview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OpenUWebView)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)

//        val webView: WebView = findViewById(R.id.webView)
//        webView.loadUrl("https://openu.psu.kz/")
//        webView.settings.javaScriptEnabled = true
//        webView.webViewClient = MyWebViewClient()
//        WebView.setWebContentsDebuggingEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    private inner class MyWebViewClient : WebViewClient() {
//
//        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//            if (Uri.parse(url).host == "openu.psu.kz") {
//                return false
//            }
//            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
//                startActivity(this)
//                return true
//            }
//
//        }
//
//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
//        }
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val webView: WebView = findViewById(R.id.webView)
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            webView.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }


}