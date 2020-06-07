package com.example.virtualstudygroup.userManagerActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.virtualstudygroup.*
import com.example.virtualstudygroup.chatActivity.MessageActivity
import com.example.virtualstudygroup.groupActivity.ExploreActivity
import com.example.virtualstudygroup.groupActivity.MyGroupActivity
import com.example.virtualstudygroup.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
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

        setSupportActionBar(profile_toolbar)
        supportActionBar?.title = "Profile"

        currentUser = getApp().currentUser

        btnSignout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        fetchUser()

        setupBotNavBar()
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
                    btnChangePassword.setOnClickListener {
                        val auth = Firebase.auth
                        val emailAddress = currentUser!!.email
                        auth.sendPasswordResetEmail(emailAddress!!)
                        Toast.makeText(applicationContext,
                            "Please check your email for reset link",
                            Toast.LENGTH_SHORT).show()

                    }
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        currentUser = getApp().currentUser
        fetchUser()
    }

    private fun fillUpInfo() {
        userData?.let { user ->
            username_text.text = "Username: ${user.email}"
            year_text.text = "Year: ${user.year}"
            major_text.text = "Major: ${user.major}"
            interest_text.text = "Interest: ${user.interest}"
            name_text.text = "Name: ${user.name}"

            Picasso.get().load(user.photoURL).into(user_profile_image)

        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBotNavBar() {
        btn_chatroom.setOnClickListener{
            val intent = Intent(this, MessageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btn_explore_groups.setOnClickListener{
            val intent = Intent(this, ExploreActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btn_my_groups.setOnClickListener{
            val intent = Intent(this, MyGroupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
