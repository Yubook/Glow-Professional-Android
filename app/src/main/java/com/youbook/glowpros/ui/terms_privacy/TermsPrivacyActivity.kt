package com.youbook.glowpros.ui.terms_privacy

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityTermsPrivacyBinding
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.utils.Utils
import kotlinx.coroutines.launch


class TermsPrivacyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityTermsPrivacyBinding
    private lateinit var viewModel: TermsPrivacyViewModel
    private var termsConditionData : String? = ""
    private var type : String? = ""
    private var ABOUT_US_URL : String? = "https://www.fadeapp.io"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_privacy)

        binding = ActivityTermsPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            TermsPrivacyViewModelFactory(
                TermsPrivacyRepository(
                    MyApi.getInstance()
                )
            )
        ).get(TermsPrivacyViewModel::class.java)

        setListener()
        type = intent.getStringExtra("Type")
        when {
            type.equals("Privacy") -> {
                binding.tvToolbarTitle.text = "Privacy Policy"
            }
            type.equals("AboutUs") -> {
                binding.tvToolbarTitle.text = "About Us"
            }
            else -> {
                binding.tvToolbarTitle.text = "Terms & Conditions"
            }
        }

        if (!type.equals("AboutUs")){
            getTermsPrivacyData()
        } else {
            LoadUrlWebView(ABOUT_US_URL!!)
        }


        viewModel.termsPrivacyResponse.observe(this, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {

                        if (it.value.result != null) {

                            if (it.value.result.isNotEmpty()) {

                                for (data in it.value.result) {
                                    if (data!!.selection!! == type) {
                                        termsConditionData = data.termsDescription

                                        loadWebViewData(termsConditionData)
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {}
            }
        })
    }

    private fun setListener() {
        binding.ivBackButton.setOnClickListener(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebViewData(data: String?) {
        binding.webView.settings.javaScriptEnabled = true;
        binding.webView.loadData(data!!, "text/html", "UTF-8")

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun LoadUrlWebView(url_api: String) {
        try {
            binding.webView.webViewClient = WebViewClient()
            binding.webView.webChromeClient = MyWebChromeClient(url_api)
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.setSupportZoom(true)
            binding.webView.settings.allowContentAccess = true
            binding.webView.settings.builtInZoomControls = true
            binding.webView.settings.displayZoomControls = false
            binding.webView.loadUrl(url_api)
        } catch (e: Exception) {
            Log.w("TAG", "setUpNavigationView", e)
        }
    }

    private fun getTermsPrivacyData() {
        viewModel.viewModelScope.launch {
            viewModel.getTermsPolicy()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBackButton -> finish()
        }

    }

    private class MyWebChromeClient(private val urlAccount: String) :
        WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {

        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)

        }
    }
}