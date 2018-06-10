package com.cortechx.kodah

/**
 * Created by Shawn Grant @ Cortechx on 8/6/2018.
 */
import android.os.*

import java.net.*
import java.util.concurrent.*
import android.util.*
import java.util.*

class GetHTML {

    fun getHtml(urlToRead: String): String? {
        var html = ""

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
            var connection: URLConnection? = null
            try {
                connection = URL(p1[0]).openConnection()
                val scanner = Scanner(connection!!.getInputStream())
                scanner.useDelimiter("\\Z")
                content = scanner.next()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            //Log.d("HTML",content)
            return  content
        }

    }


}