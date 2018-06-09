package com.cortechx.kodah

import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
//import com.squareup.picasso.*;

import android.webkit.WebView
import android.content.Intent
import android.widget.Toast
import android.content.ClipData
import android.graphics.drawable.Drawable
import android.app.WallpaperManager
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Canvas
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.speech.RecognizerIntent
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val ID_SAVEIMAGE = 1000
    private val ID_VIEWIMAGE = 2000
    private val ID_SET_AS_BG = 3000
    private val ID_SAVELINK = 4000
    private val ID_SHARELINK = 5000
    private val ID_OPENLINK = 6000

    private val REQUEST_CODE_RESULTS = 100
    private val REQUEST_CODE_VOICE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
           //BRING UP CONTROLS
            if(controls.visibility == View.GONE) {
                controls.visibility = View.VISIBLE
            }else{
                controls.visibility = View.GONE
            }
        }
        //BUTTONS
        btn_back.setOnClickListener { _ ->
            if (webview.canGoBack())
                webview.goBack()
        }
        btn_forward.setOnClickListener { _ ->
            if (webview.canGoForward())
                webview.goForward()
        }
        btn_reload.setOnClickListener { _ ->
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
        registerForContextMenu (webview)
        webview.webViewClient = object : WebViewClient() {
            val loadBar = Snackbar.make(webview, "LOADING...", Snackbar.LENGTH_INDEFINITE)

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (URLUtil.isValidUrl (request!!.url.toString())) {
                    if (request.url.toString().contains (".mp3")) {
                        val intent = Intent (Intent.ACTION_VIEW)
                        intent.setDataAndType (Uri.parse (request.url.toString()), "audio/*");
                        startActivity (Intent.createChooser (intent, "Open Using..."))
                    }
                    else if (request.url.toString().contains (".mp4") || request.url.toString().contains (".3gp")) {
                        val intent = Intent (Intent.ACTION_VIEW)
                        intent.setDataAndType (Uri.parse (request.url.toString()), "video/*")
                        startActivity (Intent.createChooser (intent, "Open Using..."))
                    }
                    else if (request.url.toString().contains ("youtube.com")) {
                        startActivity (Intent (Intent.ACTION_VIEW, Uri.parse (request.url.toString())))
                    }
                    else {
                        webview.loadUrl(request.url.toString())
                    }
                }
                else {
                    webview.loadUrl ("http://www.google.com/search?sclient=tablet-gws&safe=active&site=&source=hp&q=" + request.url.toString())
                }

                if ("about:blank" == request.url.toString() && view!!.tag != null) {
                    view.loadUrl (view.tag.toString ())
                }
                else {
                    view!!.tag = request.url.toString()
                }

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

            override fun onReceivedTitle(view: WebView?, title: String?) {
                webviewTitle.text = title
                super.onReceivedTitle(view, title)
            }
        }

        //LOAD URL
        val url = intent.data

        if (url == null) {
            webview.loadUrl("http://google.com")
        } else {
            webview.loadUrl(url.toString())
        }

        //KEYBOARD ACTION
        UrlBar.setOnEditorActionListener (object: TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_DONE || p1 == EditorInfo.IME_ACTION_NEXT) {
                    val webpage: String = UrlBar.text.toString ()
                    webview.loadUrl (webpage)
                }
               return true
            }
        })
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

            //main options
            R.id.nav_voice_search -> {
                val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now")
                startActivityForResult(i, REQUEST_CODE_VOICE)
            }
            R.id.nav_shortcut -> {
                addShortcut()
            }
            R.id.nav_save_page -> {

            }
            R.id.nav_share_page -> {
                val intent = Intent (Intent.ACTION_SEND)
			    intent.type ="text/plain"
                intent.putExtra (Intent.EXTRA_TEXT, webview.url)
                startActivity (Intent.createChooser (intent, "Share Via..."))
            }
            R.id.nav_print -> {
                printWebDoc()
            }
            R.id.nav_history -> {

            }
            R.id.nav_bookmarks -> {

            }
            //dev tools
            R.id.nav_view_html -> {

            }
            R.id.nav_editor -> {

            }
            R.id.nav_view_console -> {

            }
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
            //About app
            R.id.nav_about -> {

            }
            R.id.nav_settings -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val result = webview.hitTestResult

        val handler = MenuItem.OnMenuItemClickListener { item ->
            // do the menu action
            when (item.itemId) {
            //SAVE IMAGE
                ID_SAVEIMAGE -> SaveImage().SaveImage(result.extra)

            //VIEW IMAGE
                ID_VIEWIMAGE -> webview.loadUrl(result.extra)

            //SET IMAGE AS BACKGROUND
                ID_SET_AS_BG -> {
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    try {
                        val stream = URL(result.extra).content as InputStream
                        val drawable = Drawable.createFromStream(stream, "wallpaper_image")
                        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        drawable.draw(canvas)
                        wallpaperManager.setBitmap(bitmap)
                        Toast.makeText(applicationContext, "Wallpaper set", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Set Wallpaper failed", Toast.LENGTH_SHORT).show()
                    }

                }

            //SAVE LINK
                ID_SAVELINK -> {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(result.extra, result.extra)
                    clipboard.primaryClip = clip
                    Toast.makeText(this@MainActivity, "Copied to ClipBoard", Toast.LENGTH_SHORT).show()
                }

            //SHARE LINK
                ID_SHARELINK -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, result.extra)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!")
                    startActivity(Intent.createChooser(intent, "Share Via..."))
                }

            //OPEN LINK
                ID_OPENLINK -> webview.loadUrl(result.extra)
            }
            true
        }

        //if image
        if (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // Menu options for an image.
            menu!!.setHeaderTitle (result.extra );
            menu.add (0, ID_SAVEIMAGE, 0, "Save Image").setOnMenuItemClickListener (handler)
            menu.add (0, ID_VIEWIMAGE, 0, "View Image").setOnMenuItemClickListener (handler)
            menu.add (0, ID_SAVELINK, 0, "Copy Image URL").setOnMenuItemClickListener (handler)
            menu.add (0, ID_SHARELINK, 0, "Share Image Url").setOnMenuItemClickListener (handler)
            menu.add (0, ID_SET_AS_BG, 0, "Set as Wallpaper").setOnMenuItemClickListener (handler)
        }
        //if hyperlink
        else if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            // Menu options for a hyperlink.
            menu!!.setHeaderTitle (result.extra)
            menu.add (0, ID_SAVELINK, 0, "Save Link").setOnMenuItemClickListener (handler)
            menu.add (0, ID_SHARELINK, 0, "Share Link").setOnMenuItemClickListener (handler)
            menu.add (0, ID_OPENLINK, 0, "Open").setOnMenuItemClickListener (handler)
        }

        super.onCreateContextMenu(menu, v, menuInfo)
    }

    fun printWebDoc()
	  {
		// Check if Kitkat or higher sdk 19
		if (Build.VERSION.SDK_INT >= 21) {
			val printManager: PrintManager =  getSystemService (Context.PRINT_SERVICE) as PrintManager
			// Get a print adapter instance
			val printAdapter: PrintDocumentAdapter = webview.createPrintDocumentAdapter (webview.title)

			// Create a print job with name and adapter instance
			val jobName = "Koadah Web Document"
			printManager.print (jobName, printAdapter, PrintAttributes.Builder ().build ())
		  }
		else {
			AlertDialog.Builder (this).
			  setTitle ("Printing Error").
			  setMessage ("Unable to print the document.\nThis is only supported on Android Kitkat(API 19) and higher").show ()
		  }
	  }

    fun addShortcut()
	  {
		//Adding shortcut for website on Home screen
          if (Build.VERSION.SDK_INT >= 25) {
              val shortcutManager = getSystemService(ShortcutManager::class.java)

              val shortcut = ShortcutInfo.Builder(this, "id1")
                      .setShortLabel(webview.title)
                      .setLongLabel("Open " + webview.title)
                      .setIcon(Icon.createWithResource(this, R.mipmap.kodah_icon))
                      .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse(webview.url))).build()

              shortcutManager.dynamicShortcuts = Arrays.asList(shortcut)
          }else{
                val shortcutIntent = Intent (applicationContext, MainActivity::class.java)
                shortcutIntent.data = Uri.parse(webview.url)

                val addIntent =  Intent ()
                addIntent .putExtra (Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                addIntent.putExtra (Intent.EXTRA_SHORTCUT_NAME, webview.title)
                addIntent.putExtra (Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext (applicationContext, R.mipmap.kodah_icon))
                addIntent .action = "com.android.launcher.action.INSTALL_SHORTCUT"
                addIntent.putExtra ("duplicate", false)
                //if it's already there, don't duplicate
                applicationContext.sendBroadcast (addIntent)
          }
	  }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VOICE) {
                val words = data!!.getStringArrayListExtra (RecognizerIntent.EXTRA_RESULTS)[0]
                webview.loadUrl (words)
            }
            else if (requestCode == REQUEST_CODE_RESULTS) {
                webview.loadUrl (data!!.extras.getString ("result"))
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }



}
