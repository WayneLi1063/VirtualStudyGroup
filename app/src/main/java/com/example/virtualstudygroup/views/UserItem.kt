package com.example.virtualstudygroup.views

import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.GroupChat
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_message.view.*

class GroupChatItem(val group: GroupChat): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvChatUserName.text = "${group.className} - ${group.teamName}"

        if (group.img.startsWith("https:")) {
            Picasso.get().load(group.img)?.into(viewHolder.itemView.ivChatUserImage)
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_message
    }
}