package com.corson.playbookshighlightswidget

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HighlightsBrowser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlights_browser)

        findViewById<Button>(R.id.signOutButton).setOnClickListener { 
            Firebase.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

        // TODO: Load highlights from database

    }
}