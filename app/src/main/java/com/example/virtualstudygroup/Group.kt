package com.example.virtualstudygroup

data class Group (
    val id: Int = -1,
    val className: String = "",
    val currNumber: Int = 0,
    val totalNumber: Int = 0,
    val teamName: String = "",
    val img: String = "",
    val examSquad: Boolean = false,
    val homeworkHelp: Boolean = false,
    val labMates: Boolean = false,
    val noteExchange: Boolean = false,
    val projectPartners: Boolean = false,
    val members: MutableMap<String, Boolean>? = null
)