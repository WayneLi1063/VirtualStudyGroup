package com.example.virtualstudygroup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.virtualstudygroup.chatActivity.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        switch_to_signup.setOnClickListener {
            val intent: Intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val username = username_input.text.toString()
            val password = password_input.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username or password can't be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.i(
                                TAG,
                                "Log in successful, current user = ${auth.currentUser?.email}"
                            )
                            currentUser = auth.currentUser

                            // invoke the message activity
                            /*
                            val intent = Intent(this, MessageActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                             */
                            getApp().currentUser = currentUser

                            val intent = Intent(this, UserProfileActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.i(TAG, "Log in failed")
                            Toast.makeText(
                                this,
                                task.exception?.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    companion object {
        const val TAG = "sean"
    }
}
