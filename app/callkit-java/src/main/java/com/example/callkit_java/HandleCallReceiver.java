package com.example.callkit_java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HandleCallReceiver extends BroadcastReceiver {

    public static final String ACTION_CREATE_CALL = "com.example.callkit.ACTION_CREATE_CALL";
    public static final String ACTION_ACCEPT_CALL = "com.example.callkit.ACTION_ACCEPT_CALL";
    public static final String ACTION_CANCEL_CALL = "com.example.callkit.ACTION_CANCEL_CALL";


    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_ACCEPT_CALL: {
                CallKit.getInstance(context).setCallStatus(1);
                CallKit.getInstance(context).closeIncomingCallScreen();
                break;
            }
            case ACTION_CANCEL_CALL: {
                CallKit.getInstance(context).setCallStatus(0);
                CallKit.getInstance(context).closeIncomingCallScreen();

            }
            default:
                break;
        }
    }
}
