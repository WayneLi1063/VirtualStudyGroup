package com.example.virtualstudygroup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_explore.*

class GroupListFragment(): Fragment() {

    private var groupListAdapter: GroupListAdapter? = null
    private lateinit var groupsList: MutableList<Group>
    private val database = Firebase.database
    private val groups = database.getReference("groups")

    private var onGroupClickedListener: OnGroupClickedListener? = null

    companion object {
        val TAG: String = GroupListFragment::class.java.simpleName

        fun getInstance(): GroupListFragment {
            return GroupListFragment()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnGroupClickedListener) {
            onGroupClickedListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupListAdapter = GroupListAdapter(mutableListOf<Group>())
        rvGroupList.adapter = groupListAdapter

        groupListAdapter?.onGroupClickListener = { group ->
            onGroupClickedListener?.onGroupClicked(group)
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
                Log.i(MainActivity.TAG, "Failed to read value.", error.toException())
            }
        })
    }
}

interface OnGroupClickedListener {
    fun onGroupClicked(group: Group)
}