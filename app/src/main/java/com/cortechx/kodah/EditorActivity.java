package com.cortechx.kodah;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class EditorActivity extends Activity
{
    WebView webview;
    ViewFlipper flipper;
    EditText html, javascript, css;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_editor);

        webview = (WebView) findViewById (R.id.editorwebView);
        flipper = (ViewFlipper) findViewById (R.id.editorViewFlipper);
        html = (EditText) findViewById (R.id.htmlEditText);
        javascript = (EditText) findViewById (R.id.jsEditText);
        css = (EditText) findViewById (R.id.cssEditText);

        webview.getSettings ().setJavaScriptEnabled (true);
        webview.getSettings ().setLoadWithOverviewMode (true);
        webview.getSettings ().setBuiltInZoomControls (true);
        webview.getSettings ().setUseWideViewPort (true);
        webview.setWebViewClient (new webclient ());
        webview.setWebChromeClient (new MyWebChromeClient ());

        ArrayList<String> a=new ArrayList<String> ();
        a.add ("<html>\n\n</html>");
        a.add ("<div>\n\n</div>");
        a.add ("<head>\n\n</head>");
        a.add ("<body>\n\n</body>");
        a.add ("<script>\n\n</script>");
        a.add ("<style>\n\n</style>");
        a.add ("<h1></h1>");
        a.add ("<h2></h2>");
        a.add ("<p></p>");
        a.add ("<a></a>");
        a.add ("<img>");
        a.add ("<ul>\n\n</ul>");
        a.add ("<ol>\n\n</ol>");

        final ArrayAdapter<String> aa = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, a);

        AddTabs ();
        setTemplate ();
    }

    public void AddTabs()
    {
        getActionBar ().setNavigationMode (ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab htmlTab = getActionBar ().newTab ();
        ActionBar.Tab jsTab = getActionBar ().newTab ();
        ActionBar.Tab cssTab = getActionBar ().newTab ();
        ActionBar.Tab webTab = getActionBar ().newTab ();

        htmlTab.setText ("HTML");
        jsTab.setText ("JS");
        cssTab.setText ("CSS");
        webTab.setText ("DESIGN");

        htmlTab.setTabListener (new ActionBar.TabListener (){

            @Override
            public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
                flipper.setDisplayedChild (0);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }

            @Override
            public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }
        });

        jsTab.setTabListener (new ActionBar.TabListener (){

            @Override
            public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
                flipper.setDisplayedChild (1);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }

            @Override
            public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }
        });

        cssTab.setTabListener (new ActionBar.TabListener (){

            @Override
            public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
                flipper.setDisplayedChild (2);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }

            @Override
            public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }
        });

        webTab.setTabListener (new ActionBar.TabListener (){

            @Override
            public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
                flipper.setDisplayedChild (3);
                String style = "<style>" + css.getText ().toString () + "</style>";
                String html_css = html.getText ().toString ().replaceFirst ("<head>", "<head>" + style);
                Log.d ("html", html_css);
                webview.loadData (html_css, "text/html", "UTF-8");
                webview.loadUrl ("javascript:" + javascript.getText ().toString ());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }

            @Override
            public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
            {
                // TODO: Implement this method
            }
        });

        getActionBar ().addTab (htmlTab);
        getActionBar ().addTab (jsTab);
        getActionBar ().addTab (cssTab);
        getActionBar ().addTab (webTab);
    }

    public void setTemplate()
    {
        html.setText ("<html>\n  <head>\n\n   </head>\n   <body>\n\n   </body>\n</html>");
        javascript.setText ("alert('This is an alert from UBrowser');");
        css.setText ("body {\ncolor:blue;\nbackground:black;\n}");
    }

    public class webclient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            // TODO: Implement this method
            view.loadUrl (url);

            if (url.contains (".mp3")) {
                Intent intent = new Intent (Intent.ACTION_VIEW);
                intent.setDataAndType (Uri.parse (url), "audio/*");
                startActivity (intent);
                return true; }
            else if (url.contains (".mp4") || url.contains (".3gp")) {
                Intent intent = new Intent (Intent.ACTION_VIEW);
                intent.setDataAndType (Uri.parse (url), "video/*");
                startActivity (intent);
                return true; }
            else if (url.contains ("youtube.com")) {
                startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (url)));
                return true;
            }

            return true;
        }
    }

    public class MyWebChromeClient extends WebChromeClient
    {
        //Handle javascript alerts:
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
        {

            new AlertDialog.Builder (EditorActivity. this).
                    setTitle (view.getUrl () + " says:").
                    setMessage (message).
                    setPositiveButton ("OK", new DialogInterface.OnClickListener (){
                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {
                            // TODO: Implement this method
                            result.confirm ();
                        }
                    }).
                    show ();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage)
        {
            // TODO: Implement this method
            String logmessage = consoleMessage.message ();
            return super.onConsoleMessage (consoleMessage);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO: Implement this method
        getMenuInflater ().inflate (R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        // TODO: Implement this method
        switch (item.getItemId ()) {
            case R.id.saveProject:
                final EditText location = new EditText (this), name = new EditText (this);
                location.setText ("/storage/emulated/0/UBrowser/Projects/");
                name.setText ("New Project");
                LinearLayout ll = new LinearLayout (this);
                ll.setOrientation (LinearLayout.VERTICAL);
                ll.addView (location); ll.addView (name);
                new AlertDialog.Builder (this).setTitle ("Save").
                        setView (ll).
                        setPositiveButton ("Save", new DialogInterface.OnClickListener (){

                            @Override
                            public void onClick(DialogInterface p1, int p2)
                            {
                                // TODO: Implement this method
                                try {
                                    //MAKE THE PROJECT FOLDER
                                    File myDir= new File (location.getText ().toString () + name.getText ().toString ());
                                    if (!myDir.exists ()) {
                                        myDir.mkdirs ();
                                    }

                                    //SAVE INDEX.HTML
                                    File htmlfile = new File (myDir, "index.html");
                                    FileWriter writer = new FileWriter (htmlfile);
                                    writer.write (html.getText ().toString ());
                                    writer.flush ();
                                    writer.close ();

                                    //SAVE SCRIPTS.JS
                                    File jsfile = new File (myDir, "scripts.js");
                                    FileWriter writer1 = new FileWriter (jsfile);
                                    writer1.write (javascript.getText ().toString ());
                                    writer1.flush ();
                                    writer1.close ();

                                    //SAVE STYLES.CSS
                                    File cssfile = new File (myDir, "styles.css");
                                    FileWriter writer2 = new FileWriter (cssfile);
                                    writer2.write (css.getText ().toString ());
                                    writer2.flush ();
                                    writer2.close ();

                                    Toast.makeText (EditorActivity.this, "Saved as " + myDir.getPath (), Toast.LENGTH_SHORT).show ();
                                }
                                catch (IOException e) {
                                    e.printStackTrace ();
                                    Toast.makeText (EditorActivity.this, "Failed", Toast.LENGTH_SHORT).show ();
                                }
                            }
                        }).
                        setNegativeButton ("Cancel", new DialogInterface.OnClickListener (){

                            @Override
                            public void onClick(DialogInterface p1, int p2)
                            {
                                // TODO: Implement this method
                                p1.cancel ();
                            }
                        }).show ();
                break;
        }
        return super.onMenuItemSelected (featureId, item);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder (EditorActivity. this).
                    setTitle ("EXIT").
                    setMessage ("Are you sure You want to leave?").
                    setPositiveButton ("Yes", new DialogInterface.OnClickListener (){
                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {
                            // TODO: Implement this method
                            finish ();
                        }
                    }).setNegativeButton ("No", null).
                    show ();
        }
        return true;
    }
}
