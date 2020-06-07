package com.example.virtualstudygroup.groupActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.getApp
import com.example.virtualstudygroup.model.Group
import com.example.virtualstudygroup.userManagerActivity.RegisterActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_edit_group.*
import kotlinx.android.synthetic.main.activity_edit_group.btnExamSquad
import kotlinx.android.synthetic.main.activity_edit_group.btnFinish
import kotlinx.android.synthetic.main.activity_edit_group.btnGroupImgUpload
import kotlinx.android.synthetic.main.activity_edit_group.btnHomeworkHelp
import kotlinx.android.synthetic.main.activity_edit_group.btnLabMates
import kotlinx.android.synthetic.main.activity_edit_group.btnNoteExchange
import kotlinx.android.synthetic.main.activity_edit_group.btnProjectPartners
import kotlinx.android.synthetic.main.activity_edit_group.etCourseName
import kotlinx.android.synthetic.main.activity_edit_group.etGroupDescription
import kotlinx.android.synthetic.main.activity_edit_group.etGroupName
import kotlinx.android.synthetic.main.activity_edit_group.etGroupSize
import java.util.*

class EditGroupActivity: AppCompatActivity() {
    private var groupName: String? = null
    private var courseName: String? = null
    private var totalNumber: Int? = null
    private var groupImage: Uri? = null
    private var userSetNewImage: Boolean = false
    private var newGroupImage: Uri? = null
    private var examSquad: Boolean = false
    private var homeworkHelp: Boolean = false
    private var labMates: Boolean = false
    private var noteExchange: Boolean = false
    private var projectPartners: Boolean = false
    private var groupDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_group)

        val groupForEdit: Group? = intent.getParcelableExtra(GroupViewActivity.EDIT_KEY)
        if (groupForEdit != null) {
            groupName = groupForEdit.teamName
            courseName = groupForEdit.className
            totalNumber = groupForEdit.totalNumber
            groupImage = groupForEdit.img.toUri()
            groupDescription = groupForEdit.groupDescription
            examSquad = groupForEdit.examSquad
            homeworkHelp = groupForEdit.homeworkHelp
            labMates = groupForEdit.labMates
            projectPartners = groupForEdit.projectPartners
            noteExchange = groupForEdit.noteExchange
            Picasso.get().load(groupImage).error(R.drawable.ic_person_black_24dp)
                .into(btnGroupImgUpload)
            etGroupName.setText(groupName)
            etCourseName.setText(courseName)
            etGroupSize.setText(totalNumber.toString())
            etGroupDescription.setText(groupDescription)

            if (examSquad) {
                btnExamSquad.setBackgroundResource(R.drawable.selected_button)
            }
            if (homeworkHelp) {
                btnHomeworkHelp.setBackgroundResource(R.drawable.selected_button)
            }
            if (labMates) {
                btnLabMates.setBackgroundResource(R.drawable.selected_button)
            }
            if (projectPartners) {
                btnProjectPartners.setBackgroundResource(R.drawable.selected_button)
            }
            if (noteExchange) {
                btnNoteExchange.setBackgroundResource(R.drawable.selected_button)
            }
        }

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
            courseName = etCourseName.text.toString()
            val correctCourseName = courseName?.let { it1 -> regex.matches(it1) }
            if (correctCourseName != null && !correctCourseName) {
                Toast.makeText(this, "Course name format is incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            totalNumber = etGroupSize.text.toString().toInt()
            val nonMutableTotalNum = totalNumber
            if (groupForEdit?.currNumber != null && nonMutableTotalNum != null && groupForEdit.currNumber > nonMutableTotalNum) {
                Toast.makeText(this, "New group size cannot be smaller than ${groupForEdit.currNumber}.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            groupDescription = etGroupDescription.text.toString()
            if (userSetNewImage) {
                groupImage?.let { it1 -> uploadPhoto(it1) }
            } else {
                if (groupForEdit != null) {
                    newGroupImage = groupForEdit.img.toUri()
                }
            }
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

                if (uid != null && groupForEdit != null) {
                    val id = groupForEdit.id
                    val curNum = groupForEdit.currNumber
                    val members = groupForEdit.members
                    val leaders = groupForEdit.leaders
                    val newGroup =
                        Group(
                            id = id,
                            className = courseName!!,
                            teamName = groupName!!,
                            currNumber = curNum,
                            totalNumber = totalNumber!!,
                            img = newGroupImage.toString(),
                            examSquad = examSquad,
                            labMates = labMates,
                            projectPartners = projectPartners,
                            noteExchange = noteExchange,
                            homeworkHelp = homeworkHelp,
                            members = members,
                            leaders = leaders,
                            groupDescription = groupDescription
                        )
                    val groupRef = Firebase.database.getReference("groups")
                    groupRef.child(id).setValue(newGroup)

                    val intent = Intent(this, MyGroupActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        btnDisband.setOnClickListener {

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
            userSetNewImage = true

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, groupImage)

            btnGroupImgUpload.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "Failed to select photo", Toast.LENGTH_SHORT).show()
        }
    }

}