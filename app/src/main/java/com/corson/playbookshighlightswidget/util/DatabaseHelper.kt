package com.corson.playbookshighlightswidget.util

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import com.corson.playbookshighlightswidget.MainActivity
import com.corson.playbookshighlightswidget.higlights_recycler_view.BookTitlesAdapter
import com.corson.playbookshighlightswidget.model.BookHighlights
import com.corson.playbookshighlightswidget.model.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Tag
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

class DatabaseHelper {

    private val database = Firebase.database.reference

    fun fetchBookHighlights(callback: (ArrayList<BookHighlights>) -> Unit, onError: (Throwable) -> Unit)  {

        if (Firebase.auth.currentUser == null) {
            onError(Throwable("User not logged in"))
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
                    callback(bookList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "loadHighlights:onCancelled", error.toException())
                    onError(error.toException())
                }
            })

        }
    }

    fun listenForTaskProgress(taskId: Int, onProgress: (message: String) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid

        val taskRef = database
            .child("users")
            .child(userId!!)
            .child("tasks")
            .child(taskId.toString())

        taskRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val task = snapshot.getValue(Task::class.java)

                if (task != null && task.status == "progress") {
                    onProgress(task.description)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "taskProgress:onCancelled", error.toException())
            }
        })

    }
}