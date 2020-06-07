package com.example.virtualstudygroup.chatActivity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.virtualstudygroup.R
import com.example.virtualstudygroup.getApp
import com.example.virtualstudygroup.model.ChatMessage
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class NotificationManager(private val context: Context) {

    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        createFunChannel()
    }

    fun sendOneMessage(chatMessage: ChatMessage) {
        if (chatMessage.fromId == context.getApp().currentUser?.uid) return


        val intent = Intent(context, MessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingDealsIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_chat_black_24dp)
            .setContentTitle("New message")
            .setContentText(chatMessage.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingDealsIntent)
            .setAutoCancel(true)
            .build()

        notificationManagerCompat.notify(Random.nextInt(), notification)
    }

    private fun createFunChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Annoying Notifications"
            val descriptionText = "Some annoying message from an ex"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val MESSAGE_STR = "MESSAGE_STR"
    }

    private var notificationListener: ChildEventListener?= null
    fun startSendingNotification() {
        notificationListener = object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?:return

                context.getApp().notificationManager?.sendOneMessage(chatMessage)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        notificationListener?.let {
            FirebaseDatabase.getInstance().getReference("/latest-messages")
                .addChildEventListener(it)
        }
    }

    fun stopSendingNotification() {
        notificationListener?.let {
            FirebaseDatabase.getInstance().getReference("/latest-messages")
                .removeEventListener(it)
        }
        notificationListener = null
    }
}