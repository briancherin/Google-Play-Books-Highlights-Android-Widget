package com.corson.playbookshighlightswidget.higlights_recycler_view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.R
import com.corson.playbookshighlightswidget.SingleHighlight
import com.corson.playbookshighlightswidget.model.Highlight

// https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
class BookHighlightsAdapter(private val highlightsList: ArrayList<Highlight?>) :
        RecyclerView.Adapter<BookHighlightsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val highlightTextTextView: TextView
                val cardView: CardView
                val dateTextView: TextView
                val noteButton: ImageButton

                init {
                        highlightTextTextView = view.findViewById(R.id.bookHighlightRowTextView)
                        cardView = view.findViewById(R.id.bookHighlightRowCardView)
                        dateTextView = view.findViewById(R.id.bookHighlightRowDate)
                        noteButton = view.findViewById(R.id.bookHighlightRowNotesButton)
                }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.book_highlight_row_item, viewGroup, false)

                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val highlight = highlightsList[position]
                val context = holder.itemView.context

                holder.highlightTextTextView.text = highlight?.quoteText
                holder.cardView.setCardBackgroundColor(Color.parseColor(highlight?.highlightColor))
                holder.dateTextView.text = highlight?.dateHighlighted

                if (highlight?.highlightNotes != null && highlight.highlightNotes!!.isNotEmpty()) {
                        holder.noteButton.visibility = View.VISIBLE
                } else {
                        holder.noteButton.visibility = View.GONE
                }


                holder.cardView.setOnClickListener {
                        startActivity(context, Intent(context, SingleHighlight::class.java).apply {
                                putExtra("highlight", highlight)
                        }, null)
                }

                holder.noteButton.setOnClickListener {
                        val builder = AlertDialog.Builder(context)
                        builder.apply {
                                setMessage(highlight?.highlightNotes)
                        }
                        builder.create()
                        builder.show()
                }
        }

        override fun getItemCount(): Int {
                return highlightsList.size
        }
}