package com.corson.playbookshighlightswidget

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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

            val bookList = arrayListOf<BookHighlights>()

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

                    recyclerView.adapter = BookTitlesAdapter(bookList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "loadHighlights:onCancelled", error.toException())
                }
            })

        }


    }
}