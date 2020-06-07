package com.example.virtualstudygroup.userManagerActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.getApp
import com.example.virtualstudygroup.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile_edit.*
import kotlinx.android.synthetic.main.activity_user_profile_edit.interest_text
import kotlinx.android.synthetic.main.activity_user_profile_edit.major_text
import kotlinx.android.synthetic.main.activity_user_profile_edit.user_profile_image
import kotlinx.android.synthetic.main.activity_user_profile_edit.username_text
import kotlinx.android.synthetic.main.activity_user_profile_edit.year_text
import java.util.*


class UserProfileEditActivity : AppCompatActivity() {

    private lateinit var currentUser: FirebaseUser
    private lateinit var userData: User
    private var selectedPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)
        getApp().currentUser?.let {
            currentUser = it
        }
        fetchUser()
    }

    private fun fetchUser() {
        val uid = currentUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                p0.let {
                    userData = it.getValue(User::class.java)!!
                    fillUpInfo()
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        getApp().currentUser?.let {
            currentUser = it
        }
        fetchUser()
    }

    private fun fillUpInfo() {
        userData.let { user ->
            Picasso.get().load(user.photoURL).into(user_profile_image)
            username_text.text = user.email
            user.year?.let {
                val editable: Editable = SpannableStringBuilder(it)
                year_text.text = editable
            }
            user.name?.let {
                val editable: Editable = SpannableStringBuilder(it)
                name_text.text = editable
            }
            user.major?.let {
                val editable: Editable = SpannableStringBuilder(it)
                major_text.text = editable
            }
            user.interest?.let {
                val editable: Editable = SpannableStringBuilder(it)
                interest_text.text = editable
            }
        }

        user_profile_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btnSaveChange.setOnClickListener {
            saveChange()
        }
    }

    private fun saveChange() {
        selectedPhoto?.let {
            uploadPhoto(it)
        } ?: run {
            updateUserInfo()
        }
    }

    private fun uploadPhoto(photoUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/user_profile_image/$filename")
        ref.putFile(photoUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.i(RegisterActivity.TAG, "Photo uploaded, uri = $it")
                    selectedPhoto = it
                    updateUserInfo()
                }
            }
    }

    private fun updateUserInfo() {
        currentUser.let { user ->

            val uid = user.uid
            Log.i(RegisterActivity.TAG, uid)
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

            if (selectedPhoto == null) {
                selectedPhoto = Uri.parse(userData.photoURL)
            }

            val uploadUser = User(listOf(),
                                user.email!!,
                                name_text.text.toString(),
                                year_text.text.toString(),
                                major_text.text.toString(),
                                interest_text.text.toString(),
                                selectedPhoto.toString(),
                                user.uid)
            Log.i(RegisterActivity.TAG, "user upload created")
            ref.setValue(uploadUser)
                .addOnSuccessListener {
                    getApp().currentUser = currentUser
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                    Log.i(RegisterActivity.TAG, "saved into database")
                }.addOnFailureListener {
                    Log.i(RegisterActivity.TAG, "user upload failed")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.i(RegisterActivity.TAG, "photo selected")

            selectedPhoto = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)

            user_profile_image.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "Failed to select photo", Toast.LENGTH_SHORT).show()
        }
    }
}