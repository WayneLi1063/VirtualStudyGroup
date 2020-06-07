package com.example.virtualstudygroup.chatActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.virtualstudygroup.ExploreActivity
import com.example.virtualstudygroup.MyGroupActivity
import com.example.virtualstudygroup.userManagerActivity.LoginActivity
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.userManagerActivity.UserProfileActivity
import com.example.virtualstudygroup.chatActivity.ChatLogActivity.Companion.CHATAG
import com.example.virtualstudygroup.chatActivity.NewMessageActivity.Companion.USER_KEY
import com.example.virtualstudygroup.model.ChatFilter
import com.example.virtualstudygroup.model.ChatMessage
import com.example.virtualstudygroup.model.User
import com.example.virtualstudygroup.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessageMap = HashMap<String, ChatMessage>()
    val GroupFilterMap = HashMap<String, ChatFilter>()
    private var filters : ChatFilter ?= null

    companion object {
        var currentUser: User? = null
        var groups: ArrayList<String> ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        setSupportActionBar(chat_toolbar)
        supportActionBar?.title = "ChatRoom"

        messages_recycler.adapter = adapter
        messages_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // get filter message
        // filters = intent.getParcelableExtra(FILTER_KEY)

        groups = ArrayList<String>()

        // set up filter
        fetchGroupFilter()
        fetchCurrentUser()
        verifyUserIsLoggedIn()
        listenForLatestMessage()
        setupBotNavBar()

        // set up adapter item listener
        adapter.setOnItemClickListener{ item, _ ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerGroup)
            startActivity(intent)
        }


        // set up search bar
        // Search Listener
        chatSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filter(newText)
                }
                updateMessageRecyclerView()
                return false
            }
        })
    }

    private fun filter(nameFilter: String) {
        filters = ChatFilter(
            "", nameFilter, nameFilter, false, false, false, false, false)
    }

    private fun setupBotNavBar() {
        // btn_explore_groups.setOnClickListener()
        btn_profile.setOnClickListener{
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btn_explore_groups.setOnClickListener{
            val intent = Intent(this, ExploreActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btn_my_groups.setOnClickListener{
            val intent = Intent(this, MyGroupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchGroupFilter() {
        val groupReference = FirebaseDatabase.getInstance().getReference("/groups")
        // adapter.add(LatestMessageRow(chatMessage))
        groupReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val groupInfo = p0.getValue(ChatFilter::class.java) ?:return
                GroupFilterMap[groupInfo.id] = groupInfo
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val groupInfo = p0.getValue(ChatFilter::class.java) ?:return
                GroupFilterMap[groupInfo.id] = groupInfo
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun updateMessageRecyclerView() {
        adapter.clear()
        val filterApplied = filters
        latestMessageMap.values.forEach{chatMessage ->
            var validGroup = true
            val groupInfo = GroupFilterMap[chatMessage.toId]

            if (groupInfo != null) {
                val userGroups = groups
                userGroups?.let{
                    if (userGroups.contains(chatMessage.toId)) {
                        // ADD FILTERS HERE
                        if (filterApplied != null
                            && filterApplied.className.isNotEmpty()
                            && !groupInfo.className.contains(filterApplied.className, ignoreCase = true)
                            && filterApplied.teamName.isNotEmpty()
                            && !groupInfo.teamName.contains(filterApplied.teamName, ignoreCase = true)) {
                            validGroup = false
                        }

                        if (validGroup || filterApplied == null) {
                            adapter.add(LatestMessageRow(chatMessage))
                        }
                    }
                }

                // if (filterApplied.teamName.isNotEmpty() && !groupInfo.teamName.contains(filterApplied.teamName, ignoreCase = true)) { validGroup = false }
                /*
                if (filterApplied.examSquad && groupInfo.examSquad != filterApplied.examSquad) { validGroup = false }
                if (filterApplied.homeworkHelp && groupInfo.homeworkHelp != filterApplied.homeworkHelp) { validGroup = false }
                if (filterApplied.noteExchange && groupInfo.noteExchange != filterApplied.noteExchange) { validGroup = false }
                if (filterApplied.projectPartners && groupInfo.projectPartners != filterApplied.projectPartners) { validGroup = false }
                if (filterApplied.labMates && groupInfo.labMates != filterApplied.labMates) { validGroup = false }
                 */
            }


        }
    }

    private fun listenForLatestMessage() {
        // val fromId = FirebaseAuth.getInstance().uid
        // val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages")
        reference.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // new child for the latest message
                val chatMessage = p0.getValue(ChatMessage::class.java) ?:return
                // add to the map
                latestMessageMap[p0.key!!] = chatMessage
                updateMessageRecyclerView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?:return
                latestMessageMap[p0.key!!] = chatMessage
                updateMessageRecyclerView()
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })


    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.i(CHATAG, "Current chat user ${currentUser?.uid}")
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        val groupRef = FirebaseDatabase.getInstance().getReference("/users/$uid/groups")
        groupRef.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.let {
                    for (d : DataSnapshot in p0.children) {
                        d.key?.let {
                            val group_name = d.key
                            if (group_name != null) {
                                groups?.add(group_name)
                            }
                            Log.i(CHATAG, "hello this is " + group_name)
                            Log.i(CHATAG, groups.toString())
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        // start log in if not logged in
        if (uid == null ){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }

            // go to filter
            /*R.id.menu_filter -> {
                val intent = Intent(this, ChatFilterActivity::class.java)
                startActivity(intent)
            }
             */

            // sign out from chat activity
            /* R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

             */
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
