package com.example.virtualstudygroup.groupActivity

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualstudygroup.model.Group
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.chatActivity.ChatLogActivity
import com.example.virtualstudygroup.chatActivity.NewMessageActivity
import com.example.virtualstudygroup.getApp
import com.example.virtualstudygroup.model.GroupChat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_view.*

class GroupViewActivity : AppCompatActivity() {

    companion object {
        const val GROUP_KEY = "group"
        const val EDIT_KEY = "edit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Group"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val group = intent.getParcelableExtra<Group>(
            GROUP_KEY
        )
        val uid = getApp().currentUser?.uid

        if (group != null && uid != null) {
            Picasso.get().load(group.img).error(R.drawable.ic_person_black_24dp)
                .into(ivGroupImgUpload)
            tvGroupName.text = getString(R.string.group_name_placeholder).format(group.teamName)
            tvCourseName.text = getString(R.string.course_name_placeholder).format(group.className)
            tvGroupSize.text = getString(R.string.group_size_placeholder).format(group.currNumber, group.totalNumber)
            tvGroupDescription.text = getString(R.string.group_description_placeholder).format(group.groupDescription)

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
                        val intent = Intent(this, MyGroupActivity::class.java)
                        startActivity(intent)
                    }
                }

                btnChatRoom.setOnClickListener {
                    val groupChat = GroupChat(group.className, group.teamName, group.img, group.id)
                    val intent = Intent(it.context, ChatLogActivity::class.java)
                    intent.putExtra(NewMessageActivity.USER_KEY, groupChat)
                    startActivity(intent)

                }
            } else if (isLeader != null && isLeader) {
                btnEdit.visibility = VISIBLE
                btnChatRoom.visibility = VISIBLE

                btnEdit.setOnClickListener {
                    val intent = Intent(this, EditGroupActivity::class.java)
                    intent.putExtra(EDIT_KEY,group)
                    startActivity(intent)
                }
                btnChatRoom.setOnClickListener {
                    val groupChat = GroupChat(group.className, group.teamName, group.img, group.id)
                    val intent = Intent(it.context, ChatLogActivity::class.java)
                    intent.putExtra(NewMessageActivity.USER_KEY, groupChat)
                    startActivity(intent)
                }
            } else if (group.currNumber < group.totalNumber) { // User has not yet join the group
                btnJoin.visibility = VISIBLE

                btnJoin.setOnClickListener {
                    groupRef.child("currNumber").setValue(group.currNumber + 1)
                    groupRef.child("members").child(uid).setValue(true)
                    userRef.child("groups").child(groupId).setValue(true).addOnSuccessListener {
                        Toast.makeText(this, "Join successfully. You are now at My Groups Page.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MyGroupActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

/*        btnExploration.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        btnMyGroup.setOnClickListener {
            val intent = Intent(this, MyGroupActivity::class.java)
            startActivity(intent)
        }*/

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}