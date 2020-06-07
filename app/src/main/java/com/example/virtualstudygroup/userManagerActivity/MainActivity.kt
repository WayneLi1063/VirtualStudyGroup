package com.example.virtualstudygroup.userManagerActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.chatActivity.MessageActivity
import com.example.virtualstudygroup.getApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val layout: ConstraintLayout = findViewById(R.id.welcome_screen)


        Firebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                layout.setOnClickListener {
                    val intent: Intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                alertUser(false)
            } else {
                getApp().currentUser = it.currentUser
                layout.setOnClickListener {
                    val intent = Intent(this, MessageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                alertUser(true)
            }
        }

    }

    private fun alertUser(isSignedIn: Boolean) {
        if (!isSignedIn) {
            Toast.makeText(this, getString(R.string.start_message),
                Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.user_already_signedin_message)
                                            .format(getApp().currentUser!!.email),
                Toast.LENGTH_SHORT).show()

        }
    }
}
