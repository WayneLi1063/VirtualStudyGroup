package com.example.virtualstudygroup.views

import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.UserChat
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*

class ChatFromItem(val text: String, private val user: UserChat): Item<GroupieViewHolder>() {
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