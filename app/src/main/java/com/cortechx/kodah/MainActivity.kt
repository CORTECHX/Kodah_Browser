package com.cortechx.kodah

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

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
import android.view.*
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_CODE_VOICE = 200

    var prefs: SharedPreferences ?= null
    var editor:SharedPreferences.Editor? = null

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

        startService(Intent(this, ClipboardService::class.java))
    }

    fun connectToServer(v: View)
    {
        val client = TCPClient()
        if(!serverIP_txt.text.toString().isEmpty()) {
            client.SendMessage("This is a message to PC", serverIP_txt.text.toString())
        }else{
            Toast.makeText(this, "No Remote IP Specified", Toast.LENGTH_LONG).show()
        }
    }

    fun sendSnippetToPC(v: View)
    {
        val client = TCPClient()
        if(!serverIP_txt.text.toString().isEmpty()) {
            client.SendMessage("This is a message to PC", serverIP_txt.text.toString())
        }else{
            Toast.makeText(this, "No Remote IP Specified", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
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
                //intent.putExtra (Intent.EXTRA_TEXT, webview.url)
                //startActivity (Intent.createChooser (intent, "Share Via..."))
            }
            R.id.nav_print -> {
                printWebDoc()
            }

            //dev tools
            R.id.nav_view_html -> {
			  val i = Intent ("android.intent.action.VIEW_SOURCE")
			  i.data = Uri.parse(webview.url)
			  startActivity (i)
            }
            R.id.nav_editor -> {
                val i = Intent ("android.intent.action.EDITOR")
                startActivity (i)
            }
            R.id.nav_view_console -> {

            }

            //dev sites
            R.id.nav_stack_overflow -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://stackoverflow.com")))
            }
            R.id.nav_w3 -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://w3schools.com")))
            }
            R.id.nav_android_devs -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com")))
            }
            R.id.nav_fb_devs -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.facebook.com")))
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

    fun printWebDoc()
	  {
		// Check if Kitkat or higher sdk 19
		if (Build.VERSION.SDK_INT >= 21) {
			val printManager: PrintManager =  getSystemService (Context.PRINT_SERVICE) as PrintManager
			// Get a print adapter instance
			//val printAdapter: PrintDocumentAdapter = webview.createPrintDocumentAdapter (webview.title)

			// Create a print job with name and adapter instance
			//val jobName = "Koadah Web Document"
			//printManager.print (jobName, printAdapter, PrintAttributes.Builder ().build ())
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
                webview.loadUrl ("http://www.google.com/search?q=" + words)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
