package com.corson.playbookshighlightswidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.corson.playbookshighlightswidget.model.Highlight
import com.corson.playbookshighlightswidget.util.DatabaseHelper

class HighlightAppWidgetProvider: AppWidgetProvider() {

    val ACTION_REFRESH = "ACTION_REFRESH"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {


        DatabaseHelper().fetchBookHighlights({ bookList ->
            val highlights: List<Highlight> = bookList.flatMap { it.quotes!! }

            appWidgetIds.forEach { appWidgetId ->

                println("IN onUPDATE FOR WIDGET $appWidgetId")


                val randomHighlight = highlights.random()

                val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.highlight_widget1
                ).apply {
                    setTextViewText(R.id.widget_highlight_text, randomHighlight.quoteText)
                    setTextViewText(R.id.widget_highlight_date, randomHighlight.dateHighlighted)
                    setTextViewText(R.id.widget_highlight_title, randomHighlight.bookTitle)


                    val intent = Intent(context, HighlightAppWidgetProvider::class.java).apply {
                        action = ACTION_REFRESH;
                    }
                    println("Putting extra: $appWidgetId")
                    intent.putExtra("widgetId", appWidgetId)
                    println("Put extra: ${intent.getIntExtra("widgetId", -1)}")

                    setOnClickPendingIntent(
                        R.id.widget_button_refresh1, PendingIntent.getBroadcast(
                            context,
                            appWidgetId,
                            intent,
                            PendingIntent.FLAG_MUTABLE
                        )
                    )

                    setOnClickPendingIntent(
                        R.id.widget_relative_layout, PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, Highlight::class.java),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )


                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

        }, {e->})
    }

    override fun onReceive(context: Context, intent: Intent) {
        println("Widget intent received: ${intent.action}")
        println("Widget intent extra: ${intent.getIntExtra("widgetId", -1)}")
        when (intent.action) {
            ACTION_REFRESH -> refreshHighlight(context, intent.extras!!.getInt("widgetId"))

        }

        super.onReceive(context, intent)
    }

    fun refreshHighlight(context: Context, appWidgetId: Int) {
        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.highlight_widget1
        ).apply {

            DatabaseHelper().fetchBookHighlights({ bookList ->
                val highlights: List<Highlight> = bookList.flatMap { it.quotes!! }
                val randomHighlight = highlights.random()

                setTextViewText(R.id.widget_highlight_text, randomHighlight.quoteText)
                setTextViewText(R.id.widget_highlight_date, randomHighlight.dateHighlighted)
                setTextViewText(R.id.widget_highlight_title, randomHighlight.bookTitle)

                println("Got random highlight: ${randomHighlight.quoteText}")

                println("Attempting to update widget id ${appWidgetId}")

                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, this)
            }, {e->})
        }


    }
}