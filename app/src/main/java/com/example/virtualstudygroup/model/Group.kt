package com.example.virtualstudygroup.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    val id: String = "",
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
    val leaders: MutableMap<String, Boolean>? = null,
    val members: MutableMap<String, Boolean>? = null,
    val groupDescription: String? = null
) : Parcelable