package com.example.virtualstudygroup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Printing"
    }

    private var groupListAdapter: GroupListAdapter? = null
    private lateinit var groupsList: MutableList<Group>
    private val database = Firebase.database
    private val groups = database.getReference("groups")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        groupListAdapter = GroupListAdapter(mutableListOf<Group>())
        rvGroupList.adapter = groupListAdapter

        groupListAdapter?.onGroupClickListener = { group ->
            onGroupClicked(group)
        }

        groups.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                groupsList = dataSnapshot.getValue<MutableMap<String, Group>>()?.values?.toMutableList()!!
                groupListAdapter?.updateGroup(groupsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })
    }

//    private fun getGroupListFragment() =
//        supportFragmentManager.findFragmentByTag(GroupListFragment.TAG) as? GroupListFragment

    private fun onGroupClicked(group: Group) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
