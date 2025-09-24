package com.app.harcdis.firebase;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.app.harcdis.R;

import com.app.harcdis.adminRole.AdminNewPointScreen;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseNService extends FirebaseMessagingService {
    public static final String TAG = "MyTag";
    String channelId = "Chat";
    String channelIdRequest = "Request";
    Intent intent;
    Intent RequestIntent;
    String user_token;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.d(TAG, "onMessageReceived: " + message.getData());
        super.onMessageReceived(message);
        if (message.getNotification() != null && message.getData().size() == 0) {
            Intent intent = new Intent(this, AdminNewPointScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            String channelId = "Request";

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(defaultSoundUri)
                    .setContentTitle(message.getNotification().getTitle())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText(message.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
            Log.d("msg ", "onMessageReceived: " + pendingIntent);


            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Request channel", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.YELLOW);
                channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, builder.build());

        }

        }


    }

