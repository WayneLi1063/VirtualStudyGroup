package com.example.virtualstudygroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class GroupListAdapter(private var groupList: MutableList<Group>): RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>(),
    Filterable {

    var onGroupClickListener: ((group: Group) -> Unit)? = null
    private var groupFilterList: MutableList<Group>? = null

    init {
        groupFilterList = groupList
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupListViewHolder {
        val itemGroupView = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupListViewHolder(itemGroupView)
    }

    override fun getItemCount(): Int {
        return groupFilterList?.size!!
    }

    fun updateGroup(newGroupList: MutableList<Group>) {
        groupList = newGroupList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GroupListViewHolder, position: Int) {
        val group = groupFilterList?.get(position)
        if (group != null) {
            holder.bind(group)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    groupFilterList = groupList
                } else {
                    val resultList = ArrayList<Group>()
                    for (row in groupList) {
                        if (row.teamName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) || row.className.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    groupFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = groupFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                groupFilterList = results?.values as MutableList<Group>
                notifyDataSetChanged()
            }

        }
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