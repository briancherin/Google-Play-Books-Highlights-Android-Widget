package com.corson.playbookshighlightswidget.client.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseFunctionsHelper {
    suspend fun refreshHighlights(taskId: Int): String? = suspendCoroutine { continuation ->

        val data = hashMapOf(
            "taskId" to taskId
        )

        Firebase.functions
            .getHttpsCallable("updateUserHighlights")
            .call(data)
            .addOnSuccessListener { result ->
                val result = result?.data.toString()
                println("Finished refreshHighlights task ${taskId}: ${result}")
                continuation.resume(result)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }

    }
}