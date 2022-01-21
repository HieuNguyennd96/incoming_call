package com.example.incommingcallexample

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.callkit_java.CallKit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startCall = findViewById<TextView>(R.id.btn_start_call)
        startCall.setOnClickListener {
            CallKit.getInstance(this@MainActivity).setOnCallListener { status ->
                Log.e("onAcceptCall", "status: $status")
            }
            CallKit.getInstance(this@MainActivity)
                .openIncomingCallScreen("Hieu Nguyen", "123456789")
        }
    }


}