package com.example.virtualstudygroup.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupChat(
    val className: String,
    val teamName: String,
    val img: String,
    val id: String
) : Parcelable {
    constructor(): this("","","","")
}