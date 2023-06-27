package com.corson.playbookshighlightswidget

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookTitlesAdapter
import com.corson.playbookshighlightswidget.model.BookHighlights
import com.corson.playbookshighlightswidget.util.DatabaseHelper
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class TitlesBrowser : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBoxEditText: EditText
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

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

        val context = this


        database = Firebase.database.reference

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {

            filteredBookList =  ArrayList()

            lifecycleScope.launch {
                try {
                    bookList = DatabaseHelper().fetchBookHighlights()
                    filteredBookList.addAll(bookList)
                    recyclerView.adapter = BookTitlesAdapter(filteredBookList)

                } catch (e: Exception) {
                    Log.w(TAG, "Error fetching book highlights", e)
                }
            }

        }

        // Set up navigation drawer
        val drawerLayout: DrawerLayout = findViewById(R.id.titlesBrowserDrawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        findViewById<NavigationView>(R.id.titlesBrowserNavigationView).setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {

            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.itemId

                when (id) {
                    R.id.nav_global_search -> {
                        ContextCompat.startActivity(
                            context,
                            Intent(context, HighlightsBrowser::class.java).apply {
                                putExtra("bookTitle", "")
                            }, null)
                    }
                    R.id.nav_show_notes_toggle -> {

                    }
                }

                return true
            }
        })

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun filterTitles(query: String?) {

        filteredBookList.clear()
        filteredBookList.addAll(bookList.filter { it.title != null && it.title!!.lowercase().contains(query!!.lowercase())  })

        recyclerView.adapter?.notifyDataSetChanged()
    }
}