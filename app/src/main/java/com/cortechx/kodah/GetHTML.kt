package com.cortechx.kodah

/**
 * Created by Shawn Grant @ Cortechx on 8/6/2018.
 */
import android.*
import android.os.*

import java.io.*
import java.net.*
import java.util.concurrent.*
import android.util.*

class GetHTML {
    fun GetHTML() {

    }

    fun getHtml(urlToRead: String): String? {
        var html: String? = null

        try {
            html = MyTask().execute(urlToRead).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return html
    }

    inner class MyTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(p1: Array<String>): String {
            // TODO: Implement this method
            var content = ""
            val url: URL
            var stream: InputStream? = null
            val br: BufferedReader
            var line: String = ""
            try {
                url = URL(p1[0])
                stream = url.openStream() // throws an IOException
                br = BufferedReader(InputStreamReader(stream!!))

                while ((line == br.readLine()) != null) {
                    println(line)
                    content += line + "\n"
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    if (stream != null) stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            Log.d("HTMLContent", content)
            return content
        }

    }


}