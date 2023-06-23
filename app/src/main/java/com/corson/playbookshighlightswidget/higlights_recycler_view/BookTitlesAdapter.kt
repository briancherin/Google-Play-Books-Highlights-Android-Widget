package com.corson.playbookshighlightswidget.higlights_recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.corson.playbookshighlightswidget.R

// https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
class BookTitlesAdapter(private val titlesList: ArrayList<String>) :
        RecyclerView.Adapter<BookTitlesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.bookTitleRowTextView)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.book_title_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = titlesList[position]
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}