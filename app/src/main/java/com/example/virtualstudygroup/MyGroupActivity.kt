package com.example.virtualstudygroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualstudygroup.GroupViewActivity.Companion.GROUP_KEY
import androidx.appcompat.widget.SearchView
import com.example.virtualstudygroup.chatActivity.MessageActivity
import com.example.virtualstudygroup.userManagerActivity.UserProfileActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_my_group.*

class MyGroupActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Printing"
    }

    private var groupListAdapter: GroupListAdapter? = null
    private lateinit var groupsList: MutableList<Group>
    private val database = Firebase.database
    private val groups = database.getReference("groups")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_group)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Group"

        setupBotNavBar()

        groupListAdapter = GroupListAdapter(mutableListOf<Group>())
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
                    filterMyGroupList(groupValues)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })

/*        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }*/

        groupSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                groupListAdapter!!.filter.filter(newText)
                return false
            }
        })

        /*
        btnExploration.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

         */
    }

    private fun filterMyGroupList(groupValues: MutableMap<String, Group>) {
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
                    if (userGroupValues != null) {
                        val newGroupValues: MutableList<Group> = mutableListOf()
                        userGroupValues.keys.forEach { key ->
                            groupValues[key]?.let { group ->
                                newGroupValues.add(group)
                            }
                        }
                        groupsList = newGroupValues
                        groupListAdapter?.updateGroup(groupsList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.i(TAG, "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun onGroupClicked(group: Group) {
        val intent = Intent(this, GroupViewActivity::class.java)
        intent.putExtra(GROUP_KEY, group)

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

        btn_explore_groups.setOnClickListener{
            val intent = Intent(this, ExploreActivity::class.java)
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
