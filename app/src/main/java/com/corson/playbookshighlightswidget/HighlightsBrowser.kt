package com.corson.playbookshighlightswidget

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookHighlightsAdapter
import com.corson.playbookshighlightswidget.model.Highlight
import com.corson.playbookshighlightswidget.client.firebase.FirebaseDatabaseHelper
import com.corson.playbookshighlightswidget.repository.HighlightsRepository
import kotlinx.coroutines.launch

class HighlightsBrowser : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var highlightsList: ArrayList<Highlight>
    private lateinit var filteredHighlightsList: ArrayList<Highlight?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlights_browser)

        recyclerView = findViewById(R.id.bookHighlightsRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val highlightsRepository = HighlightsRepository(applicationContext)

        val title = intent.getStringExtra("bookTitle")

        val showingAllBooks = title == null || title.isEmpty()

        findViewById<TextView>(R.id.highlightsBrowserBookTitleText).text = if(showingAllBooks) "All books" else title

        lifecycleScope.launch {
            try {
                if (!showingAllBooks && title?.isNotEmpty()!!) {
                    highlightsList = highlightsRepository.getHighlightsForBookTitle(title)
                } else {
                    highlightsList = highlightsRepository.getAllHighlights()
                }

                filteredHighlightsList = ArrayList(highlightsList)
                recyclerView.adapter = BookHighlightsAdapter(filteredHighlightsList, showingAllBooks)
            } catch (e: Exception) {
                Log.w(TAG, "Error fetching book highlights in HighlightsBrowser", e)
            }
        }

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