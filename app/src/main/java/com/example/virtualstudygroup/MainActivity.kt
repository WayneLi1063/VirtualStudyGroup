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

class MainActivity : AppCompatActivity(), onGroupClickedListener {

    companion object {
        const val TAG = "Printing"
    }

    private var groupsData: MutableMap<String, Group>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val groups = database.getReference("groups")


        groups.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                groupsData = dataSnapshot.getValue<MutableMap<String, Group>>()

                if (getGroupListFragment() == null) {
                    val immutableGroupsData = groupsData
                    if (immutableGroupsData != null) {
                        val groupListFragment = GroupListFragment.getInstance(immutableGroupsData)
                        supportFragmentManager
                            .beginTransaction()
                            .add(
                                R.id.fragContainer, groupListFragment, GroupListFragment.TAG
                            )
                            .commit()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })



    }

    private fun getGroupListFragment() =
        supportFragmentManager.findFragmentByTag(GroupListFragment.TAG) as? GroupListFragment

    override fun onGroupClicked(group: Group) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
