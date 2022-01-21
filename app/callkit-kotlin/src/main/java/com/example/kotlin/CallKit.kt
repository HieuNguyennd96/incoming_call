package com.example.kotlin

import SingletonHolder
import android.content.Context
import android.content.Intent

class CallKit private constructor(val context: Context) {
    var listener: CallKitListener? = null

    fun openIncomingCallScreen(caller: String? = "Hieu Nguyen", number: String? = "123456789") {
        val intent = Intent(context, CallkitService::class.java).apply {
            putExtra(CallkitService.CALLER_NAME, caller)
            putExtra(CallkitService.NUMBER, number)
            action = HandleCallReceiver.ACTION_CREATE_CALL
        }
        context.startService(intent)
    }

    fun closeIncomingCallScreen() {
        context.stopService(Intent(context, CallkitService::class.java))
    }

    fun setCallStatus(status: Int) {
        listener?.onAcceptCall(status);
    }

    fun setOnCallListener(listener: CallKitListener?) {
        this.listener = listener
    }

    companion object : SingletonHolder<CallKit, Context>(::CallKit)
}