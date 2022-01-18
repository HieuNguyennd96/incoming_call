package com.example.callkit

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat

class CallkitService : Service() {

    companion object {
        const val CALLER_NAME = "CALLER_NAME"
        const val NUMBER = "NUMBER"
    }

    private var callReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        //Android8から、明示的なブロードキャストか、registerReciever()しか許可されなくなった
        callReceiver = HandleCallReceiver()
        val inf = IntentFilter()
        inf.addAction(HandleCallReceiver.ACTION_ACCEPT_CALL)
        inf.addAction(HandleCallReceiver.ACTION_CANCEL_CALL)
        registerReceiver(callReceiver, inf)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY
        when (intent.action) {
            HandleCallReceiver.ACTION_CREATE_CALL -> {
                createIncomingCallScreen(intent, applicationContext)
            }
            HandleCallReceiver.ACTION_ACCEPT_CALL -> {
                stopSelf()
            }
            HandleCallReceiver.ACTION_CANCEL_CALL -> {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createIncomingCallScreen(intent: Intent, context: Context) {
        val callingIntent = createCallingIntent(intent, context)
        val acceptIntent = createAcceptIntent(context)
        val cancelIntent = createCancelIntent(context)
        val builder = NotificationCompat.Builder(context, "callnotification")
        builder.setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
        builder.setContentIntent(callingIntent)
        builder.setFullScreenIntent(callingIntent, true)
        builder.setContentTitle("Incoming call")
        builder.setContentText("Incoming call")
        builder.setAutoCancel(true)
        // Use builder.addAction(..) to add buttons to answer or reject the call.
        val acceptAction = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.drawable.ic_accept_call),
            "Accept", acceptIntent
        ).build()
        val declineAction = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.drawable.ic_end_call),
            "Decline", cancelIntent
        ).build()
        builder.addAction(acceptAction)
        builder.addAction(declineAction)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "callnotification", "Incoming Calls", NotificationManager.IMPORTANCE_HIGH
            )
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            channel.setSound(
                ringtoneUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId("callnotification")
        }
        val notification = builder.build()
        notification.flags = Notification.FLAG_INSISTENT
        notification.fullScreenIntent = callingIntent
        notificationManager.notify("callnotification", 100, notification)
    }

    private fun createCancelIntent(context: Context): PendingIntent {
        val intent = Intent().apply {
            action = HandleCallReceiver.ACTION_CANCEL_CALL
        }
        return PendingIntent.getBroadcast(
            context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createAcceptIntent(context: Context): PendingIntent {
        val intent = Intent().apply {
            action = HandleCallReceiver.ACTION_ACCEPT_CALL
        }
        return PendingIntent.getBroadcast(
            context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createCallingIntent(intent: Intent, context: Context): PendingIntent {
        intent.apply {
            action = Intent.ACTION_MAIN
            flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
            setClass(context, IncomingCallActivity::class.java)
        }
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        if (callReceiver != null) {
            unregisterReceiver(callReceiver)
            callReceiver = null
        }
        stopForeground(true)
        super.onDestroy()
    }
}