package com.cmd.flora.view.detail

import android.graphics.Bitmap
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import com.cmd.flora.R
import com.cmd.flora.base.BaseFragment
import com.cmd.flora.base.MyActivity
import com.cmd.flora.base.pushTo
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.databinding.FragmentDetailBinding
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.webview.WebViewActivity
import com.cmd.flora.view.webview.config

class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
    override fun initView() {
        super.initView()
        binding.progressView1.visible()
        binding.webView.config()
        arguments?.getString(WebViewActivity.ARG_URL)?.let {
            binding.webView.loadUrl(it)
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressView1.visible()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressView1.gone()
            }
        }
    }

    override fun onDestroyView() {
        binding.webView.webViewClient = WebViewClient()
        binding.progressView1.gone()
        super.onDestroyView()
    }

    companion object {
        fun start(activity: MyActivity?, url: String?) {
            if (url == null || !URLUtil.isValidUrl(url)) {
                activity?.let {
                    it.showCustomAlertDialog(
                        AlertData(
                            msg = it.getString(R.string.url_not_found),
                            posTitle = it.getString(R.string.ok)
                        )
                    )
                }
            } else (activity as? MainActivity)?.pushTo(
                R.id.actionToDetailFragment,
                bundleOf(WebViewActivity.ARG_URL to url)
            )
        }
    }
}