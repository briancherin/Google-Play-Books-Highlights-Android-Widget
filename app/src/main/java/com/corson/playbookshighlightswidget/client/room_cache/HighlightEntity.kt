package com.corson.playbookshighlightswidget.client.room_cache

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "book_highlights")
data class BookHighlightsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val dateModified: Long  // UNIX timestamp
)

@Entity(
    tableName = "highlights",
    foreignKeys = [
        ForeignKey(
            entity = BookHighlightsEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: Int,  // Link to BookHighlightsEntity
    val bookLink: String?,
    val bookTitle: String,
    val dateHighlighted: String,
    val highlightColor: String,
    val highlightNotes: String?,
    val quoteIsFavorited: Boolean,
    val quoteText: String
)
