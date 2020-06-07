package com.example.virtualstudygroup.views

import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem (val text: String, private val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_user_chat_log_to.text = text

        // load our user image into the start
        val uri = user.photoURL
        if (uri.startsWith("https:")) {
            Picasso.get().load(uri).into(viewHolder.itemView.iv_user_chat_log_to)
        }

        viewHolder.itemView.tv_user_name.text = user.name
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
