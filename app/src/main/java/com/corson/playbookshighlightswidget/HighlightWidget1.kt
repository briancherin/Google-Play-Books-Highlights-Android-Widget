package com.corson.playbookshighlightswidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */

val ACTION_REFRESH = "ACTION_REFRESH"

class HighlightWidget1 : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            println("In Widget. Id: $appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {

        println("Widget1 intent received: ${intent.action}")

        when (intent.action) {
            ACTION_REFRESH -> println("IN HighlightWidget1 onReceive for action_refresh")

        }

        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.highlight_widget1)
    views.setTextViewText(R.id.widget_highlight_text, "TEST_TEXT")


    val intent = Intent(context, HighlightWidget1::class.java)
    intent.action = ACTION_REFRESH

    views.setOnClickPendingIntent(
        R.id.widget_button_refresh1, PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    )

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)



}

