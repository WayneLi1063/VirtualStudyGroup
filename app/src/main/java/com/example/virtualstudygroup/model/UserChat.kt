package com.example.virtualstudygroup.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserChat(
    val email: String?,
    val name: String?,
    val photoURL: String,
    val uid: String
) : Parcelable {
    constructor(): this("","","","")
}