package com.corson.playbookshighlightswidget.higlights_recycler_view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.R
import com.corson.playbookshighlightswidget.model.Highlight

// https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
class BookHighlightsAdapter(private val highlightsList: ArrayList<Highlight>) :
        RecyclerView.Adapter<BookHighlightsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val textView: TextView
                val cardView: CardView

                init {
                        textView = view.findViewById(R.id.bookHighlightRowTextView)
                        cardView = view.findViewById(R.id.bookHighlightRowCardView)
                }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.book_highlight_row_item, viewGroup, false)

                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.textView.text = highlightsList[position].quoteText
                holder.cardView.setCardBackgroundColor(Color.parseColor(highlightsList[position].highlightColor))
        }

        override fun getItemCount(): Int {
                return highlightsList.size
        }
}