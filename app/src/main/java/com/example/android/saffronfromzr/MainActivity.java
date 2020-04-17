package com.example.android.saffronfromzr;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.badoualy.datepicker.TimelineView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.function.Function;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static android.media.CamcorderProfile.get;
import static com.example.android.saffronfromzr.OrderActivity.DATE_FORMAT;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addSearchBtn;

    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayout searchLayout;
    TextInputEditText searchText;
    MaterialButton cancel_btn;
    MaterialButton go_btn;

    DatePickerTimeline datePicker;


    FirebaseAuth mAuth;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseItems;
    DatabaseReference databaseUsers;

    //Variables
    static String deliveryDate;
    int viewPagerPosition;
    String searchOrderNo;

    //objects
    common common=new common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        common.getCurrentUser();

        fetchItems();
        fetchUsers();
        common.fetchNoOfUserTotalOrders();
        common.fetchUserNoOfCompletedOrders();
        common.fetchNoOfTotalOrders();
        common.fetchNoOfTotalCompletedOrders();
        createTabItems();
        viewPager= findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


           }

           @Override
           public void onPageSelected(int position) {
               if (position==0)
               {
                   try {
                       tabLayout.getTabAt(0).select();
                   }
                   catch (NullPointerException n)
                   {
                       Log.d("exceptions","MainActivity line 101",n);
                   }
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
        viewPagerPosition=viewPager.getCurrentItem();
        datePicker = findViewById(R.id.datePickerTimeline);
        addSearchBtn = findViewById(R.id.fabBtn);
        addSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAddSarchBtn();
                showSearchLayout();
                goBtnListerner();
                hideTab();
                goneDatePicker();
                hideViewPager();

            }
        });

        final Calendar calendar = Calendar.getInstance();

        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);

        datePicker.setInitialDate(year,month,day);
        Date date=new GregorianCalendar(year,month,day+1).getTime();
        deliveryDate=dateToString(date);
        setupViewPager(viewPager);
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                Date date=new GregorianCalendar(year,month,day+1).getTime();
                deliveryDate=dateToString(date);
                setupViewPager(viewPager);
            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {

            }
        });


    }

    private void goBtnListerner() {
        go_btn=findViewById(R.id.go_button);
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String orderNo=getSearchOrderNo();
                final DatabaseReference databaseOrders = FirebaseDatabase.getInstance().getReference("orders");
               databaseOrders.child(orderNo).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if (dataSnapshot.getValue() == null) {
                           Intent orderActivity = new Intent(getApplicationContext(),OrderActivity.class);
                           orderActivity.putExtra("orderNo",getSearchOrderNo());
                           Toast.makeText(MainActivity.this, getSearchOrderNo(), Toast.LENGTH_SHORT).show();
                           startActivity(orderActivity);
                       } else {
                           String designerId = (String) dataSnapshot.child("designerId").getValue();
                           String customerName = (String) dataSnapshot.child("customerName")
                                   .getValue();
                           Boolean isHandWork = (Boolean) dataSnapshot.child("handWork").getValue();
                           String orderDate = (String) dataSnapshot.child("orderDate").getValue();

                           long itemsCount = dataSnapshot.child("items").getChildrenCount();
                           String item1 = (String) dataSnapshot.child("items").child("item1")
                                   .getValue();
                           String item2=null;
                           String item3=null;

                           if (itemsCount == 2) {
                                item2 = (String) dataSnapshot.child("items").child("item2").
                                       getValue();
                           } else if (itemsCount >= 3) {
                                item2 = (String) dataSnapshot.child("items").child("item2")
                                       .getValue();

                                item3 = (String) dataSnapshot.child("items").child("item3")
                                       .getValue();
                           }
                           Intent orderDetailsActivity = new Intent(getApplicationContext(),
                                   orderDetailsActivity.class);
                           common.putExtra(orderDetailsActivity,orderNo,designerId,customerName,
                                   isHandWork,orderDate,deliveryDate,itemsCount,item1,item2,item3);
                           startActivity(orderDetailsActivity);
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

            }
        });
    }


    private void hideViewPager() {
        viewPager.setVisibility(View.INVISIBLE);
    }

    private void goneDatePicker() {
        MaterialCardView datePickerContainer;
        datePickerContainer=findViewById(R.id.timelineContainer);
        datePickerContainer.setVisibility(View.GONE);
    }

    private void hideTab() {
        tabLayout=findViewById(R.id.tabs);
        tabLayout.setVisibility(View.INVISIBLE);
    }

    private void showSearchLayout() {
        searchLayout=findViewById(R.id.searchLayout);
        searchLayout.setVisibility(View.VISIBLE);
        searchText=findViewById(R.id.searchText);
        searchText.requestFocus();
//        keyBoardUp();
        setGo_btn();
        setCancel_btn();
    }

    private void hideAddSarchBtn() {
        addSearchBtn.setVisibility(View.INVISIBLE);
    }

    private void setupViewPager(ViewPager viewPager)
    {

        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new me_fragment());
        adapter.addFragment(new all_fragment());
        viewPager.setAdapter(adapter);
        setViewPagerWithTabPosition();
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



    private void setViewPagerWithTabPosition() {
        if (tabLayout.getSelectedTabPosition()==0) {
            viewPager.setCurrentItem(0);
        }
        else
        {
            viewPager.setCurrentItem(1);
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
    public void loadData(Date selectedDate)
    {
        all_fragment all_fragment=new all_fragment();
        all_fragment.deliveryDate=dateToString(selectedDate);
        Log.d("deliveryDate=",all_fragment.deliveryDate=dateToString(selectedDate));
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
//    public void notification()
//    {
//
//       long systemTime=System.currentTimeMillis();
//        Calendar calendar=Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY,11);
//        calendar.set(Calendar.MINUTE,10);
//        calendar.set(Calendar.SECOND,0);
//        Intent intent=new Intent(getApplicationContext(),Notification_reciever.class);
//        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
//        if(systemTime <= calendar.getTimeInMillis())
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent );
//    }



    public void createTabItems() {



        tabLayout= findViewById(R.id.tabs);



       View userTabView = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView userImg=(ImageView)userTabView.findViewById(R.id.tabIcon);
        userImg.setImageResource(R.drawable.man_user);
        TextView tabTitile=(TextView) userTabView.findViewById(R.id.tabTitle);
        tabTitile.setText(R.string.username);
      final TextView orderDetails=(TextView) userTabView.findViewById(R.id.orderdetails);
        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child(common.getCurrentUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               int  userTotalOrder=Integer.valueOf(dataSnapshot.child("totalOrder").getValue().toString());
               int userCompletedOrder=Integer.valueOf(dataSnapshot.child("completedOrder").getValue().toString());
                orderDetails.setText(getString(R.string.tabBelow,String.valueOf(userTotalOrder),String.valueOf(userCompletedOrder)));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setCustomView(userTabView));

        View allUserTabView = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView allUserImg=(ImageView) allUserTabView.findViewById(R.id.tabIcon);
        allUserImg.setImageResource(R.drawable.all_person_icon);
        TextView allUserTabTitile=(TextView) allUserTabView.findViewById(R.id.tabTitle);
        allUserTabTitile.setText(R.string.allusers);
     final    TextView allUserorderOverView=(TextView) allUserTabView.findViewById(R.id.orderdetails);
        noOfOrdersComplete.child("allOrders").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalOrder=Integer.valueOf(dataSnapshot.child("totalOrder").getValue().toString());
                int completedOrder=Integer.valueOf(dataSnapshot.child("completedOrder").getValue().toString());
                allUserorderOverView.setText(getString(R.string.tabBelow,String.valueOf(totalOrder),String.valueOf(completedOrder)));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        tabLayout.addTab(tabLayout.newTab().setCustomView(allUserTabView));




    }
    private void fetchItems()
    {
        databaseItems=database.getReference("items");
        databaseItems.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists())
                    try {


                        common.items.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                    }
                    catch (NullPointerException e)
                    {
                        Toast.makeText(MainActivity.this, "Loading Items Failed..", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
private void fetchUsers(){
    databaseUsers=database.getReference("users");
    databaseUsers.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            common.userHashMap.put(dataSnapshot.getKey(), dataSnapshot.child("name").getValue()
                    .toString());

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}

    public String getSearchOrderNo() {
        searchText=findViewById(R.id.searchText);
        return searchOrderNo=searchText.getText().toString().trim();
    }
    public void setGo_btn()
    {
        searchText=findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!getSearchOrderNo().isEmpty())
                {
                    showGoBtn();
                }
                else {
                    hideGoBtn();
                }


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!getSearchOrderNo().isEmpty())
                {
                    showGoBtn();
                }
                else {
                    hideGoBtn();
                }


            }
        });


    }
    private void setCancel_btn()
    {
        cancel_btn=findViewById(R.id.cancel_button);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                common.hideKeyboard(MainActivity.this);
                emptySearchText();
                showAddSearch();
                goneSearchLayout();
                showTab();
                showDatePicker();
                showViewPager();
            }
        });
    }

    private void emptySearchText() {
        searchText=findViewById(R.id.searchText);
        searchText.setText("");
    }

    private void showViewPager() {
        viewPager.setVisibility(View.VISIBLE);
    }

    private void showDatePicker() {
        MaterialCardView datePickerContainer;
        datePickerContainer=findViewById(R.id.timelineContainer);
        datePickerContainer.setVisibility(View.VISIBLE);
    }

    private void showTab() {
        tabLayout=findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void goneSearchLayout() {
        searchLayout=findViewById(R.id.searchLayout);
        searchLayout.setVisibility(View.GONE);
    }

    private void showAddSearch() {
        addSearchBtn.setVisibility(View.VISIBLE);
    }

    private void showGoBtn() {
        go_btn=findViewById(R.id.go_button);
        go_btn.setVisibility(View.VISIBLE);
    }
    private void hideGoBtn() {
        go_btn=findViewById(R.id.go_button);
        go_btn.setVisibility(View.INVISIBLE);
    }
    public void keyBoardUp()
    {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

    }

}