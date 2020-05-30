package com.example.virtualstudygroup.views

import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.UserChat
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_message.view.*

class UserItem(val user: UserChat): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // show email instead of user name b/c username info missing
        if (user.name == "") {
            viewHolder.itemView.tvChatUserName.text = user.email
        } else {
            viewHolder.itemView.tvChatUserName.text = user.name
        }

        if (user.photoURL.startsWith("https:")) {
            Picasso.get().load(user.photoURL)?.into(viewHolder.itemView.ivChatUserImage)
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_message
    }
}