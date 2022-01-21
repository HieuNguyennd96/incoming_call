package com.example.incommingcallexample

import android.app.Application
import com.example.callkit_java.CallKit

class CallApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CallKit.getInstance(this)
    }
}