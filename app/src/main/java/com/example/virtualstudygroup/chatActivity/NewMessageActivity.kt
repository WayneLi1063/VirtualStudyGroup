package com.example.virtualstudygroup.chatActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.chatActivity.ChatLogActivity.Companion.CHATAG
import com.example.virtualstudygroup.model.GroupChat
import com.example.virtualstudygroup.views.GroupChatItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_message.chat_toolbar
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    companion object {
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        // set up back button
        setSupportActionBar(chat_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Select A New Chat"

        // set up recycler view
        val adapter = GroupAdapter<GroupieViewHolder>()
        new_message_list.adapter = adapter

        fetchChats()
    }

    private fun fetchChats() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        //val ref = FirebaseDatabase.getInstance().getReference("/users")
        val ref = FirebaseDatabase.getInstance().getReference("/groups")

        // use all the groups for now
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.i("Diana", it.toString())
                    val group = it.getValue(GroupChat::class.java)
                    val groups = MessageActivity.groups

                    Log.i(CHATAG, group!!.id)
                    Log.i(CHATAG, groups.toString())
                    group.let {
                        if (groups != null && groups.contains(group.id)) {
                            adapter.add(
                                GroupChatItem(group)
                            )
                        }
                    }
                }
                
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as GroupChatItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.group)
                    startActivity(intent)

                    finish()
                }
                
                new_message_list.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}