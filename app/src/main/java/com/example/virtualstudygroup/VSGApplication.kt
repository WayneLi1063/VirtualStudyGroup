package com.example.virtualstudygroup

import android.app.Application
import com.google.firebase.auth.FirebaseUser

class VSGApplication: Application() {
    var currentUser: FirebaseUser ?= null


}