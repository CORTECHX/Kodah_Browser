package com.cortechx.kodah

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_view_source.*

class ViewSourceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_source)

        val url = intent.data.toString()

        SourceView.settings.useWideViewPort = true
        SourceView.settings.javaScriptEnabled = true
        SourceView.settings.builtInZoomControls = true
        SourceView.settings.supportZoom()
        SourceView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view!!.loadUrl(request!!.url.toString())
                return true
            }
        }

        var HTML_TEXT = GetHTML().getHtml(url)

        HTML_TEXT = HTML_TEXT!!.replace("<","&lt;")
        HTML_TEXT = HTML_TEXT.replace(">","&gt;")


        val head = " <head><link rel=\"stylesheet\" href=\"highlight.JS/styles/monokai.css\"><script src=\"highlight.JS/highlight.pack.js\"></script><script>hljs.initHighlightingOnLoad();</script></head>"
        val htmlData = "<!doctype html><html>"+head+"<body><pre><code class=\"html\">"+HTML_TEXT+"</code></pre></body></html>"

        SourceView.loadDataWithBaseURL("file:///android_asset/",htmlData, "text/html", "utf-8",null)

        SourceLoad.text = url
    }
}
