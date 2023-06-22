package com.corson.playbookshighlightswidget

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import com.corson.playbookshighlightswidget.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
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


        findViewById<Button>(R.id.signOutButton).setOnClickListener { _ -> Firebase.auth.signOut() }


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

                                        findViewById<TextView>(R.id.helloScreenText).setText("Hello ${user?.displayName}")
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



/*


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res);
//        println("Got result!!!!!!")
    }

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth


        val currentUser = auth.currentUser

        println("Hello! My name is BIRD!")

        if (currentUser == null) {
            println("Current user null. opening sign in intent.")
            createSignInIntent()
        } else {
            println("Current user not null.")

            // hashtag name personalization
            findViewById<TextView>(R.id.helloScreenText).setText("Hello ${currentUser.displayName}")

            // TODO: Open highlights browser
        }

        findViewById<Button>(R.id.signOutButton).setOnClickListener({ _ -> Firebase.auth.signOut() })


    }
    override fun onStart() {
        super.onStart()


    }


    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        println("Launching signInIntent")

        signInLauncher.launch(signInIntent);
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        println(" IN onSIGNINRESULT")
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            println("Signed in! User email: " + user?.email);
        } else {
            // Sign in failed
            println("Sign in failed. result code: " + result.resultCode)
        }
    }
*/






}