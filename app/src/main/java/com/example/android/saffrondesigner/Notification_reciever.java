package com.example.android.saffrondesigner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import java.util.Calendar;
import androidx.core.app.NotificationCompat;

public class Notification_reciever extends BroadcastReceiver {
   DatabaseHelper myDb;


    @Override
    public void onReceive(Context context, Intent intent) {
        myDb=new DatabaseHelper(context);
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1=new Intent(context,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,100,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {500,1000};
        Calendar calendar=Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH)+1;
        int todayDate = calendar.get(Calendar.DATE);
        String todayDateString=Integer.toString(todayDate).trim()+Integer.toString(todayMonth).trim()+Integer.toString(todayYear).trim();
         long countRows=myDb.countRows(todayDateString);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.add)
                .setContentTitle("Saffron")
                .setContentText("Good morning ! You have "+countRows+" orders on today hope you completed all.")
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setVibrate(v);
        notificationManager.notify(100,builder.build());
    }
}