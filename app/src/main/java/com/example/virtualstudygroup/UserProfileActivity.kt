package com.example.virtualstudygroup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.virtualstudygroup.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    private var currentUser: FirebaseUser ?= null
    private var userData: User?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        currentUser = getApp().currentUser
        assert(currentUser != null)
        fetchUser()
    }

    private fun fetchUser() {
        val uid = currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                p0.let {
                    userData = it.getValue(User::class.java)
                    fillUpInfo()
                }
            }

        })
    }

    private fun fillUpInfo() {
        userData?.let { user ->
            username_text.text = "Username: ${user.email}"
            year_text.text = "Year: ${user.year}"
            major_text.text = "Major: ${user.major}"
            interest_text.text = "Interest: ${user.interest}"

            Picasso.get().load(user.photoURL).into(user_profile_image)

        }
    }
}
