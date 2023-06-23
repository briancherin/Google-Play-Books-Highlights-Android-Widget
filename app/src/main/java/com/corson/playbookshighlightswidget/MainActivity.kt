package com.corson.playbookshighlightswidget

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val REQ_ONE_TAP: Int = 2
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            loadHighlightsBrowser()
        } else {
            oneTapClient = Identity.getSignInClient(this)
            signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build())
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(BuildConfig.DEFAULT_WEB_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(false)
                        .build())
//            .setAutoSelectEnabled(true)
                .build()
        }






        findViewById<Button>(R.id.signInButton).setOnClickListener { _ -> createSignInIntent() }


    }

    fun createSignInIntent() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP, null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Log.d(TAG, e.localizedMessage)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    println("HI message 0")
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    println("HI message 1")
                    val idToken = credential.googleIdToken

                    println("HI22")

                    when {
                        idToken != null -> {
                            Log.d(TAG, "Got ID token.")

                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success
                                        val user = auth.currentUser
                                        println("Got user! Email: ${user?.email}")

                                        loadHighlightsBrowser()
                                    } else {
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }

                                }

                        }
                        else -> {
                            // Shouldn't happen
                            Log.d(TAG, "No ID token!")
                        }
                    }

                } catch (e: ApiException) {
                    println("ERROR")
                    Log.d(TAG, e.localizedMessage)
                }
            }
        }
    }


    fun loadHighlightsBrowser() {
        startActivity(Intent(this, HighlightsBrowser::class.java))
    }



}