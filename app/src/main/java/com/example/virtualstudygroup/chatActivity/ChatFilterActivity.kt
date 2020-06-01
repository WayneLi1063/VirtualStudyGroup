package com.example.virtualstudygroup.chatActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.chatActivity.ChatLogActivity.Companion.CHATAG
import com.example.virtualstudygroup.model.ChatFilter
import kotlinx.android.synthetic.main.activity_chat_filter.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_message.chat_toolbar

class ChatFilterActivity : AppCompatActivity() {

    companion object {
        val FILTER_KEY = "Filter Key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_filter)

        setSupportActionBar(chat_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Filter"

        btn_chat_filter.setOnClickListener{
            Log.i(CHATAG, "chat filter is clicked")
            performFilter()
        }
    }

    private fun performFilter() {
        val intent = Intent(this, MessageActivity::class.java)
        val chatFilter =
            ChatFilter(
                et_chat_group_name.text.toString(),
                et_chat_class_name.text.toString(),
                cb_exam_squad.isChecked,
                cb_homework_help.isChecked,
                cb_lab_mates.isChecked,
                cb_note_exchange.isChecked,
                cb_project_partners.isChecked)
        intent.putExtra(FILTER_KEY, chatFilter)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
