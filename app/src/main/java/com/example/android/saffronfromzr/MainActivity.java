package com.example.android.saffronfromzr;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Objects;
import java.util.TimeZone;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import static com.example.android.saffronfromzr.OrderActivity.DATE_FORMAT;
import static com.example.android.saffronfromzr.common.hideKeyboard;
import static com.example.android.saffronfromzr.common.items;
import static com.example.android.saffronfromzr.common.userHashMap;

public class MainActivity extends AppCompatActivity {

    //XML
    FloatingActionButton addSearchBtn;

    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayout searchLayout;
    TextInputEditText searchText;
    MaterialButton cancel_btn;
    MaterialButton go_btn;

    DatePickerTimeline datePicker;

    //FIREBASE OBJECTS
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseItems;
    DatabaseReference databaseUsers;

    //Variables
    static String deliveryDate;

    String searchOrderNo;

    //objects
    common common=new common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchUserState();
        startService(new Intent(this,backgroundService.class));
        viewPager= findViewById(R.id.viewPager);
        datePicker = findViewById(R.id.datePickerTimeline);
        addSearchBtn = findViewById(R.id.fabBtn);

        common.getCurrentUser(); // FETCHING CURRENT USER
        fetchItems();  // Fetching all items in items tree to items hash map
        fetchUsers(); // Fetching all users in users tree to users hash map
        createTabItems(); // create tabs
//        setupViewPager(viewPager);
        addSearchBtnListener();//Listerner function when clicks on addsearch button



