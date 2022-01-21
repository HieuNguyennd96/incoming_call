package com.example.callkit_java;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

public class CallkitService extends Service {

    public static String CALLER_NAME = "CALLER_NAME";
    public static String NUMBER = "NUMBER";

    private BroadcastReceiver callReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        callReceiver = new HandleCallReceiver();
        IntentFilter inf = new IntentFilter();
        inf.addAction(HandleCallReceiver.ACTION_ACCEPT_CALL);
        inf.addAction(HandleCallReceiver.ACTION_CANCEL_CALL);
        registerReceiver(callReceiver, inf);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        switch (intent.getAction()) {
            case HandleCallReceiver.ACTION_CREATE_CALL: {
                createIncomingCallScreen(intent, getApplicationContext());
                break;
            }
            case HandleCallReceiver.ACTION_ACCEPT_CALL:
            case HandleCallReceiver.ACTION_CANCEL_CALL: {
                stopSelf();
                break;
            }
        }
        return START_NOT_STICKY;
    }

    private void createIncomingCallScreen(Intent intent, Context context) {
        PendingIntent callingIntent = createCallingIntent(intent, context);
        PendingIntent acceptIntent = createAcceptIntent(context);
        PendingIntent cancelIntent = createCancelIntent(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "callnotification");
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentIntent(callingIntent);
        builder.setFullScreenIntent(callingIntent, true);
        builder.setContentTitle("Incoming call");
        builder.setContentText(intent.getStringExtra(CALLER_NAME));
        builder.setAutoCancel(true);
        // Use builder.addAction(..) to add buttons to answer or reject the call.
        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                IconCompat.createWithResource(context, R.drawable.ic_accept_call),
                "Accept", acceptIntent
        ).build();
        NotificationCompat.Action declineAction = new NotificationCompat.Action.Builder(
                IconCompat.createWithResource(context, R.drawable.ic_end_call),
                "Decline", cancelIntent
        ).build();
        builder.addAction(acceptAction);
        builder.addAction(declineAction);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "callnotification", "Incoming Calls", NotificationManager.IMPORTANCE_HIGH
            );
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            channel.setSound(
                    ringtoneUri, new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("callnotification");
        }
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_INSISTENT;
        notification.fullScreenIntent = callingIntent;
        startForeground(100, notification);
    }

    private PendingIntent createCancelIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(HandleCallReceiver.ACTION_CANCEL_CALL);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createAcceptIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(HandleCallReceiver.ACTION_ACCEPT_CALL);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createCallingIntent(Intent intent, Context context) {
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, IncomingCallActivity.class);
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
            callReceiver = null;
        }
        stopForeground(true);
        super.onDestroy();
    }
}
