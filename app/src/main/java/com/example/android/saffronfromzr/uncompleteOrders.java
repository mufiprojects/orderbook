package com.example.android.saffronfromzr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.android.saffronfromzr.MainActivity.deliveryDate;
import static com.example.android.saffronfromzr.OrderActivity.DATE_FORMAT;

public class uncompleteOrders extends AppCompatActivity {

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseOrders;
    List<String> unCompletedList=new ArrayList<>();
    ListView unCompletedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uncomplete_orders);
        databaseOrders=database.getReference("orders");
        unCompletedListView=findViewById(R.id.unCompletedList);
        fetchOrders();

    }
    private void fetchOrders()
    {
        Log.d("unOrderNo","runned fetchOrders()");
        databaseOrders.orderByChild("deliveryDate").equalTo(getTomorrowDate()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("unOrderNo","runned child added");
                String orderNo=dataSnapshot.getKey();
                Log.d("unOrderNo",orderNo);
                assert orderNo != null;
                Boolean workComplete=(Boolean) dataSnapshot.child("workComplete").getValue();
                if (!workComplete){
                addUncompleteOrder(orderNo);
                }
                else{
                    addUncompleteOrder("All Orders are completed..Keep Going");
                }

                setAdapter();
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
    private String getTomorrowDate()
    {
        final Calendar calendar = Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        Date date=new GregorianCalendar(year,month,day+2).getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(date);
    }


    private void addUncompleteOrder(String orderNo)
    {
        unCompletedList.add(orderNo);
    }
    private void setAdapter()
    {

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                unCompletedList);

        unCompletedListView.setAdapter(arrayAdapter);



    }

}