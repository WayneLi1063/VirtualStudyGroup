package com.example.virtualstudygroup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_explore.*

class GroupListFragment(private val groupsData: MutableMap<String, Group>): Fragment() {

    private lateinit var groupListAdapter: GroupListAdapter
    private var groupsList: MutableList<Group>? = null

    private var onGroupClickedListener: onGroupClickedListener? = null

    companion object {
        val TAG: String = GroupListFragment::class.java.simpleName

        fun getInstance(groupsData: MutableMap<String, Group>): GroupListFragment {
            return GroupListFragment(groupsData)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is onGroupClickedListener) {
            onGroupClickedListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        groupsList = groupsData.values.toMutableList()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val immutableGroupList = groupsList

        if (immutableGroupList != null) {
            groupListAdapter = GroupListAdapter(immutableGroupList)
            rvGroupList.adapter = groupListAdapter

            groupListAdapter.onGroupClickListener = { group ->
                onGroupClickedListener?.onGroupClicked(group)
            }
        }
    }
}

interface onGroupClickedListener {
    fun onGroupClicked(group: Group)
}