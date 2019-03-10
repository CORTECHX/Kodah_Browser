package com.cortechx.kodah

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.tiagohm.codeview.CodeView
import br.tiagohm.codeview.Language
import br.tiagohm.codeview.Theme
import kotlinx.android.synthetic.main.activity_view_source.*

class ViewSourceActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_source)

        val url = intent.data.toString()
        val HTML_TEXT = GetHTML().getHtml(url)


        SourceView.setOnHighlightListener(object : CodeView.OnHighlightListener{
            override fun onStartCodeHighlight() {
                //
            }

            override fun onFinishCodeHighlight() {
                SourceLoad.text = url
                SourceView.code = HTML_TEXT
                SourceView.theme = Theme.MONOKAI
                SourceView.apply()
            }

            override fun onLanguageDetected(p0: Language?, p1: Int) {
                //Toast.makeText(applicationContext, "language: " + p0 + " relevance: " + p1, Toast.LENGTH_SHORT).show()
            }

            override fun onLineClicked(p0: Int, p1: String?) {
                SourceView.highlightLineNumber(p0)
            }

            override fun onFontSizeChanged(p0: Int) {
                SourceView.fontSize = p0.toFloat()
            }
        })

    }


}
