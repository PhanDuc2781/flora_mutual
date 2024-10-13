package com.cmd.flora.utils.widget.header

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.ViewWebviewHeaderBinding


//need dessign
class WebViewHeaderView : FrameLayout {

    var webView: WebView? = null

    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    private lateinit var binding: ViewWebviewHeaderBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = ViewWebviewHeaderBinding.inflate(LayoutInflater.from(context), this, true)
        binding.btnReload.setOnSingleClickListener {
            webView?.reload()
            checkWebViewNav(webView)
        }

        binding.btnBack.setOnSingleClickListener {
            if (webView?.canGoBack() == true) {
                webView?.goBack()
                checkWebViewNav(webView)
            }
        }

        binding.btnForward.setOnSingleClickListener {
            if (webView?.canGoForward() == true) {
                webView?.goForward()
                checkWebViewNav(webView)
            }
        }

        binding.btnBack.isEnabled = false
        binding.btnForward.isEnabled = false
    }

    fun setOnCloseClickListener(listener: (View) -> Unit) {
        binding.btnClose.setOnSingleClickListener { listener.invoke(binding.btnClose) }
    }

    fun setOnReloadClickListener(listener: (View) -> Unit) {
        binding.btnReload.setOnSingleClickListener { listener.invoke(binding.btnClose) }
    }

    fun setupWithWebView(webView: WebView, client: WebViewClient) {
        this.webView = webView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                client.onPageFinished(view, url)
                checkWebViewNav(view)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                client.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return client.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    fun onDestroy() {
        this.webView?.webViewClient = WebViewClient()
        this.webView = null
    }

    private fun checkWebViewNav(webView: WebView?) {
        webView ?: return
        binding.imgBack.alpha = if (webView.canGoBack()) 1f else 0.6f
        binding.imgForward.alpha = if (webView.canGoForward()) 1f else 0.6f
        binding.btnBack.isEnabled = webView.canGoBack()
        binding.btnForward.isEnabled = webView.canGoForward()
    }
}