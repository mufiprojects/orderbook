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

public class Notification_reciever extends BroadcastReceiver {
   DatabaseHelper myDb;


    @Override
    public void onReceive(Context context, Intent intent) {
        myDb=new DatabaseHelper(context);
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelId="morningnotification";
        CharSequence channelName="saffron notifications";
        int importanc=NotificationManager.IMPORTANCE_HIGH;
        Intent intent1=new Intent(context,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,100,intent1,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] v = {500,1000};
        Calendar calendar=Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH)+1;
        int todayDate = calendar.get(Calendar.DATE);
        String todayDateString=Integer.toString(todayDate).trim()+Integer.toString(todayMonth).trim()+Integer.toString(todayYear).trim();
         long countRows=myDb.countRows(todayDateString);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            AudioAttributes attributes=new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importanc);
            notificationChannel.enableLights(true);

            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 300, 200, 400});
            notificationChannel.setSound(notificationSound, attributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }
            NotificationCompat.Builder builder=new NotificationCompat.Builder(context,channelId)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.saffronappbarlogo)
                    .setContentTitle("Good morning!")
                    .setContentText("You have "+countRows+" orders on today hope you completed all.")
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setVibrate(v);
            notificationManager.notify(100,builder.build());

    }
}