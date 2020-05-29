package com.example.virtualstudygroup.chatActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.ChatMessage
import com.example.virtualstudygroup.model.User
import com.example.virtualstudygroup.model.UserChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_message.chat_toolbar
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: UserChat? = null

    companion object {
        val CHATAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        // set the action bar and title
        setSupportActionBar(chat_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set up recycler view
        chat_log_recycler.adapter = adapter

        // get and show user email / name based on info
        toUser = intent.getParcelableExtra<UserChat>(NewMessageActivity.USER_KEY)
        if (toUser?.name == "") {
            supportActionBar?.title= toUser!!.email
        } else {
            supportActionBar?.title = toUser?.name
        }

        listenForMessages()

        btn_send_chat_log.setOnClickListener{
            Log.i(CHATAG, "Attempt to send msg...")
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val reference = FirebaseDatabase.getInstance().getReference("/messages")
        reference.addChildEventListener(object: ChildEventListener {
            // listen for new messages
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    // check if its a from/to message

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = MessageActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

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
        val user = intent.getParcelableExtra<UserChat>(NewMessageActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null || toId == null) return

        // call firebase database and prepare to send new msg
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis())
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.i(CHATAG, "Saved our chat message: ${reference.key}")
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

class ChatFromItem(val text: String, val user: UserChat): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_user_chat_log_from.text = text

        val uri = user.photoURL
        if (uri.startsWith("https:")) {
            Picasso.get().load(uri).into(viewHolder.itemView.iv_user_chat_log_from)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem (val text: String, val user: UserChat): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_user_chat_log_to.text = text

        // load our user image into the start
        val uri = user.photoURL
        if (uri.startsWith("https:")) {
            Picasso.get().load(uri).into(viewHolder.itemView.iv_user_chat_log_to)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
