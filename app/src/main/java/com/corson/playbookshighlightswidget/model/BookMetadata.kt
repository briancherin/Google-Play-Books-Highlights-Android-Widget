package com.corson.playbookshighlightswidget.model

data class BookMetadata (
    var title: String? = "",
    var dateModified: Long? = 0 // UNIX timestamp, when the book file was last updated
)