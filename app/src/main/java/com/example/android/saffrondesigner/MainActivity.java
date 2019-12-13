package com.example.android.saffrondesigner;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.badoualy.datepicker.TimelineView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.media.CamcorderProfile.get;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    DatabaseHelper myDb;
    RecyclerView orderListRecyclerView;
    TabLayout tabLayout;
    private RecyclerView.LayoutManager layoutManager;
     orderListAdapter orderListAdapter;

         DatePickerTimeline datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
//       Toolbar toolbar = findViewById(R.id.toolbar);
//
//
//       setSupportActionBar(toolbar);
       notification();
       createTabItems();
       tabLayout=(TabLayout) findViewById(R.id.tabs);
        datePicker = findViewById(R.id.datePickerTimeline);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOrderActivity();
            }
        });

        orderListRecyclerView=findViewById(R.id.orderListRecyclerView);
        layoutManager=new LinearLayoutManager(this);
        orderListRecyclerView.setLayoutManager(layoutManager);
        orderListRecyclerView.setHasFixedSize(true);
        loadData(todayDate());


        Calendar calendar = Calendar.getInstance();

        int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        datePicker.setInitialDate(year,month,date);
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                String selectedDeliveryDate=Integer.toString(day)+Integer.toString(month+1)+Integer.toString(year);
                loadData(selectedDeliveryDate);

            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {

            }
        });


    }

    public void toOrderActivity() {
        Intent orderActivity = new Intent(getApplicationContext(), OrderActivity.class);
        startActivity(orderActivity);
    }
    public void loadData(String selectedDeliveryDate)
    {
        orderListAdapter=new orderListAdapter(getApplicationContext(),myDb.getData(selectedDeliveryDate));
        orderListRecyclerView.setAdapter(orderListAdapter);
    }
    public String todayDate()
    {
        Calendar calendar=Calendar.getInstance();
        int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH)+1;
        int year=calendar.get(Calendar.YEAR);
     String todayDateString =Integer.toString(date).trim()+Integer.toString(month).trim()+Integer.toString(year).trim();
     return todayDateString;


    }
    public void notification()
    {

       long systemTime=System.currentTimeMillis();
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.MINUTE,10);
        calendar.set(Calendar.SECOND,0);
        Intent intent=new Intent(getApplicationContext(),Notification_reciever.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        if(systemTime <= calendar.getTimeInMillis())
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent );


    }
    public void createTabItems() {
       View view = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView customImageView=(ImageView)view.findViewById(R.id.tabIcon);
        customImageView.setImageResource(R.drawable.man_user);
        TextView tabTitile=(TextView) view.findViewById(R.id.tabTitle);
        tabTitile.setText(R.string.username);
        TextView orderDetails=(TextView) view.findViewById(R.id.orderdetails);
        orderDetails.setText(R.string.orderdetails);

//        try {
            tabLayout.addTab(tabLayout.newTab().setCustomView(view));
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }

    }

    }



