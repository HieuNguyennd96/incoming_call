package com.example.callkit;

import android.content.BroadcastReceiver;
import android.content.Context
import android.content.Intent

class HandleCallReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_CREATE_CALL = "com.example.callkit.ACTION_CREATE_CALL";
        const val ACTION_ACCEPT_CALL = "com.example.callkit.ACTION_ACCEPT_CALL";
        const val ACTION_CANCEL_CALL = "com.example.callkit.ACTION_CANCEL_CALL";
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action ?: "") {
            ACTION_ACCEPT_CALL -> {
                CallKit.getInstance(context).setCallStatus(1)
                CallKit.getInstance(context).closeIncomingCallScreen()
            }
            ACTION_CANCEL_CALL -> {
                CallKit.getInstance(context).setCallStatus(0)
                CallKit.getInstance(context).closeIncomingCallScreen()
            }
        }
    }
}
