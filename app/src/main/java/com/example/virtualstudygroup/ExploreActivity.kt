package com.example.virtualstudygroup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
//import android.widget.SearchView
import androidx.appcompat.widget.SearchView
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
//    private var groupsList: MutableList<Group>? = null
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
                    btnHomeworkHelp.setBackgroundColor(resources.getColor(R.color.beige))
                    homeworkHelp = !homeworkHelp
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnHomeworkHelp.setBackgroundColor(Color.GREEN)
                    homeworkHelp = !homeworkHelp
                    groupListAdapter!!.filter.filter("homeworkhelp")
                }
            }
        })

        btnExamSquad.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (examSquad) {
                    btnExamSquad.setBackgroundColor(resources.getColor(R.color.beige))
                    examSquad = !examSquad
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnExamSquad.setBackgroundColor(Color.GREEN)
                    examSquad = !examSquad
                    groupListAdapter!!.filter.filter("examsquad")
                }
            }
        })

        btnLabMates.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (labMates) {
                    btnLabMates.setBackgroundColor(resources.getColor(R.color.beige))
                    labMates = !labMates
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnLabMates.setBackgroundColor(Color.GREEN)
                    labMates = !labMates
                    groupListAdapter!!.filter.filter("labmate")
                }
            }
        })

        btnProjectPartners.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (projectPartners) {
                    btnProjectPartners.setBackgroundColor(resources.getColor(R.color.beige))
                    projectPartners = !projectPartners
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnProjectPartners.setBackgroundColor(Color.GREEN)
                    projectPartners = !projectPartners
                    groupListAdapter!!.filter.filter("projectpartner")
                }
            }
        })

        btnNoteExchange.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (noteExchange) {
                    btnNoteExchange.setBackgroundColor(resources.getColor(R.color.beige))
                    noteExchange = !noteExchange
                    groupListAdapter!!.filter.filter("")
                } else {
                    btnNoteExchange.setBackgroundColor(Color.GREEN)
                    noteExchange = !noteExchange
                    groupListAdapter!!.filter.filter("noteexchange")
                }
            }
        })


//        btnHomeworkHelp.setOnClickListener {
//            homeworkHelp = if (homeworkHelp) {
//                btnHomeworkHelp.setBackgroundColor(resources.getColor(R.color.beige))
//                !homeworkHelp
//            } else {
//                btnHomeworkHelp.setBackgroundColor(Color.GREEN)
//                !homeworkHelp
//                groupListAdapter!!.filter.filter("homework")
////                groupSearch.setQuery("homework", false)
////                groupSearch.clearFocus()
//            }
//        }

//        btnFilter.setOnClickListener {
//            if (groupsList != null) {
//                val intent = Intent(this, FilterGroupActivity::class.java)
////                intent.putExtra("GROUP_LIST", groupsList)
//                startActivity(intent)
//            }
//        }
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
}

private fun SearchView.setOnQueryTextListener(onQueryTextListener: SearchView.OnQueryTextListener) {

}

