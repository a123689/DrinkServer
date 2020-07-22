package com.example.dat.drinksever.Services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.example.dat.drinksever.R;
import com.example.dat.drinksever.ShowOrderActivity;

public class NotificationHelper extends ContextWrapper {
    private static final String DEV_ID = "com.example.dat.drinksever.Services.DAT";
    private static final String DEV_NAME = "Drink Shop Server";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChanel();
    }

    private NotificationManager notificationManager;
    @TargetApi(Build.VERSION_CODES.O)
    private void createChanel() {
        NotificationChannel channel = new NotificationChannel(DEV_ID
                , DEV_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getDrinkShopNotification(String title,
                                                         String message,
                                                         Uri soundUri)
    {
        Intent resultIntent = new Intent(this, ShowOrderActivity.class);
        resultIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ShowOrderActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =  PendingIntent.getActivity(this,0,resultIntent, PendingIntent.FLAG_ONE_SHOT);
        return  new Notification.Builder(getApplicationContext(),DEV_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

    }
}

