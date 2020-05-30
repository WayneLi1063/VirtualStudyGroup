package com.example.virtualstudygroup.views

import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.ChatMessage
import com.example.virtualstudygroup.model.UserChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.main_message_row.view.*

class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.main_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartnerId: String

        // get the chat partner
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        // fetch the user inside the users database
        val reference = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(UserChat::class.java) ?: return

                // show chat partner name
                if (user.name != "") {
                    viewHolder.itemView.tv_message_name.text = user.name
                } else {
                    viewHolder.itemView.tv_message_name.text = user.email
                }

                // show chat partner image
                if (user.photoURL.startsWith("https:")) {
                    Picasso.get().load(user.photoURL)?.into(viewHolder.itemView.iv_message_image)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        viewHolder.itemView.tv_message_recent.text = chatMessage.text
    }
}
