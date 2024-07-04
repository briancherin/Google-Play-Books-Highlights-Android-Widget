package com.corson.playbookshighlightswidget.client.room_cache

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.corson.playbookshighlightswidget.client.firebase.FirebaseBookHighlightsEntity
import com.corson.playbookshighlightswidget.model.BookMetadata
import com.corson.playbookshighlightswidget.model.Highlight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CacheHelper(private val context: Context) {

    companion object {
        const val PREFS_NAME = "HighlightWidgetPrefs"
        const val LAST_UPDATE_TIME = "lastUpdateTime"
    }

    /*suspend fun fetchBookHighlightsFromCache(): ArrayList<FirebaseBookHighlightsEntity> {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        val result = ArrayList<FirebaseBookHighlightsEntity>()

        val booksWithHighlights = db.bookHighlightsDao().getAllBookHighlights()
        booksWithHighlights.forEach { bookWithHighlights ->
            val bookHighlights = FirebaseBookHighlightsEntity(
                title = bookWithHighlights.bookHighlights.title,
                dateModified = bookWithHighlights.bookHighlights.dateModified,
                quotes = ArrayList(bookWithHighlights.highlights.map { highlight ->
                    highlightEntityToApplicationObject(highlight)
                })
            )
            result.add(bookHighlights)
        }

        return result
    }*/


    suspend fun getRandomHighlight(): Highlight? {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        return db.highlightDao().getRandomHighlight().let { entity ->
            if (entity != null) highlightEntityToApplicationObject(entity) else null
        }
    }

    suspend fun getAllHighlights(): List<Highlight> {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        return db.highlightDao().getAllHighlights().map { entity ->
            highlightEntityToApplicationObject(entity)
        }
    }


    suspend fun getSortedBookList(): List<BookMetadata> {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        return db.bookHighlightsDao().getBookTitlesSortedByDate().map {
            BookMetadata(title=it.title, dateModified = it.dateModified)
        }
    }

    suspend fun getHighlightsForBookTitle(title: String): List<Highlight> {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        return db.highlightDao().getHighlightsForBookTitle(title).map { entity ->
            highlightEntityToApplicationObject(entity)
        }
    }

    suspend fun getBookCount(): Number {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        return db.bookHighlightsDao().getBookCount();
    }


    suspend fun saveBookHighlightsToCache(bookList: ArrayList<FirebaseBookHighlightsEntity>) {
        withContext(Dispatchers.IO) {
            try {
                println("Saving book highlights to cache.")
                val db =
                    Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

                // Clear existing cache
                clearCache()
                println("Cleared existing cache.")

                // Insert new data
                bookList.forEach { book ->
                    val bookId = db.bookHighlightsDao().insert(
                        BookHighlightsEntity(
                            title = book.title ?: "",
                            dateModified = book.dateModified ?: 0L
                        )
                    )
                    book.quotes?.let { quotes ->
                        val highlightEntities = quotes.map { quote ->
                            HighlightEntity(
                                bookId = bookId.toInt(),
                                bookLink = quote.bookLink,
                                bookTitle = quote.bookTitle ?: "",
                                dateHighlighted = quote.dateHighlighted ?: "",
                                highlightColor = quote.highlightColor ?: "",
                                highlightNotes = quote.highlightNotes,
                                quoteIsFavorited = quote.quoteIsFavorited ?: false,
                                quoteText = quote.quoteText ?: ""
                            )
                        }
                        db.highlightDao().insertAll(highlightEntities)
                    }
                }
                println("Added new books to cache")

                // Finally, update the last update time
                saveLastUpdateTime()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving book highlights to cache", e)
            }
        }
    }

    suspend fun clearCache() {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
        // Clear existing data in the database
        db.highlightDao().deleteAllHighlights()
        db.bookHighlightsDao().deleteAllBookHighlights()
    }

    // Should update cache if it's been 7 days since the last update
    fun shouldUpdateCache(): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastUpdateTime = prefs.getLong(LAST_UPDATE_TIME, 0)
        val weekInMillis = TimeUnit.DAYS.toMillis(7)
        return System.currentTimeMillis() - lastUpdateTime > weekInMillis
    }


    private fun saveLastUpdateTime() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putLong(LAST_UPDATE_TIME, System.currentTimeMillis())
        prefs.apply()
    }

    private fun highlightEntityToApplicationObject(entity: HighlightEntity): Highlight {
        return Highlight(
            bookLink = entity.bookLink,
            bookTitle = entity.bookTitle,
            dateHighlighted = entity.dateHighlighted,
            highlightColor = entity.highlightColor,
            highlightNotes = entity.highlightNotes,
            quoteIsFavorited = entity.quoteIsFavorited,
            quoteText = entity.quoteText
        )
    }


}