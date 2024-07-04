package com.corson.playbookshighlightswidget.repository

import android.content.Context
import androidx.room.Room
import com.corson.playbookshighlightswidget.client.firebase.FirebaseDatabaseHelper
import com.corson.playbookshighlightswidget.client.firebase.FirebaseFunctionsHelper
import com.corson.playbookshighlightswidget.client.room_cache.AppDatabase
import com.corson.playbookshighlightswidget.client.room_cache.CacheHelper
import com.corson.playbookshighlightswidget.model.BookMetadata
import com.corson.playbookshighlightswidget.model.Highlight
import kotlin.collections.ArrayList
import kotlin.random.Random

class HighlightsRepository(private val context: Context) {

    private val cacheHelper = CacheHelper(context)
    private val firebaseDatabase = FirebaseDatabaseHelper()

    suspend fun getRandomHighlight(): Highlight? {
        updateCacheIfStale()
        return cacheHelper.getRandomHighlight()
    }

    suspend fun getSortedBookList(): List<BookMetadata> {
        updateCacheIfStale()
        return cacheHelper.getSortedBookList()
    }

    suspend fun getHighlightsForBookTitle(title: String): ArrayList<Highlight> {
        updateCacheIfStale()
        return ArrayList(cacheHelper.getHighlightsForBookTitle(title))
    }

    suspend fun getAllHighlights(): ArrayList<Highlight> {
        updateCacheIfStale()
        return ArrayList(cacheHelper.getAllHighlights())
    }

    private suspend fun updateCacheIfStale() {
        val numBooksInCache = cacheHelper.getBookCount()
        println("Cache book count: $numBooksInCache")
        if (cacheHelper.shouldUpdateCache() || numBooksInCache == 0) {
            println("Updating cache from remote")
            val taskId = Random.nextInt(10000)
            try {
                val result = FirebaseFunctionsHelper().refreshHighlights(taskId)
                println("Got firebase result: $result")
                val latestHighlights = firebaseDatabase.fetchBookHighlights()
                println("Fetched latest highlights from firebase db. count = ${latestHighlights.size}")
                cacheHelper.saveBookHighlightsToCache(latestHighlights)
            } catch (e: Exception) {
                println("Failed to refresh highlights: ${e.message}")
            }
            FirebaseDatabaseHelper().listenForTaskProgress(taskId) { message ->
                println("Update from Firebase function task $taskId: $message")
            }
        }
    }
}