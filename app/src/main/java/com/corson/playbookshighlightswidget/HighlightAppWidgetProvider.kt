package com.corson.playbookshighlightswidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.corson.playbookshighlightswidget.client.room_cache.CacheHelper
import com.corson.playbookshighlightswidget.model.Highlight
import com.corson.playbookshighlightswidget.client.firebase.FirebaseDatabaseHelper
import com.corson.playbookshighlightswidget.client.firebase.FirebaseFunctionsHelper
import com.corson.playbookshighlightswidget.repository.HighlightsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class HighlightAppWidgetProvider: AppWidgetProvider() {

    val ACTION_REFRESH = "ACTION_REFRESH"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

            appWidgetIds.forEach { appWidgetId ->

//                println("IN onUPDATE FOR WIDGET $appWidgetId")

                refreshHighlight(context, appWidgetId)
            }
    }

    override fun onReceive(context: Context, intent: Intent) {
        println("Widget intent received: ${intent.action}")
        println("Widget intent extra: ${intent.getIntExtra("widgetId", -1)}")
        when (intent.action) {
            ACTION_REFRESH -> refreshHighlight(context, intent.extras!!.getInt("widgetId"))

        }

        super.onReceive(context, intent)
    }

    private fun refreshHighlight(context: Context, appWidgetId: Int) {
        val views: RemoteViews = RemoteViews(context.packageName, R.layout.highlight_widget1)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val highlightsRepository = HighlightsRepository(context)
                val randomHighlight = highlightsRepository.getRandomHighlight()

                if (randomHighlight != null) {
                    updateWidgetView(context, appWidgetId, views, randomHighlight)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing widget highlight", e)
            }
        }
    }

    private fun updateWidgetView(context: Context, appWidgetId: Int, views: RemoteViews, randomHighlight: Highlight) {
        views.setTextViewText(R.id.widget_highlight_text, randomHighlight.quoteText)
        views.setTextViewText(R.id.widget_highlight_date, randomHighlight.dateHighlighted)
        views.setTextViewText(R.id.widget_highlight_title, randomHighlight.bookTitle)
        views.setViewVisibility(R.id.widget_highlight_has_note, if (randomHighlight.highlightNotes.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE)
        views.setInt(R.id.widget_highlight_text, "setBackgroundColor", Color.parseColor(randomHighlight.highlightColor))

        val refreshIntent = Intent(context, HighlightAppWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
        }
        refreshIntent.putExtra("widgetId", appWidgetId)

        views.setOnClickPendingIntent(R.id.widget_button_refresh, PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))

        val openHighlightIntent = Intent(context, SingleHighlight::class.java).apply {
            action = "singleHighlight-${System.currentTimeMillis()}"
            putExtra("highlight", randomHighlight)
        }

        views.setOnClickPendingIntent(R.id.widget_relative_layout, PendingIntent.getActivity(
            context,
            appWidgetId,
            openHighlightIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views)
    }
}