package com.cortechx.kodah

import android.app.Activity
import android.app.DownloadManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

import android.webkit.WebView
import android.widget.Toast
import android.graphics.drawable.Drawable
import android.app.WallpaperManager
import android.content.*
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
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val ID_SAVEIMAGE = 1000
    private val ID_VIEWIMAGE = 2000
    private val ID_SET_AS_BG = 3000
    private val ID_SAVELINK = 4000
    private val ID_SHARELINK = 5000
    private val ID_OPENLINK = 6000

    private val REQUEST_CODE_HISTORY = 100
    private val REQUEST_CODE_VOICE = 200

    var prefs: SharedPreferences ?= null
    var editor:SharedPreferences.Editor? = null
    val historyTitles = ArrayList<String>()
    val historyUrls = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        prefs = getSharedPreferences("Kodah_Data", Context.MODE_PRIVATE)
        editor = prefs!!.edit()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //SETUP THE WEBVIEW
        webview.settings.javaScriptEnabled = true
        webview.settings.setAppCacheEnabled(true)
        webview.settings.setAppCachePath(this.cacheDir.path)
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        registerForContextMenu (webview)
        webview.setDownloadListener{p0, _, _, _, _ ->
                val request = DownloadManager.Request(Uri.parse(p0))
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val Dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                Dm.enqueue(request)
                Toast.makeText(applicationContext, "Download started", Toast.LENGTH_SHORT).show()
            }
        webview.webViewClient = object : WebViewClient() {
            val loadBar = Snackbar.make(webview, "LOADING...", Snackbar.LENGTH_INDEFINITE)

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view!!.loadUrl(request!!.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadBar.setAction("STOP", {
                    view!!.stopLoading()
                    loadBar.dismiss()
                }).show()
                UrlBar.setText(url)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                loadBar.dismiss()

                if (url!!.contains (".mp3")) {
                    val intent = Intent (Intent.ACTION_VIEW)
                    intent.setDataAndType (Uri.parse (url), "audio/*")
                    startActivity (Intent.createChooser (intent, "Open Using..."))
                }
                else if (url.contains (".mp4") || url.contains (".3gp")) {
                    val intent = Intent (Intent.ACTION_VIEW)
                    intent.setDataAndType (Uri.parse (url), "video/*")
                    startActivity (Intent.createChooser (intent, "Open Using..."))
                }
                else if (url.contains ("youtube.com")) {
                    startActivity (Intent (Intent.ACTION_VIEW, Uri.parse (url)))
                }

                //ADD TO HISTORY
                if(prefs!!.getBoolean("HistoryEnabled", true)){
                    historyTitles.add(view!!.title)
                    historyUrls.add(view.url)
                }
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                Snackbar.make(webview, "ERROR LOADING PAGE", Snackbar.LENGTH_SHORT).show()
                view!!.loadUrl("file:///android_asset/error.html")
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

        //KEYBOARD ACTION
        UrlBar.setOnEditorActionListener {_, p1, _  ->
                if (p1 == EditorInfo.IME_ACTION_SEND) {
                    val webpage: String = UrlBar.text.toString ()
                    if (Patterns.WEB_URL.matcher(webpage).matches()) {
                        if(webpage.contains("http") or webpage.contains("https")) {
                            webview.loadUrl(webpage)
                        }else{
                            webview.loadUrl("http://" + webpage)
                        }
                    }
                    else {
                        webview.loadUrl ("http://www.google.com/search?q=" + webpage)
                    }
                    //CLOSE SOFT KEYBOARD
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(UrlBar.windowToken, 0)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener true
            }

        //LOAD URL
        val url = intent.data

        if (url == null) {
            webview.loadUrl("http://google.com")
        } else {
            webview.loadUrl(url.toString())
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        if (webview.canGoBack()){
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
                SaveHistory()
                val i = Intent ("android.intent.action.HISTORY")
                startActivityForResult (i, REQUEST_CODE_HISTORY)
            }
            R.id.nav_bookmarks -> {
                showBookmarks()
            }
            //dev tools
            R.id.nav_view_html -> {
			  val i = Intent ("android.intent.action.VIEW_SOURCE")
			  i.data = Uri.parse(webview.url)
			  startActivity (i)
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
                val i = Intent ("android.intent.action.ABOUT")
                startActivity (i)
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

    fun addBookmark(v:View){
        val bookmarkname = EditText (this)
        bookmarkname.setText (webview.title)
        AlertDialog.Builder (this).
                setTitle ("Add Bookmark").
                setMessage ("name this bookmark:").
                setView (bookmarkname).
                setPositiveButton("ADD", {_, _ ->
                    if (bookmarkname.text.toString() != ""){
                        try {
                            editor!!.putString("BookmarkTitles", prefs!!.getString("BookmarkTitles","") + "\n" + bookmarkname.text.toString())
                            editor!!.putString("BookmarkUrls", prefs!!.getString("BookmarkUrls","") + "\n" + webview.url)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        editor!!.commit()
                    }else{
                        Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show()
                    }
                }).setNegativeButton("Cancel",{p0, _ ->
                    p0.dismiss()
                }).show()
    }

    fun showBookmarks(){
        val d = AlertDialog.Builder(this)
        val list = ListView(this)
        val titles = prefs!!.getString("BookmarkTitles", "").split("\n")
        val urls = prefs!!.getString("BookmarkUrls", "").split("\n")
        val arrayAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                titles)
        list.adapter = arrayAdapter

        list.setOnItemClickListener { _, _, i, _ ->
            webview.loadUrl(urls[i])
        }
        d.setTitle("Bookmarks").setView(list).show()
    }

    fun Controls(v: View){
        when (v.id){
            R.id.btn_back->{
                    webview.goBack()
            }
            R.id.btn_forward->{
                    webview.goForward()
            }
            R.id.btn_reload->{
                webview.reload()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VOICE) {
                val words = data!!.getStringArrayListExtra (RecognizerIntent.EXTRA_RESULTS)[0]
                webview.loadUrl ("http://www.google.com/search?q=" + words)
            }
            else if (requestCode == REQUEST_CODE_HISTORY) {
                webview.loadUrl (data!!.extras.getString ("url_clicked"))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        SaveHistory()
        super.onStop()
    }

    private fun SaveHistory(){
        try {
            editor!!.putString("HistoryTitles", prefs!!.getString("HistoryTitles","") + ObjectSerializer.serialize(historyTitles))
            editor!!.putString("HistoryUrls", prefs!!.getString("HistoryUrls","") + ObjectSerializer.serialize(historyUrls))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        editor!!.commit()
    }

}
