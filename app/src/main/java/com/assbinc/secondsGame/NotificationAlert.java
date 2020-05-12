package com.assbinc.secondsGame;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationAlert extends BroadcastReceiver {

    int notificationId;
    @Override
    public void onReceive(Context context, Intent intent) {

        notificationId = 200;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notificationGame")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.notifTitle))
                .setContentText("this is a test")
                .setTicker("THE 30 seconds game")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent notifIntent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());

    }
}
