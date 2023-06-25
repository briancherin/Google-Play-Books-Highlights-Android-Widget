package com.corson.playbookshighlightswidget

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.corson.playbookshighlightswidget.model.Highlight

class SingleHighlight : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_highlight)

        val highlight = intent.getParcelableExtra<Highlight>("highlight")

        findViewById<TextView>(R.id.singleHighlightPageBookTitle).text = highlight?.bookTitle
        findViewById<TextView>(R.id.singleHighlightPageHighlightText).text = highlight?.quoteText
        findViewById<TextView>(R.id.singleHighlightPageDate).text = highlight?.dateHighlighted
        findViewById<TextView>(R.id.singleHighlightPageNoteText).text = highlight?.highlightNotes

        findViewById<CardView>(R.id.singleHighlightPageCardView).setCardBackgroundColor(Color.parseColor(highlight?.highlightColor))

        findViewById<ImageButton>(R.id.singleHighlightPageLinkButton).setOnClickListener {
            ContextCompat.startActivity(
                this,
                Intent(Intent.ACTION_VIEW, Uri.parse(highlight?.bookLink)),
                null
            )
        }

        if (highlight?.highlightNotes == null || highlight.highlightNotes!!.isEmpty()) {
            findViewById<TextView>(R.id.singleHighlightPageHighlightNoteLabel).visibility = View.GONE
        }

    }
}