package com.example.android.saffronfromzr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;


import static com.example.android.saffronfromzr.MainActivity.deliveryDate;

 class common {

      static HashMap<String, String> items=new HashMap<>();
      static HashMap<String, String>  userHashMap=new HashMap<>();
      static String lastEntryCustomerName="";
      static String lastEntryDeliveryDate;
      public static int noOfOrdersInAll,noOfOrdersInUser;
      public static  String currentUser;


     String makeFirstLetterCap(String string)
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





    //FROM onClick()
     void putExtra(Intent intent, orderbook orderbook)
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
    //FROM SEARCH
     void putExtra(Intent intent,String orderNo,String designerId,String customerName,
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

     void fetchNoOfTotalOrders()
    {

        DatabaseReference noOfOrdersComplete= FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child("allOrders").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    noOfOrdersInAll = Integer.parseInt(
                            Objects.requireNonNull( Objects.requireNonNull(dataSnapshot.
                                    child("totalOrder").getValue()).toString()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
     void fetchNoOfUserTotalOrders()
    {

        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child(getCurrentUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                noOfOrdersInUser=Integer.parseInt(
                        Objects.requireNonNull( dataSnapshot.child("totalOrder").getValue().toString()));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     void fetchNoOfTotalCompletedOrders()
    {
        DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child("allOrders").child("completedOrder").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                noOfOrdersInAll=Integer.parseInt(Objects.requireNonNull
                                (dataSnapshot.getValue()).toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     void fetchUserNoOfCompletedOrders()
    {
        final DatabaseReference noOfOrdersComplete=FirebaseDatabase.getInstance().getReference("noOfOrdersComplete");
        noOfOrdersComplete.child(getCurrentUser()).child("completedOrder").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                noOfOrdersInUser=Integer.parseInt(
                        Objects.requireNonNull(dataSnapshot.getValue().toString()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


     void incrementNoOfOrder(String keyName)
    {
        DatabaseReference noOfOrdersComplete = FirebaseDatabase.getInstance().
                getReference("noOfOrdersComplete");
        noOfOrdersComplete.child("allOrders").child(keyName).setValue(getNoOfOrdersInAll()+1);
        noOfOrdersComplete.child(getCurrentUser()).child(keyName).setValue(getNoOfOrdersInUser()+1);
    }

    //KEYBOARD OPERATIONS FUNCTIONS
     static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
       // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

     void keyBoardUp(Activity activity)
    {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

    }
     //KEYBOARD OPERATIONS FUNCTIONS

    //GETTERS

     int getNoOfOrdersInAll() {
         return noOfOrdersInAll;
     }

     int getNoOfOrdersInUser() {
         return noOfOrdersInUser;
     }
     String getCurrentUser()
     {
         FirebaseAuth mAuth;
         mAuth=FirebaseAuth.getInstance();
         try {
             currentUser = mAuth.getCurrentUser().getUid();
         }
         catch (NullPointerException e)
         {
             Log.d("exceptions","Common class line 50 "+e);
         }
         return   mAuth.getCurrentUser().getUid();
     }
     //GETTERS
}
