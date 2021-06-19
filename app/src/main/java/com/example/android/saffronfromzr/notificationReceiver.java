package com.example.android.saffronfromzr;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import java.util.Calendar;
import androidx.core.app.NotificationCompat;


public class notificationReceiver extends BroadcastReceiver {
    static long orderCount;



    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelId="morningnotification";
        CharSequence channelName="saffron notifications";
        int importance=NotificationManager.IMPORTANCE_HIGH;
        Intent intent1=new Intent(context,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,100,intent1,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] v = {500,1000};

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            AudioAttributes attributes=new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);

            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 300, 200, 400});
            notificationChannel.setSound(notificationSound, attributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.all_person_icon)
                .setContentTitle("Good morning!")
                .setContentText("You have "+orderCount+" orders on today hope you completed all.")
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setVibrate(v);
        notificationManager.notify(100,builder.build());

    }

    public static void setOrderCount(long orderCount) {
        notificationReceiver.orderCount = orderCount;
    }
}