package com.example.virtualstudygroup.groupActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualstudygroup.model.Group
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.getApp
import com.example.virtualstudygroup.userManagerActivity.RegisterActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create.*
import java.util.*


class CreateGroupActivity : AppCompatActivity() {

    private var groupName: String? = null
    private var courseName: String? = null
    private var totalNumber: Int? = null
    private var groupImage: Uri? = null
    private var newGroupImage: Uri? = null
    private var examSquad: Boolean = false
    private var homeworkHelp: Boolean = false
    private var labMates: Boolean = false
    private var noteExchange: Boolean = false
    private var projectPartners: Boolean = false
    private var groupDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Create"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnExamSquad.setOnClickListener {
            examSquad = if (examSquad) {
                btnExamSquad.setBackgroundResource(R.drawable.unselected_button)
                !examSquad
            } else {
                btnExamSquad.setBackgroundResource(R.drawable.selected_button)
                !examSquad
            }
        }

        btnHomeworkHelp.setOnClickListener {
            homeworkHelp = if (homeworkHelp) {
                btnHomeworkHelp.setBackgroundResource(R.drawable.unselected_button)
                !homeworkHelp
            } else {
                btnHomeworkHelp.setBackgroundResource(R.drawable.selected_button)
                !homeworkHelp
            }
        }

        btnLabMates.setOnClickListener {
            labMates = if (labMates) {
                btnLabMates.setBackgroundResource(R.drawable.unselected_button)
                !labMates
            } else {
                btnLabMates.setBackgroundResource(R.drawable.selected_button)
                !labMates
            }
        }

        btnProjectPartners.setOnClickListener {
            projectPartners = if (projectPartners) {
                btnProjectPartners.setBackgroundResource(R.drawable.unselected_button)
                !projectPartners
            } else {
                btnProjectPartners.setBackgroundResource(R.drawable.selected_button)
                !projectPartners
            }
        }

        btnNoteExchange.setOnClickListener {
            noteExchange = if (noteExchange) {
                btnNoteExchange.setBackgroundResource(R.drawable.unselected_button)
                !noteExchange
            } else {
                btnNoteExchange.setBackgroundResource(R.drawable.selected_button)
                !noteExchange
            }
        }

        btnGroupImgUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btnFinish.setOnClickListener {
            if (isEmpty(etGroupName)) {
                Toast.makeText(this, "Group name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isEmpty(etGroupSize)) {
                Toast.makeText(this, "Group size cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (etGroupSize.text.toString().toInt() <= 1) {
                Toast.makeText(this, "Group size cannot be less than 2 people.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isEmpty(etCourseName)) {
                Toast.makeText(this, "Course name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val regex = """[A-Z\s]{3,6}\s?[0-9]{3}""".toRegex()
            groupName = etGroupName.text.toString()
            courseName = etCourseName.text.toString().toUpperCase(Locale.ROOT)
            val correctCourseName = courseName?.let { it1 -> regex.matches(it1) }
            if (correctCourseName != null && !correctCourseName) {
                Toast.makeText(this, "Course name format is incorrect, correct examples: 'CSE142' or 'korean 101'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            totalNumber = etGroupSize.text.toString().toInt()
            groupDescription = etGroupDescription.text.toString()
            groupImage?.let { photo ->
                uploadPhoto(photo)
            } ?: run {
                val defaultPhotoName = "default_image.png"
                val ref = FirebaseStorage.getInstance().getReference("/images/group_image/$defaultPhotoName")
                ref.downloadUrl.addOnSuccessListener {
                    newGroupImage = it
                }
            }
            if (groupName != null && courseName != null && totalNumber != null && newGroupImage != null) {
                val user = getApp().currentUser
                val uid = user?.uid
                val groupCount =  getApp().groupCount

                if (uid != null && groupCount != null) {
                    val newId = "Group_${(groupCount + 1)}"
                    val newGroup =
                        Group(
                            id = newId,
                            className = courseName!!,
                            teamName = groupName!!,
                            currNumber = 1,
                            totalNumber = totalNumber!!,
                            img = newGroupImage.toString(),
                            examSquad = examSquad,
                            labMates = labMates,
                            projectPartners = projectPartners,
                            noteExchange = noteExchange,
                            homeworkHelp = homeworkHelp,
                            leaders = mutableMapOf(uid to true),
                            groupDescription = groupDescription
                        )
                    val groupRef = Firebase.database.getReference("groups")
                    groupRef.child(newId).setValue(newGroup).addOnSuccessListener {
                        val groupCountRef = Firebase.database.getReference("groupCount")
                        groupCountRef.setValue(groupCount + 1)
                    }

                    val userRef = Firebase.database.getReference("users")
                    userRef.child(uid).child("groups").child(newId).setValue(true)

                    val intent = Intent(this, MyGroupActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim().isEmpty()
    }

    private fun uploadPhoto(photoUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/group_image/$filename")
        ref.putFile(photoUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.i(RegisterActivity.TAG, "Photo uploaded, uri = $it")
                    newGroupImage = it
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.i(RegisterActivity.TAG, "photo selected")

            groupImage = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, groupImage)

            btnGroupImgUpload.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "Failed to select photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}