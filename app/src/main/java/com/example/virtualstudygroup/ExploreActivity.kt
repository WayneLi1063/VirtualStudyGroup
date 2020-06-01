package com.example.virtualstudygroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

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
                    filterExploreList(groupValues)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })

        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        btnMyGroup.setOnClickListener {
            val intent = Intent(this, MyGroupActivity::class.java)
            startActivity(intent)
        }
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
                    if (userGroupValues != null) {
                        userGroupValues.keys.forEach { key ->
                                groupValues.remove(key)
                        }
                        groupsList = groupValues.values.toMutableList()
                        groupListAdapter?.updateGroup(groupsList)
                    }
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
}
