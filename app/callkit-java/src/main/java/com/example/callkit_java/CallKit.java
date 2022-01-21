package com.example.callkit_java;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class CallKit {

    private static CallKit instance = null;

    public Context context;
    public CallKitListener listener = null;

    private CallKit(Context context) {
        this.context = context;
    }

    public static CallKit getInstance(Context context) {
        if (instance == null) {
            instance = new CallKit(context);
        }
        return instance;
    }

    public void openIncomingCallScreen(String callerName, String number) {
        Intent intent = new Intent(context, CallkitService.class);
        intent.putExtra(CallkitService.CALLER_NAME, callerName);
        intent.putExtra(CallkitService.NUMBER, number);
        intent.setAction(HandleCallReceiver.ACTION_CREATE_CALL);
        context.startService(intent);
    }

    public void closeIncomingCallScreen() {
        context.stopService(new Intent(context, CallkitService.class));
    }

    public void setCallStatus(int status) {
        if (listener != null) {
            listener.onAcceptCall(status);
        }
    }

    public void setOnCallListener(CallKitListener listener) {
        this.listener = listener;
    }
}
