package com.example.android.saffronfromzr;

import android.app.AlarmManager;
import android.app.PendingIntent;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.badoualy.datepicker.TimelineView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static android.media.CamcorderProfile.get;
import static com.example.android.saffronfromzr.OrderActivity.DATE_FORMAT;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    DatabaseHelper myDb;
    RecyclerView orderListRecyclerView;

    TabLayout tabLayout;
    ViewPager viewPager;
    private RecyclerView.LayoutManager layoutManager;
    orderListAdapter orderListAdapter;
    DatePickerTimeline datePicker;

    FirebaseAuth mAuth;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseOrders;

    int userTotalOrder,userCompletedOrder,allTotalOrder,allCompletedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        databaseOrders=database.getReference("orders");


        myDb = new DatabaseHelper(this);
//       Toolbar toolbar = findViewById(R.id.toolbar);
//
//
//       setSupportActionBar(toolbar);
//       notification();
        tabLayout=(TabLayout) findViewById(R.id.tabs);

       viewPager=(ViewPager) findViewById(R.id.viewPager);

       setupViewPager(viewPager);
       createTabItems();
       tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
       viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


           }

           @Override
           public void onPageSelected(int position) {
               if (position==0)
               {
                   tabLayout.getTabAt(0).select();
               }
               else if (position==1)
               {
                   tabLayout.getTabAt(1).select();
               }
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });

        datePicker = findViewById(R.id.datePickerTimeline);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOrderActivity();
            }
        });

//        orderListRecyclerView=findViewById(R.id.orderListRecyclerView);
//        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//        orderListRecyclerView.setLayoutManager(layoutManager);
//        orderListRecyclerView.setHasFixedSize(true);
//        loadData(todayDate());


        final Calendar calendar = Calendar.getInstance();

        final int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        datePicker.setInitialDate(year,month,date);
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                Date date=new GregorianCalendar(year,month,day+1).getTime();
                dateToString(date);


            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {

            }
        });


    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new me_fragment());
        adapter.addFragment(new all_fragment());
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        public  Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }



    public String dateToString(Date selectedDate)
    {

        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateToString=dateFormat.format(selectedDate);
        Toast.makeText(this, dateToString, Toast.LENGTH_SHORT).show();
        return dateToString;
    }
    public void toOrderActivity() {
        Intent orderActivity = new Intent(getApplicationContext(), OrderActivity.class);
        startActivity(orderActivity);
    }
    public void loadData(String selectedDeliveryDate)
    {
//        orderListAdapter=new orderListAdapter(getApplicationContext(),myDb.getData(selectedDeliveryDate));
//        orderListRecyclerView.setAdapter(orderListAdapter);
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

    public int getUserTotalOrder() {

        return userTotalOrder;
    }

    public int getUserCompletedOrder() {
        return userCompletedOrder;
    }

    public int getAllTotalOrder() {

        return allTotalOrder;
    }

    public int getAllCompletedOrder() {
        return allCompletedOrder;
    }

    public void createTabItems() {


       View userTabView = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView userImg=(ImageView)userTabView.findViewById(R.id.tabIcon);
        userImg.setImageResource(R.drawable.man_user);
        TextView tabTitile=(TextView) userTabView.findViewById(R.id.tabTitle);
        tabTitile.setText(R.string.username);
        TextView orderDetails=(TextView) userTabView.findViewById(R.id.orderdetails);
        orderDetails.setText(getUserTotalOrder() +R.string.Outof +getUserCompletedOrder());

        tabLayout.addTab(tabLayout.newTab().setCustomView(userTabView));

        View allUserTabView = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView allUserImg=(ImageView) allUserTabView.findViewById(R.id.tabIcon);
        allUserImg.setImageResource(R.drawable.all_person_icon);
        TextView allUserTabTitile=(TextView) allUserTabView.findViewById(R.id.tabTitle);
        allUserTabTitile.setText(R.string.allusers);
        TextView allUserorderOverView=(TextView) allUserTabView.findViewById(R.id.orderdetails);
        allUserorderOverView.setText(getAllTotalOrder()+ R.string.Outof + getAllCompletedOrder());

        tabLayout.addTab(tabLayout.newTab().setCustomView(allUserTabView));




    }


}





