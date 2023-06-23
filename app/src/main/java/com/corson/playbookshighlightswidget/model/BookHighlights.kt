package com.corson.playbookshighlightswidget.model

// Object containing a list of highlights for a particular book
data class BookHighlights (
    var title: String? = "",
    var quotes: ArrayList<Highlight>? = null,
    var dateModified: Long? = 0 // UNIX timestamp, when the book file was last updated
)