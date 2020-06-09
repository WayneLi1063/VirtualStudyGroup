package com.example.virtualstudygroup.chatActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.ChatMessage
import com.example.virtualstudygroup.model.GroupChat
import com.example.virtualstudygroup.model.User
import com.example.virtualstudygroup.views.ChatFromItem
import com.example.virtualstudygroup.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_message.chat_toolbar

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toGroup: GroupChat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        // set the action bar and title
        setSupportActionBar(chat_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set up recycler view
        chat_log_recycler.adapter = adapter

        // get and show user email / name based on info
        toGroup = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toGroup?.teamName

        listenForMessages()

        btn_send_chat_log.setOnClickListener{
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toGroup?.id
        val reference = FirebaseDatabase.getInstance().getReference("/group-messages/$toId")
        reference.addChildEventListener(object: ChildEventListener {
            // listen for new messages
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    val userRef = FirebaseDatabase.getInstance().getReference("/users/${chatMessage.fromId}")
                    userRef.addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            val chatSender = p0.getValue(User::class.java)
                            chatSender?.let {
                                if (chatSender.uid != fromId) {
                                    adapter.add(ChatToItem(chatMessage.text, chatSender))

                                } else {
                                    adapter.add(ChatFromItem(chatMessage.text, chatSender))

                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }

                // scroll to the bottom of the screen
                chat_log_recycler.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    // send message to the firebase database
    private fun performSendMessage() {
        // generate auto node for messages
        val text = et_chat_log.text.toString()

        // gather information to be sent
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toGroup?.id

        if (fromId == null || toId == null) return

        // call firebase database and prepare to send new msg
        val toReference = FirebaseDatabase.getInstance().getReference("/group-messages/$toId").push()
        val toLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId")

        val chatMessage = ChatMessage(toReference.key!!, text, fromId, toId, System.currentTimeMillis())
        toReference.setValue(chatMessage)
            .addOnSuccessListener {
                et_chat_log.text.clear()
                chat_log_recycler.scrollToPosition(adapter.itemCount - 1)
            }

        toLatestMessageRef.setValue(chatMessage)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}