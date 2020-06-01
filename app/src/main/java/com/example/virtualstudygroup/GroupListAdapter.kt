package com.example.virtualstudygroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class GroupListAdapter(private var groupList: MutableList<Group>): RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>() {

    var onGroupClickListener: ((group: Group) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupListViewHolder {
        val itemGroupView = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupListViewHolder(itemGroupView)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    fun updateGroup(newGroupList: MutableList<Group>) {
        groupList = newGroupList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GroupListViewHolder, position: Int) {
        val group = groupList[position]
        holder.bind(group)
    }

    inner class GroupListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageGroup by lazy { itemView.findViewById<ImageView>(R.id.imageGroup) }
        private val textCourse by lazy { itemView.findViewById<TextView>(R.id.textCourse) }
        private val textTeamName by lazy { itemView.findViewById<TextView>(R.id.textTeamName) }

        fun bind(group: Group) {
            Picasso.get().load(group.img).error(R.drawable.ic_person_black_24dp)
                .into(imageGroup)
            textCourse.text = group.className
            textTeamName.text = group.teamName

            itemView.setOnClickListener{
                    onGroupClickListener?.invoke(group)
            }
        }

    }
}