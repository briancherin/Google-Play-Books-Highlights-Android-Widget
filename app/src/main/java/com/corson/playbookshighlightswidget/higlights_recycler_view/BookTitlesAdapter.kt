package com.corson.playbookshighlightswidget.higlights_recycler_view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.HighlightsBrowser
import com.corson.playbookshighlightswidget.R
import com.corson.playbookshighlightswidget.model.BookMetadata

// https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
class BookTitlesAdapter(private val bookList: ArrayList<BookMetadata>) :
        RecyclerView.Adapter<BookTitlesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val cardView: CardView

        init {
            textView = view.findViewById(R.id.bookTitleRowTextView)
            cardView = view.findViewById(R.id.bookTitleRowCardView)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.book_title_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = bookList[position].title

        val context = holder.itemView.context

        holder.cardView.setOnClickListener {
            startActivity(context, Intent(context, HighlightsBrowser::class.java).apply {
                putExtra("bookTitle", bookList[position].title)
            }, null)
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}