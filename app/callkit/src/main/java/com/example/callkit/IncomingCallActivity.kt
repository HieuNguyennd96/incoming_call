package com.example.callkit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class IncomingCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        this.turnScreenOnAndKeyguardOff()

        val callerName = intent.getStringExtra(CallkitService.CALLER_NAME)
        val txtCaller = findViewById<TextView>(R.id.txtName)
        txtCaller.text = callerName

        val number = intent.getStringExtra(CallkitService.NUMBER)
        val txtNumber = findViewById<TextView>(R.id.txtNumber)
        txtNumber.text = number

        val acceptCall = findViewById<ImageView>(R.id.btnAcceptCall)
        acceptCall.setOnClickListener {
            broadcastCallState(HandleCallReceiver.ACTION_ACCEPT_CALL)
            finishAffinity()
        }
        val endCall = findViewById<ImageView>(R.id.btnEndCall)
        endCall.setOnClickListener {
            broadcastCallState(HandleCallReceiver.ACTION_CANCEL_CALL)
            finishAffinity()
        }
    }

    private fun broadcastCallState(action: String) {
        val intent = Intent().apply {
            this.action = action
        }
        sendBroadcast(intent)
    }
}