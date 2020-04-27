package com.example.android.saffronfromzr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import static com.example.android.saffronfromzr.OrderActivity.DATE_FORMAT;

public class backgroundService extends Service
{
    //Todo(12) Make this optimise,There are some drawbacks for notification . optimise in future
    DatabaseReference databaseOrders=FirebaseDatabase.getInstance().getReference("orders");
    FirebaseAuth auth;
    Calendar calendar=Calendar.getInstance();


     long  noOfOrder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        fetchOrderNo();
        return START_STICKY;
    }

    private void fetchOrderNo() {
        int day= calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        Date date=new GregorianCalendar(year,month,day+1).getTime();
        databaseOrders.orderByChild("designerId_deliveryDate")
                .equalTo(getCurrentUser()+dateToString(date)).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        noOfOrder=dataSnapshot.getChildrenCount();
                        notificationReceiver.setOrderCount(noOfOrder);
                        createNotification();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private String getCurrentUser()
    {
        auth=FirebaseAuth.getInstance();
        return auth.getCurrentUser().getUid();
    }
    public void createNotification()
    {

        Intent intent=new Intent(getApplicationContext(),notificationReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),
                100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager
//        .INTERVAL_DAY,pendingIntent );

    }
    public String dateToString(Date selectedDate)
    {

        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(selectedDate);
    }


}
