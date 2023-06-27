package com.corson.playbookshighlightswidget.util

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import com.corson.playbookshighlightswidget.MainActivity
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookTitlesAdapter
import com.corson.playbookshighlightswidget.model.BookHighlights
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

class DatabaseHelper {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fetchBookHighlights(): ArrayList<BookHighlights> {
        return suspendCancellableCoroutine { continuation ->
            val database = Firebase.database.reference

            if (Firebase.auth.currentUser == null) {
                continuation.cancel(Throwable("User not logged in"))
            } else {
                val userId = Firebase.auth.currentUser?.uid

                val userHighlightsRef = database
                    .child("users")
                    .child(userId!!)
                    .child("highlights")
                    .orderByChild("dateModified") //Sort by latest added

                val bookList = ArrayList<BookHighlights>()

                userHighlightsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (highlightSnapshot in snapshot.children) {
                            val h: BookHighlights? =
                                highlightSnapshot.getValue(BookHighlights::class.java)

                            if (h?.title != null) {
                                bookList.add(h)
                            }
                        }
                        bookList.reverse() // Reverse sorted order
                        continuation.resume(bookList, null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "loadHighlights:onCancelled", error.toException())
                        continuation.cancel(Throwable(error.toException()))
                    }
                })

            }
        }
    }
}