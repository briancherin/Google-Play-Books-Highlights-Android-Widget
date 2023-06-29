package com.corson.playbookshighlightswidget.util

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlin.random.Random
import kotlin.random.nextInt

class FirebaseFunctionsHelper {
    fun refreshHighlights(): Task<String> {
        val taskId = Random.nextInt(1000000)

        val data = hashMapOf(
            "taskId" to taskId
        )

        return Firebase.functions
            .getHttpsCallable("updateUserHighlights")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data.toString()
                println("Finished refreshHighlights task ${taskId}: ${result}")
                result
            }
    }
}