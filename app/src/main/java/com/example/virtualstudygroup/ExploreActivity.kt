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
                val groupValues = dataSnapshot.getValue<MutableMap<String, Group>>()?.values
                if (groupValues != null) {
                    groupsList = groupValues.toMutableList()
                    groupListAdapter?.updateGroup(groupsList)
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
    }

//    private fun getGroupListFragment() =
//        supportFragmentManager.findFragmentByTag(GroupListFragment.TAG) as? GroupListFragment

    private fun onGroupClicked(group: Group) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
