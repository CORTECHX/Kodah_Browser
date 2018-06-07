package com.cortechx.kodah

import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
           //BRING UP CONTROLS
            if(controls.visibility == View.INVISIBLE) {
                controls.visibility = View.VISIBLE
            }else{
                controls.visibility = View.INVISIBLE
            }
        }
        //BUTTONS
        btn_back.setOnClickListener { view ->
            webview.goBack()
        }
        btn_forward.setOnClickListener { view ->
            webview.goForward()
        }
        btn_back.setOnClickListener { view ->
            webview.reload()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //SETUP THE WEBVIEW
        webview.settings.javaScriptEnabled = true
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        webview.webViewClient = object : WebViewClient() {
            val loadBar = Snackbar.make(webview, "LOADING...", Snackbar.LENGTH_INDEFINITE)

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                webview.loadUrl(request!!.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadBar.setAction("STOP", {
                    webview.stopLoading()
                    loadBar.dismiss()
                }).show()

                UrlBar.setText(url)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                loadBar.dismiss()
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                Snackbar.make(webview, "ERROR LOADING PAGE", Snackbar.LENGTH_SHORT).show()
                super.onReceivedError(view, request, error)
            }
        }
        webview.webChromeClient = object :WebChromeClient(){
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return true
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@MainActivity).setMessage(message).setTitle(url+" Says:").show()
                return true
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                siteIcon.setImageBitmap(icon)
                super.onReceivedIcon(view, icon)
            }
        }

        webview.loadUrl("http://google.com")

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else if (webview.canGoBack()){
            webview.goBack()
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            //dev sites
            R.id.nav_stack_overflow -> {
                webview.loadUrl("http://stackoverflow.com")
            }
            R.id.nav_w3 -> {
                webview.loadUrl("http://w3schools.com")
            }
            R.id.nav_android_devs -> {
                webview.loadUrl("http://developer.android.com")
            }
            R.id.nav_fb_devs -> {
                webview.loadUrl("http://developer.facebook.com")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
