package com.example.virtualstudygroup

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_view.*
import kotlinx.android.synthetic.main.activity_group_view.btnEdit
import kotlinx.android.synthetic.main.activity_group_view.btnExploration
import kotlinx.android.synthetic.main.activity_group_view.btnMyGroup
import kotlinx.android.synthetic.main.activity_user_profile.*

class GroupViewActivity : AppCompatActivity() {

    companion object {
        const val GROUP_KEY = "group"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_view)

        val group = intent.getParcelableExtra<Group>(GROUP_KEY)
        val uid = getApp().currentUser?.uid

        if (group != null && uid != null && group.currNumber < group.totalNumber) {
            Picasso.get().load(group.img).error(R.drawable.ic_person_black_24dp)
                .into(ivGroupImgUpload)
            tvGroupName.text = "Group Name: ${group.teamName}"
            tvCourseName.text = "Course Name: ${group.className}"
            tvGroupSize.text = "Group Size: ${group.currNumber} out of ${group.totalNumber}"
            tvGroupDescription.text = "Group Description: ${group.groupDescription}"

            if (group.examSquad) {
                btnExamSquad.visibility = VISIBLE
            }
            if (group.labMates) {
                btnLabMates.visibility = VISIBLE
            }
            if (group.projectPartners) {
                btnProjectPartners.visibility = VISIBLE
            }
            if (group.homeworkHelp) {
                btnHomeworkHelp.visibility = VISIBLE
            }
            if (group.noteExchange) {
                btnNoteExchange.visibility = VISIBLE
            }

            val isMember = group.members?.keys?.contains(uid)
            val isLeader = group.leaders?.keys?.contains(uid)
            val groupId = group.id
            val groupRef = Firebase.database.getReference("groups").child(groupId)
            val userRef = Firebase.database.getReference("users").child(uid)


            if (isMember != null && isMember) {
                btnLeave.visibility = VISIBLE
                btnChatRoom.visibility = VISIBLE

                btnLeave.setOnClickListener {
                    groupRef.child("currNumber").setValue(group.currNumber - 1)
                    groupRef.child("members").child(uid).setValue(null)
                    userRef.child("groups").child(groupId).setValue(null).addOnSuccessListener {
                        Toast.makeText(this, "Leave successfully.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                }

                btnChatRoom.setOnClickListener {

                }
            } else if (isLeader != null && isLeader) {
                btnEdit.visibility = VISIBLE
                btnChatRoom.visibility = VISIBLE

                btnEdit.setOnClickListener {

                }
                btnChatRoom.setOnClickListener {

                }
            } else { // User has not yet join the group
                btnJoin.visibility = VISIBLE

                btnJoin.setOnClickListener {
                    groupRef.child("currNumber").setValue(group.currNumber + 1)
                    groupRef.child("members").child(uid).setValue(true)
                    userRef.child("groups").child(groupId).setValue(true).addOnSuccessListener {
                        Toast.makeText(this, "Join successfully.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MyGroupActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        btnExploration.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        btnMyGroup.setOnClickListener {
            val intent = Intent(this, MyGroupActivity::class.java)
            startActivity(intent)
        }

    }

}