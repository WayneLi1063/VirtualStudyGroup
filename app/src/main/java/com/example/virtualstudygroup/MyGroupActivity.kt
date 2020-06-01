package com.example.virtualstudygroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_explore.*

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

        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        groupSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                groupListAdapter!!.filter.filter(newText)
                return false
            }
        })
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
