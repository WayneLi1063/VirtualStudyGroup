package com.example.virtualstudygroup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CreateGroupActivity : AppCompatActivity() {

    private var className: String? = null
    private var courseName: String? = null
    private var currNumber: Int? = null
    private var totalNumber: Int? = null
    private var groupImage: String? = null
    private var examSquad: Boolean? = null
    private var homeworkHelp: Boolean? = null
    private var labMates: Boolean? = null
    private var noteExchange: Boolean? = null
    private var projectPartners: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
    }
}