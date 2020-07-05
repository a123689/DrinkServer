package com.example.dat.drinksever.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.dat.drinksever.R;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.ShowOrderActivity;
import com.example.dat.drinksever.Utils.Common;
import com.example.dat.drinksever.ViewOrderDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateTokenToServer(s);
    }

    private void updateTokenToServer(String token) {
        IDrinkShopAPI mService = Common.getAPI();

        mService.updateToken("server_app_01", token, "1")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("DEBUG", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("DEBUG", t.getMessage());
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationAPI26(remoteMessage);
            } else {
                sendNotification(remoteMessage);
            }
        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        //get information from message
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Intent resultIntent = new Intent(this, ShowOrderActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ShowOrderActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(new Random().nextInt(), builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        //get information from message
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        //From API level 26 , we need implement Notification Chanel

        NotificationHelper helper;
        Notification.Builder builder;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        builder = helper.getDrinkShopNotification(title, message, defaultSoundUri);

        helper.getManager().notify(new Random().nextInt(), builder.build());


    }
}
