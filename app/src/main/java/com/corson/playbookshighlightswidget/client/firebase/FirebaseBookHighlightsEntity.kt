package com.corson.playbookshighlightswidget.client.firebase

import com.corson.playbookshighlightswidget.model.Highlight

// Object containing a list of highlights for a particular book
data class FirebaseBookHighlightsEntity (
    var title: String? = "",
    var quotes: ArrayList<Highlight>? = null,
    var dateModified: Long? = 0 // UNIX timestamp, when the book file was last updated
)