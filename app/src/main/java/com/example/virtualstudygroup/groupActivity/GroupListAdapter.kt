package com.example.virtualstudygroup.groupActivity

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.model.Group
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
        if (groupFilterList.isNullOrEmpty()) {
            return groupList.size
        }
        return groupFilterList!!.size
    }

    fun updateGroup(newGroupList: MutableList<Group>) {
        groupList = newGroupList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GroupListViewHolder, position: Int) {
        if (groupFilterList.isNullOrEmpty()) {
            val group = groupList[position]
            holder.bind(group)
        } else {
            val group = groupFilterList?.get(position)
            if (group != null) {
                holder.bind(group)
            }
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
                        // var validGroup = true;
                        // rewrite the filter
                        if (row.teamName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))
                            || row.className.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))
                            || (row.homeworkHelp && charSearch.toLowerCase(Locale.ROOT) == "homeworkhelp")
                            || (row.examSquad && charSearch.toLowerCase(Locale.ROOT) == "examsquad")
                            || (row.labMates && charSearch.toLowerCase(Locale.ROOT) == "labmate")
                            || (row.projectPartners && charSearch.toLowerCase(Locale.ROOT) == "projectpartner")
                            || (row.noteExchange && charSearch.toLowerCase(Locale.ROOT) == "noteexchange")) {
                            resultList.add(row)
                        }
                        /*
                        if (charSearch.toLowerCase(Locale.ROOT).isNotEmpty()) {
                            Log.i("filter", "not empty: " + charSearch)
                            if (!row.teamName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))
                                        && !row.className.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))){
                                    Log.i("filter", "not a valid group bc charsearch: " + row.className)
                                    validGroup = false
                            }
                        }

                        if (row.homeworkHelp && charSearch.toLowerCase(Locale.ROOT) != "homeworkhelp"){
                            Log.i("filter", "not a valid group bc hw: " + row.className)
                            validGroup = false
                        }
                        if (row.examSquad && charSearch.toLowerCase(Locale.ROOT) != "examsquad"){
                            Log.i("filter", "not a valid group bc exam: " + row.className)
                            validGroup = false
                        }
                        if (row.labMates && charSearch.toLowerCase(Locale.ROOT) != "labmate"){
                            Log.i("filter", "not a valid group bc lab: " + row.className)
                            validGroup = false
                        }
                        if (row.projectPartners && charSearch.toLowerCase(Locale.ROOT) != "projectpartner"){
                            Log.i("filter", "not a valid group bc project: " + row.className)
                            validGroup = false
                        }
                        if (row.noteExchange && charSearch.toLowerCase(Locale.ROOT) != "noteexchange"){
                            Log.i("filter", "not a valid group bc notes: " + row.className)
                            validGroup = false
                        }

                        // add it here
                        if (validGroup) {
                            resultList.add(row)
                        }*/
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