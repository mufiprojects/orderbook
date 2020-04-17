package com.example.android.saffronfromzr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Function;

import androidx.annotation.NonNull;


import static com.example.android.saffronfromzr.MainActivity.deliveryDate;

public class common {
    private Calendar calendar = Calendar.getInstance();


      static HashMap<String, String> items=new HashMap<>();
     static HashMap<String, String>  userHashMap=new HashMap<>();

 public  static int totalOrder,userTotalOrder,totalCompletedOrders,userCompletedOrder,noOfOrdersInAll,noOfOrdersInUser;
 public static  String currentUser;

    public void currentDate()
    {

         int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);

    }
    public String makeFirstLetterCap(String string)
    {
        try {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        catch (NullPointerException e)
        {
            Log.d("tags","Null PointerException");
            return  e.getMessage();
        }

    }

    public  String getCurrentUser()
    {
         FirebaseAuth mAuth;
         mAuth=FirebaseAuth.getInstance();
         currentUser=mAuth.getCurrentUser().getUid();
            return   mAuth.getCurrentUser().getUid();
    }



    public void putExtra(Intent intent, orderbook orderbook)
    {
        intent.putExtra("orderNo",orderbook.getOrderNo());
        intent.putExtra("designerId",orderbook.getDesignerId());
        intent.putExtra("customerName",orderbook.getCustomerName());
        intent.putExtra("isHandWork",orderbook.getIsHandWork());
        intent.putExtra("orderDate",orderbook.getOrderDateString());
        intent.putExtra("deliveryDate",deliveryDate);
        intent.putExtra("itemsCount",orderbook.getItemCount());
        intent.putExtra("item1",orderbook.getItem1());
        intent.putExtra("item2",orderbook.getItem2());
        intent.putExtra("item3",orderbook.getItem3());

    }
    public void putExtra(Intent intent,String orderNo,String designerId,String customerName,
                         Boolean isHandWork,String orderDate,String deliveryDate,long itemsCount,String item1,String item2,String item3)
    {
        intent.putExtra("orderNo",orderNo);
        intent.putExtra("designerId",designerId);
        intent.putExtra("customerName",customerName);
        intent.putExtra("isHandWork",isHandWork);
        intent.putExtra("orderDate",orderDate);
        intent.putExtra("deliveryDate",deliveryDate);
        intent.putExtra("itemsCount",itemsCount);
        intent.putExtra("item1",item1);
        intent.putExtra("item2",item2);
        intent.putExtra("item3",item3);

    }

    public void fetchNoOfTotalOrders()
    {

        DatabaseReference noOfOrdersComplete= FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child("allOrders").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                noOfOrdersInAll=Integer.valueOf(dataSnapshot.child("totalOrder").getValue().toString());


                Log.d("tabs","total orders in onChilAdded() = "+totalOrder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
    public void fetchNoOfUserTotalOrders()
    {
        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child(getCurrentUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOfOrdersInUser=Integer.valueOf(dataSnapshot.child("totalOrder").getValue().toString());


                Log.d("tabs","user total orders in onChilAdded() = "+userTotalOrder);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void fetchNoOfTotalCompletedOrders()
    {
        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child("allOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOfOrdersInAll=Integer.valueOf(dataSnapshot.child("completedOrder").getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void fetchUserNoOfCompletedOrders()
    {
        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child(getCurrentUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOfOrdersInUser=Integer.valueOf(dataSnapshot.child("completedOrder").getValue().toString());

                Log.d("tabs","user total orders in onChilAdded() = "+userTotalOrder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public int getTotalOrder() {
        return totalOrder;
    }

    public int getUserTotalOrder() {
        return userTotalOrder;
    }

    public int getUserCompletedOrder() {
        return userCompletedOrder;
    }

    public int getTotalCompletedOrders() {
        return totalCompletedOrders;
    }

    public  int getNoOfOrdersInAll() {
        return noOfOrdersInAll;
    }

    public  int getNoOfOrdersInUser() {
        return noOfOrdersInUser;
    }

    public void incrementNoOfOrder(String keyName)
    {
        DatabaseReference noOfOrdersComplete = FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child("allOrders").child(keyName).setValue(getNoOfOrdersInAll()+1);
        noOfOrdersComplete.child(getCurrentUser()).child(keyName).setValue(getNoOfOrdersInUser()+1);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

//        InputMethodManager imm = (InputMethodManager)
//                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm != null){
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//        }
    }
}
