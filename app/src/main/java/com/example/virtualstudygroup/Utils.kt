package com.example.virtualstudygroup

import android.content.Context

fun Context.getApp(): VSGApplication {
    val app = (applicationContext as VSGApplication)
    return app
}