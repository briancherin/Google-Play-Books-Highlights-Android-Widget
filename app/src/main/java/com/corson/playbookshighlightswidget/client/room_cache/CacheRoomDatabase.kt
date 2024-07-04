package com.corson.playbookshighlightswidget.client.room_cache;
import androidx.room.*


@Dao
interface BookHighlightsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookHighlights: BookHighlightsEntity): Long  // Returns the row ID of the inserted item

    @Transaction
    @Query("SELECT * FROM book_highlights")
    suspend fun getAllBookHighlights(): List<BookHighlightsWithHighlights>

    @Query("SELECT * FROM book_highlights ORDER BY dateModified DESC")
    suspend fun getBookTitlesSortedByDate(): List<BookHighlightsEntity>

    @Query("SELECT COUNT(*) FROM book_highlights")
    suspend fun getBookCount(): Int

    @Query("DELETE FROM book_highlights")
    suspend fun deleteAllBookHighlights()
}

@Dao
interface HighlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(highlights: List<HighlightEntity>)

    @Query("SELECT * FROM highlights ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomHighlight(): HighlightEntity?

    @Query("SELECT * FROM highlights WHERE bookTitle = :title")
    suspend fun getHighlightsForBookTitle(title: String): List<HighlightEntity>

    @Query("SELECT * FROM highlights")
    suspend fun getAllHighlights(): List<HighlightEntity>

    @Query("DELETE FROM highlights")
    suspend fun deleteAllHighlights()
}

data class BookHighlightsWithHighlights(
    @Embedded val bookHighlights: BookHighlightsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val highlights: List<HighlightEntity>
)

@Database(entities = [BookHighlightsEntity::class, HighlightEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookHighlightsDao(): BookHighlightsDao
    abstract fun highlightDao(): HighlightDao
}