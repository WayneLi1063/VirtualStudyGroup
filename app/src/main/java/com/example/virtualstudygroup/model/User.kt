package com.example.virtualstudygroup.model

class User(
    val courses: List<String>,
    val email: String,
    val name: String?,
    val year: String?,
    val major: String?,
    val interest: String?,
    val photoURL: String,
    val uid: String
) {
    constructor() : this(listOf(), "", "", "", "", "", "", "")
}