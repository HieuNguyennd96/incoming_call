package com.example.callkit_java;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IncomingCallActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        turnScreenOnAndKeyguardOff();

        String callerName = getIntent().getStringExtra(CallkitService.CALLER_NAME);
        TextView txtCaller = findViewById(R.id.txtName);
        txtCaller.setText(callerName);

        String number = getIntent().getStringExtra(CallkitService.NUMBER);
        TextView txtNumber = findViewById(R.id.txtNumber);
        txtNumber.setText(number);
        ImageView acceptCall = findViewById(R.id.btnAcceptCall);
        acceptCall.setOnClickListener(v -> {
            broadcastCallState(HandleCallReceiver.ACTION_ACCEPT_CALL);
            finishAffinity();
        });
        ImageView endCall = findViewById(R.id.btnEndCall);
        endCall.setOnClickListener(v -> {
            broadcastCallState(HandleCallReceiver.ACTION_CANCEL_CALL);
            finishAffinity();
        });
    }

    private void broadcastCallState(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    void turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );
        }
        KeyguardManager manager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.requestDismissKeyguard(this, null);
        }
    }
}
