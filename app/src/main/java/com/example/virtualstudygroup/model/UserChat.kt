package com.example.virtualstudygroup.model

data class UserChat(
    val email: String?,
    val name: String?,
    val photoURL: String,
    val uid: String
) {
    constructor(): this("","","","")
}