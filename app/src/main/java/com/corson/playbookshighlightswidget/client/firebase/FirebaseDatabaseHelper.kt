package com.corson.playbookshighlightswidget.client.firebase

import android.content.ContentValues
import android.util.Log
import com.corson.playbookshighlightswidget.model.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseHelper {

    private val database = Firebase.database.reference

    suspend fun fetchBookHighlights(): ArrayList<FirebaseBookHighlightsEntity> = suspendCoroutine{ continuation ->
        if (Firebase.auth.currentUser == null) {
            continuation.resumeWithException(Throwable("User not logged in"))
        } else {
            val userId = Firebase.auth.currentUser?.uid

            val userHighlightsRef = database
                .child("users")
                .child(userId!!)
                .child("highlights")
                .orderByChild("dateModified") //Sort by latest added

            val bookList = ArrayList<FirebaseBookHighlightsEntity>()

            userHighlightsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (highlightSnapshot in snapshot.children) {
                        val h: FirebaseBookHighlightsEntity? =
                            highlightSnapshot.getValue(FirebaseBookHighlightsEntity::class.java)

                        if (h?.title != null) {
                            bookList.add(h)
                        }
                    }
                    bookList.reverse() // Reverse sorted order

                    // Return highlights to caller
                    continuation.resume(bookList);
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "loadHighlights:onCancelled", error.toException())
                    continuation.resumeWithException(error.toException())
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