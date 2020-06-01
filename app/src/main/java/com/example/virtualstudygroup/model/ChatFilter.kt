package com.example.virtualstudygroup.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatFilter(
    val className: String,
    val teamName: String,
    val examSquad: Boolean,
    val homeWorkHelp: Boolean,
    val labMates: Boolean,
    val noteExchange: Boolean,
    val projectPartners: Boolean
) : Parcelable {
    constructor(): this("", "", false, false, false, false, false)
}