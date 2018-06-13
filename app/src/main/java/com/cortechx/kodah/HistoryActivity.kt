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

class HistoryActivity : AppCompatActivity() {

    var editor: SharedPreferences.Editor? = null
    var history = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val prefs = getSharedPreferences("Kodah_Data", Context.MODE_PRIVATE)
        editor = prefs.edit()

        try {
            history = ObjectSerializer.deserialize(prefs.getString("HistoryList", ObjectSerializer.serialize(ArrayList<String>()))) as ArrayList<String>
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        val arrayAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                history)
        historyListView.adapter = arrayAdapter

        historyListView.setOnItemClickListener { adapterView, _, i, _ ->
            val returnIntent = Intent()
            returnIntent.putExtra("url_clicked", arrayAdapter.getItem(i).toString())
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
                editor!!.putBoolean("HistoryEnabled", true)
            }
            editor!!.commit()
        }
    }
}