        final Calendar calendar = Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        datePicker.setInitialDate(year,month,day);
        //SET DATE PICKER INITIAL DATE AS SYSTEM DATE
        Date date=new GregorianCalendar(year,month,day+1).getTime();
        deliveryDate=dateToString(date);
        // STATIC VARIABLE MAKE THIS TODAY DATE
        setupViewPager(viewPager);
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                Date date=new GregorianCalendar(year,month,day+1).getTime();
                deliveryDate=dateToString(date);
                // STATIC VARIABLE MAKE THIS SELECTED  DATE
                setupViewPager(viewPager);
            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek,
                                               boolean isDisabled) {

            }
        });


    }
    private void addSearchBtnListener() {
        addSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAddSearchBtn();
                showSearchLayout();
                goBtnListener();
                hideTab();
                goneDatePicker();
                hideViewPager();

            }
        });
    }

    //THIS FUNCTION PERFORM GO FUNCTION
    private void goBtnListener() {
        go_btn=findViewById(R.id.go_button);
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String orderNo=getSearchOrderNo();
                final DatabaseReference databaseOrders = FirebaseDatabase.getInstance()
                        .getReference("orders");
                //CHECKING ORDER NER IS IN ORDERS TREE
                //IF NO GO TO ORDER ACTIVITY
                //ELSE GO TO ORDER DETAILS ACTIVITY WITH ALL THAT CHILDS
               databaseOrders.child(orderNo).addListenerForSingleValueEvent(new ValueEventListener()
               {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if (dataSnapshot.getValue() == null) {
                           //ADD ORDER IF THERE NO SUCH ORDER IN ORDERS TREE
                           Intent orderActivity = new Intent(getApplicationContext(),
                                   OrderActivity.class);
                           orderActivity.putExtra("orderNo",getSearchOrderNo());

                         startActivity(orderActivity);
                       } else {
                           String designerId = (String) dataSnapshot.child("designerId").getValue();
                           String customerName = (String) dataSnapshot.child("customerName")
                                   .getValue();
                           Boolean isHandWork = (Boolean) dataSnapshot.child("handWork").getValue();
                           String orderDate = (String) dataSnapshot.child("orderDate").getValue();
                           String delDate = (String) dataSnapshot.child("deliveryDate").getValue();


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
                                   isHandWork,orderDate,delDate,itemsCount,item1,item2,item3);
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

    //Listerner functions


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
        setGo_btn();
        setCancel_btn();
    }

    private void hideAddSearchBtn() {
        addSearchBtn.setVisibility(View.INVISIBLE);
    }



    //VIEW PAGER FUNCTIONS
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();


        private ViewPagerAdapter(FragmentManager manager) {
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

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    private void setupViewPager(ViewPager viewPager)
    {

        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new me_fragment());
        adapter.addFragment(new all_fragment());
        viewPager.setAdapter(adapter);
        setViewPagerWithTabPosition();
        setTabPositionWithViewPager();

    }
    //SETTING TAB POSITION CORRESPONDING WITH VIEW PAGER POSITION
    private void setTabPositionWithViewPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position==0)
                {
                    try {
                        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                    }
                    catch (NullPointerException n)
                    {
                        Log.d("exceptions","MainActivity line 101",n);
                    }
                }
                else if (position==1)
                {
                    Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
    //VIEW PAGER FUNCTIONS




    public void createTabItems() {
        tabLayout= findViewById(R.id.tabs);
        //Setting tab1 item
        View userTabView = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView userImg= userTabView.findViewById(R.id.tabIcon);
        userImg.setImageResource(R.drawable.man_user);
        TextView tabTitle= userTabView.findViewById(R.id.tabTitle);
        tabTitle.setText(R.string.username);
        final TextView userOrderCompleteNoText=userTabView.findViewById(R.id.orderdetails);
        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().
                getReference("noOfOrdersComplete");
        noOfOrdersComplete.child(common.getCurrentUser()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int userTotalOrder=0;
                int userCompletedOrder=0;
                if(dataSnapshot.exists()){
                    try {

                        userTotalOrder = Integer.parseInt(Objects.requireNonNull(
                                dataSnapshot.child("totalOrder")
                                .getValue()).toString());
                        userCompletedOrder = Integer.parseInt( Objects.requireNonNull
                                (dataSnapshot.child("completedOrder").getValue()).toString());
                        userOrderCompleteNoText.setText(getString(R.string.tabBelow,
                                String.valueOf(userTotalOrder), String.valueOf(userCompletedOrder)));
                    }
                    catch (NullPointerException e)
                    {
                        Log.d("exceptions","MainActivity line 373");
                    }

            }
            else{
                    userOrderCompleteNoText.setText("");


            }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setCustomView(userTabView));
        //Setting tab2 item
        View allUserTabView = LayoutInflater.from(this).inflate(R.layout.tabitem,null);
        ImageView allUserImg=allUserTabView.findViewById(R.id.tabIcon);
        allUserImg.setImageResource(R.drawable.all_person_icon);
        TextView allUserTabTitle=allUserTabView.findViewById(R.id.tabTitle);
        allUserTabTitle.setText(R.string.allusers);
        final TextView allOrderCompleteNoText=allUserTabView.findViewById(R.id.orderdetails);
        noOfOrdersComplete.child("allOrders").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalOrder=0;
                int completedOrder=0;
                if (dataSnapshot.exists()) {
                    try {
                        totalOrder = Integer.parseInt(Objects.requireNonNull
                                (dataSnapshot.child("totalOrder").getValue()).toString());
                        completedOrder = Integer.parseInt(Objects.requireNonNull
                                (dataSnapshot.child("completedOrder").getValue()).toString());
                        allOrderCompleteNoText.setText(getString(R.string.tabBelow, String.valueOf(totalOrder), String.valueOf(completedOrder)));
                    }catch (NullPointerException e){Log.d("exceptions","NullpointerException");}
                }
                else
                allOrderCompleteNoText.setText(" ");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        tabLayout.addTab(tabLayout.newTab().setCustomView(allUserTabView));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));




    }
    private void fetchItems()
    {
        databaseItems=database.getReference("items");
        databaseItems.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists())
                    try {

                        items.put(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                    }
                    catch (NullPointerException e)
                    {
                        Toast.makeText(MainActivity.this, "Loading Items Failed..",
                                Toast.LENGTH_SHORT).show();
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
            userHashMap.put(dataSnapshot.getKey(), (String) dataSnapshot.child("name").getValue());

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

    private void fetchUserState() {
        DatabaseReference databaseUserState = database.getReference("activeUsers");

        databaseUserState.child(common.getCurrentUser())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean userState=false;
                        if (dataSnapshot.exists())
                        userState = (Boolean) dataSnapshot.getValue();
                        if (!userState) {
                            hideTab();
                            goneDatePicker();
                            hideViewPager();
                            hideAddSearchBtn();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
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
                hideKeyboard(MainActivity.this);
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

    // GETTER FUNCTIONS
    public String dateToString(Date selectedDate)
    {

        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateToString=dateFormat.format(selectedDate);
        return dateToString;
    }
    public String getSearchOrderNo() {
        searchText=findViewById(R.id.searchText);
        return searchOrderNo=searchText.getText().toString().trim();
    }

    //GETTER FUNCTIONS
}