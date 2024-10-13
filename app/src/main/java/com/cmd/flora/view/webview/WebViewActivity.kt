package com.cmd.flora.view.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.cmd.flora.application.isInternetAvailable
import com.cmd.flora.application.showNetworkError
import com.cmd.flora.base.BaseActivity
import com.cmd.flora.databinding.ActivityWebviewBinding
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.file.Paths

@SuppressLint("SetJavaScriptEnabled")
fun WebView.config() {
    settings.apply {
        javaScriptEnabled = true
        databaseEnabled = true
        domStorageEnabled = true
        mediaPlaybackRequiresUserGesture = false
        allowFileAccess = true
    }


    webChromeClient = WebChromeClient()
}

class WebViewActivity : BaseActivity<ActivityWebviewBinding>(ActivityWebviewBinding::inflate) {

    companion object {
        const val ARG_URL = "ARG_URL"

        fun start(activity: Activity, url: String) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).apply {
                putExtra(ARG_URL, url)
            })
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        binding.progressView1.visible()
        val url = intent.extras?.getString(ARG_URL)

        url?.let {
            if (it.contains(".pdf")) {
                setupPdfView(it)
            } else {
                setupWebView(it)
            }
        } ?: finish()

        binding.header.setOnCloseClickListener {
            finish()
        }
    }

    fun getFilePDF(url: String): File {
        val fileName = Paths.get(URI(url).getPath()).fileName.toString()
        return File(cacheDir, fileName)
    }

    private fun setupPdfView(url: String) {
        binding.header.setOnReloadClickListener {
            if (binding.progressView1.visibility == View.GONE) setupPdfView(url)
        }

        if (!isInternetAvailable) {
            binding.progressView1.gone()
            showNetworkError()
            return
        } else {
            binding.progressView1.visible()
        }

        binding.webView.visibility = View.GONE
        binding.pdfView.visible()
        binding.progressView1.visible()
        val file = getFilePDF(url)
        if (file.exists()) {
            binding.pdfView.fromFile(file).onLoad {
                binding.progressView1.gone()
            }.load()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val input = URL(url).openStream()
                    file.copyInputStreamToFile(input)
                    withContext(Dispatchers.Main) {
                        binding.pdfView.fromFile(file).onLoad {
                            binding.progressView1.gone()
                        }.load()
                    }
                } catch (e: Exception) {
                    Log.d("aaa", e.message.toString())
                }
            }

        }
    }

    private fun setupWebView(url: String) {
        binding.pdfView.visibility = View.GONE
        binding.webView.apply {
            config()
            binding.header.setupWithWebView(this, object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (!isInternetAvailable) {
                        binding.progressView1.gone()
                        showNetworkError()
                        return
                    } else {
                        binding.progressView1.visible()
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressView1.gone()
                }
            })
            loadUrl(url)
        }
    }

    override fun onDestroy() {
        lifecycleScope.cancel()
        binding.header.onDestroy()
        super.onDestroy()
    }
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    this.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
    inputStream.close()
}