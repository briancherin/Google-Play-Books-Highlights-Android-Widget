package com.corson.playbookshighlightswidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookHighlightsAdapter
import com.corson.playbookshighlightswidget.model.Highlight

class HighlightsBrowser : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var highlightsList: ArrayList<Highlight?>
    private lateinit var filteredHighlightsList: ArrayList<Highlight?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlights_browser)

        recyclerView = findViewById(R.id.bookHighlightsRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        highlightsList = intent.getParcelableArrayListExtra<Highlight>("highlights") as ArrayList
        filteredHighlightsList = ArrayList(highlightsList)

        findViewById<TextView>(R.id.highlightsBrowserBookTitleText).text = highlightsList[0]?.bookTitle

        recyclerView.adapter = BookHighlightsAdapter(filteredHighlightsList)

        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.menuActionSearch)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search highlight text or notes..."


        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                filterHighlights(p0)
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                filterHighlights(p0)
                return false
            }
        })

        return true
    }

    private fun filterHighlights(query: String?) {
        val queryL = query?.lowercase() ?: ""

        print("queryL = $queryL")

        filteredHighlightsList.clear()
        filteredHighlightsList.addAll(highlightsList.filter { it?.quoteText!!.lowercase().contains(queryL) || it.highlightNotes!!.lowercase().contains(queryL)  })

        recyclerView.adapter?.notifyDataSetChanged()
    }
}