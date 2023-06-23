package com.corson.playbookshighlightswidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookHighlightsAdapter
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookTitlesAdapter
import com.corson.playbookshighlightswidget.model.Highlight

class HighlightsBrowser : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlights_browser)

        recyclerView = findViewById(R.id.bookHighlightsRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val highlightsList = intent.getParcelableArrayListExtra<Highlight>("highlights")

        findViewById<TextView>(R.id.highlightsBrowserBookTitleText).text = highlightsList?.get(0)?.bookTitle

        recyclerView.adapter = BookHighlightsAdapter(ArrayList(highlightsList))

        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }
    }
}