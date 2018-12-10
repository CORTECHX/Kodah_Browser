package com.cortechx.kodah

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.IOException
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.content_main.*
import android.app.Activity
import android.content.Intent
import android.widget.CompoundButton
import android.widget.Toast

class HistoryActivity : AppCompatActivity() {

    var editor: SharedPreferences.Editor? = null
    var historyTitles = ArrayList<String>()
    var historyUrls = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val prefs = getSharedPreferences("Kodah_Data", Context.MODE_PRIVATE)
        editor = prefs.edit()

        try {
            historyTitles = ObjectSerializer.deserialize(prefs.getString("HistoryTitles", ObjectSerializer.serialize(ArrayList<String>()))) as ArrayList<String>
            historyUrls = ObjectSerializer.deserialize(prefs.getString("HistoryUrls", ObjectSerializer.serialize(ArrayList<String>()))) as ArrayList<String>
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        val arrayAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                historyTitles)
        historyListView.adapter = arrayAdapter

        historyListView.setOnItemClickListener { adapterView, _, i, _ ->
            val returnIntent = Intent()
            returnIntent.putExtra("url_clicked", historyUrls[i])
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        if (prefs.getBoolean("HistoryEnabled", true)){
            historyToggle.toggle()
        }

        historyToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                editor!!.putBoolean("HistoryEnabled", true)
            } else {
                // The toggle is disabled
                editor!!.putBoolean("HistoryEnabled", false)
            }
            editor!!.commit()
        }
    }
}
