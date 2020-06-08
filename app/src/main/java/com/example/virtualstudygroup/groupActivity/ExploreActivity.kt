package com.example.virtualstudygroup.groupActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualstudygroup.*
import com.example.virtualstudygroup.chatActivity.MessageActivity
import com.example.virtualstudygroup.model.Group
import com.example.virtualstudygroup.userManagerActivity.UserProfileActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_explore.*

class ExploreActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Printing"
    }

    private var groupListAdapter: GroupListAdapter? = null
    private lateinit var groupsList: MutableList<Group>
    private val database = Firebase.database
    private val groups = database.getReference("groups")
    private var homeworkHelp: Boolean = false
    private var projectPartners: Boolean = false
    private var examSquad: Boolean = false
    private var labMates: Boolean = false
    private var noteExchange: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Explore"

        setupBotNavBar()

        groupListAdapter =
            GroupListAdapter(
                mutableListOf<Group>()
            )
        rvGroupList.adapter = groupListAdapter

        groupListAdapter?.onGroupClickListener = { group ->
            onGroupClicked(group)
        }

        groups.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val groupValues = dataSnapshot.getValue<MutableMap<String, Group>>()
                if (groupValues != null) {
                    filterExploreList(groupValues)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })

        // Search Listener
        groupSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                groupListAdapter!!.filter.filter(newText)
                return false
            }
        })

        // Group Tags Button onClick Listener
        btnHomeworkHelp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (homeworkHelp) {
                    btnHomeworkHelp.setBackgroundResource(R.drawable.unselected_button)
                    homeworkHelp = !homeworkHelp
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnHomeworkHelp.setBackgroundResource(R.drawable.selected_button)
                    homeworkHelp = !homeworkHelp
                    groupListAdapter!!.filter.filter("homeworkhelp")
                }
            }
        })

        btnExamSquad.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (examSquad) {
                    btnExamSquad.setBackgroundResource(R.drawable.unselected_button)
                    examSquad = !examSquad
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnExamSquad.setBackgroundResource(R.drawable.selected_button)
                    examSquad = !examSquad
                    groupListAdapter!!.filter.filter("examsquad")
                }
            }
        })

        btnLabMates.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (labMates) {
                    btnLabMates.setBackgroundResource(R.drawable.unselected_button)
                    labMates = !labMates
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnLabMates.setBackgroundResource(R.drawable.selected_button)
                    labMates = !labMates
                    groupListAdapter!!.filter.filter("labmate")
                }
            }
        })

        btnProjectPartners.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (projectPartners) {
                    btnProjectPartners.setBackgroundResource(R.drawable.unselected_button)
                    projectPartners = !projectPartners
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnProjectPartners.setBackgroundResource(R.drawable.selected_button)
                    projectPartners = !projectPartners
                    groupListAdapter!!.filter.filter("projectpartner")
                }
            }
        })

        btnNoteExchange.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (noteExchange) {
                    btnNoteExchange.setBackgroundResource(R.drawable.unselected_button)
                    noteExchange = !noteExchange
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnNoteExchange.setBackgroundResource(R.drawable.selected_button)
                    noteExchange = !noteExchange
                    groupListAdapter!!.filter.filter("noteexchange")
                }
            }
        })
    }


    private fun filterExploreList(groupValues: MutableMap<String, Group>) {
        val user = getApp().currentUser
        val uid = user?.uid

        if (uid != null) {
            val userRef = Firebase.database.getReference("users")
            val userGroupsRef = userRef.child(uid).child("groups")

            userGroupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val userGroupValues = dataSnapshot.getValue<MutableMap<String, Boolean>>()
                    userGroupValues?.keys?.forEach { key ->
                        groupValues.remove(key)
                    }
                    groupsList = groupValues.values.toMutableList()
                    groupListAdapter?.updateGroup(groupsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.i(MyGroupActivity.TAG, "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun onGroupClicked(group: Group) {
        val intent = Intent(this, GroupViewActivity::class.java)
        intent.putExtra(GroupViewActivity.GROUP_KEY, group)

        startActivity(intent)
    }

    private fun setupBotNavBar() {
        btn_profile.setOnClickListener{
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btn_chatroom.setOnClickListener{
            val intent = Intent(this, MessageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btn_my_groups.setOnClickListener{
            val intent = Intent(this, MyGroupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_create_group -> {
                val intent = Intent(this, CreateGroupActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_group_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

