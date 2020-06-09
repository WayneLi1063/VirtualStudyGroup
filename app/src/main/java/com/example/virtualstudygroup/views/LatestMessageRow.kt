package com.example.virtualstudygroup.views

import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.ChatMessage
import com.example.virtualstudygroup.model.GroupChat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.main_message_row.view.*

class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
    var chatPartnerGroup: GroupChat ?= null

    override fun getLayout(): Int {
        return R.layout.main_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        // fetch the user inside the users database
        val reference = FirebaseDatabase.getInstance().getReference("/groups/${chatMessage.toId}")
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerGroup = p0.getValue(GroupChat::class.java) ?: return

                // show chat partner name
                viewHolder.itemView.tv_message_name.text =
                    "${chatPartnerGroup!!.className} - ${chatPartnerGroup!!.teamName}"

                // show chat partner image
                if (chatPartnerGroup!!.img.startsWith("https:")) {
                    Picasso.get().load(chatPartnerGroup!!.img)?.into(viewHolder.itemView.iv_message_image)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        viewHolder.itemView.tv_message_recent.text = chatMessage.text
    }
}
