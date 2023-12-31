package com.corson.playbookshighlightswidget.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Highlight (
    var bookLink: String? = "",
    var bookTitle: String? = "",
    var dateHighlighted: String? = "",
    var highlightColor: String? = "",
    var highlightNotes: String? = "",
    var quoteIsFavorited: Boolean? = false,
    var quoteText: String? = "",
) : Parcelable

/*
*
* 	bookLink:"....."
	bookTitle:"A Canticle for Leibowitz"
	dateHighlighted:"March 30, 2018"
	highlightColor:"#fde096"
	highlightNotes:""
	quoteIsFavorited:false
	quoteText:"He had finally learned that house cat did not mean cat house,"

*
* */