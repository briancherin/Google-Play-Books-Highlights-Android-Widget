package com.corson.playbookshighlightswidget

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookTitlesAdapter
import com.corson.playbookshighlightswidget.client.firebase.FirebaseBookHighlightsEntity
import com.corson.playbookshighlightswidget.client.firebase.FirebaseDatabaseHelper
import com.corson.playbookshighlightswidget.client.firebase.FirebaseFunctionsHelper
import com.corson.playbookshighlightswidget.client.room_cache.CacheHelper
import com.corson.playbookshighlightswidget.model.BookMetadata
import com.corson.playbookshighlightswidget.repository.HighlightsRepository
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.random.Random

class TitlesBrowser : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val databaseHelper: FirebaseDatabaseHelper = FirebaseDatabaseHelper()

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBoxEditText: EditText
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var bookList: ArrayList<BookMetadata> // Should stay constant with initial list
    private lateinit var filteredBookList: ArrayList<BookMetadata> // Reference fed to recycler view

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titles_browser)

        val highlightsRepository = HighlightsRepository(applicationContext)


        findViewById<Button>(R.id.signOutButton).setOnClickListener { 
            Firebase.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

        recyclerView = findViewById(R.id.bookTitlesRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val context = this

        dialogBuilder = AlertDialog.Builder(context)
        alertDialog = dialogBuilder.create()
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", DialogInterface.OnClickListener {
                    _, _ ->
        })

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            bookList =  ArrayList()
            filteredBookList =  ArrayList()
            recyclerView.adapter = BookTitlesAdapter(filteredBookList)

            lifecycleScope.launch {
                try {
                    val books = highlightsRepository.getSortedBookList()
                    filteredBookList.clear()
                    filteredBookList.addAll(books)
                    bookList.addAll(books)
                    recyclerView.adapter?.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.w(TAG, "Erorr loading book titles in TitlesBrowser", e)
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
                    R.id.nav_refresh_highlights -> {
                        refreshHighlights(context)
                    }
                    R.id.nav_clear_cache -> {
                        lifecycleScope.launch {
                            CacheHelper(applicationContext).clearCache()
                        }
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

    private fun refreshHighlights(context: Context) {
        lifecycleScope.launch {
            try {
                showAlert("Refreshing highlights...")
                // Refresh highlights in backend
                val taskId = Random.nextInt(1000000)
                try {
                    val result = FirebaseFunctionsHelper().refreshHighlights(taskId)
                    showAlert("Successfully refreshed highlights.")
                    alertDialog.getButton(Dialog.BUTTON_POSITIVE).visibility = View.VISIBLE
                } catch (e: Exception) {
                    showAlert("Failed to refresh highlights.")
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                        Log.w(TAG, "Failed to refresh highlights (Firebase error ${code}): $details")
                    } else {
                        Log.w(TAG, "Failed to refresh highlights: ", e)
                    }
                }

                databaseHelper.listenForTaskProgress(taskId) { message ->
                    showAlert(message)
                }


            } catch (e: Exception) {
                Log.w(TAG, "Error refreshing book highlights", e)
            }
        }

    }

    private fun showAlert(message: String) {
        alertDialog.setMessage(message)
        alertDialog.show()
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).visibility = View.GONE
    }


}