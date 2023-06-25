package com.corson.playbookshighlightswidget

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookTitlesAdapter
import com.corson.playbookshighlightswidget.model.BookHighlights
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TitlesBrowser : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBoxEditText: EditText

    private lateinit var bookList: ArrayList<BookHighlights> // Should stay constant with initial list
    private lateinit var filteredBookList: ArrayList<BookHighlights> // Reference fed to recycler view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titles_browser)

        findViewById<Button>(R.id.signOutButton).setOnClickListener { 
            Firebase.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

        recyclerView = findViewById(R.id.bookTitlesRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = Firebase.database.reference

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            val userId = Firebase.auth.currentUser?.uid

            val userHighlightsRef = database
                .child("users")
                .child(userId!!)
                .child("highlights")
                .orderByChild("dateModified") //Sort by latest added

            bookList = ArrayList()
            filteredBookList =  ArrayList()

            userHighlightsRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (highlightSnapshot in snapshot.children) {
                        val h: BookHighlights? = highlightSnapshot.getValue(BookHighlights::class.java)

                        if (h?.title != null) {
                            bookList.add(h)
                        }
                        bookList.reverse() // Reverse sorted order
                        println("Got book title: ${h?.title}")
                    }

                    filteredBookList.addAll(bookList)

                    recyclerView.adapter = BookTitlesAdapter(filteredBookList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "loadHighlights:onCancelled", error.toException())
                }
            })

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.menuActionSearch)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search book title..."


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                filterTitles(p0)
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                filterTitles(p0)
                return false
            }
        })

        return true
    }

    private fun filterTitles(query: String?) {

        filteredBookList.clear()
        filteredBookList.addAll(bookList.filter { it.title != null && it.title!!.lowercase().contains(query!!.lowercase())  })

        recyclerView.adapter?.notifyDataSetChanged()
    }
}