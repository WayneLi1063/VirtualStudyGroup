package com.example.virtualstudygroup.chatActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.virtualstudygroup.LoginActivity
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.chatActivity.ChatLogActivity.Companion.CHATAG
import com.example.virtualstudygroup.model.ChatMessage
import com.example.virtualstudygroup.model.UserChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.main_message_row.view.*

class MessageActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessageMap = HashMap<String, ChatMessage>()

    companion object {
        var currentUser: UserChat? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        setSupportActionBar(chat_toolbar)

        // setupDummyRows()
        messages_recycler.adapter = adapter

        fetchCurrentUser()

        listenForLatestMessage()

        verifyUserIsLoggedIn()
    }

    class MainMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.main_message_row
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            // viewHolder.itemView.tv_message_name.text = chatMessage.text
            viewHolder.itemView.tv_message_recent.text = chatMessage.text
            // viewHolder.itemView.iv_message_image
        }
    }

    private fun updateMessageRecyclerView() {
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(MainMessageRow(it))
        }
    }

    private fun listenForLatestMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
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
                currentUser = p0.getValue(UserChat::class.java)
                Log.i(CHATAG, "Current chat user ${currentUser?.uid}")
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

            // sign out from chat activity
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
