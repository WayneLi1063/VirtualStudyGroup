package com.example.virtualstudygroup.userManagerActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.virtualstudygroup.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        Toast.makeText(this, getString(R.string.start_message), Toast.LENGTH_SHORT).show()

        val layout: ConstraintLayout = findViewById(R.id.welcome_screen)
        layout.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
