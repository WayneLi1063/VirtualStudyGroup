package com.example.virtualstudygroup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualstudygroup.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_message.chat_toolbar
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_message.view.*

class NewMessageActivity : AppCompatActivity() {

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

        fetchUsers()

    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.i("Diana", it.toString())
                    val user = it.getValue(UserChat::class.java)
                    user?.let {
                        adapter.add(UserItem(user))
                    }
                }
                new_message_list.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return super.onSupportNavigateUp()
    }
}

class UserItem(val user: UserChat): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvChatUserName.text = user.name
        if (user.photoURL.startsWith("https:")) {
            Picasso.get().load(user.photoURL)?.into(viewHolder.itemView.ivChatUserImage)
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_message
    }

}

data class UserChat(
    val email: String?,
    val name: String?,
    val photoURL: String,
    val uid: String
) {
    constructor(): this("","","","")
}