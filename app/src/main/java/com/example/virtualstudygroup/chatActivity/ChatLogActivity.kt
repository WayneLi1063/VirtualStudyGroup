package com.example.virtualstudygroup.chatActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.UserChat
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_message.chat_toolbar

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        // set the action bar and title
        setSupportActionBar(chat_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get user email / name based on info
        val user = intent.getParcelableExtra<UserChat>(NewMessageActivity.USER_KEY)

        if (user?.name == "") {
            supportActionBar?.title= user.email
        } else {
            supportActionBar?.title = user?.name
        }

        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())

        chat_log_recycler.adapter = adapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return super.onSupportNavigateUp()
    }
}

class ChatFromItem: Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem: Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
