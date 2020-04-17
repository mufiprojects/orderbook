package com.example.android.saffronfromzr;

import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class orderbook  {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseUsers;
     private String customerName,itemName,orderDateString,orderNo,desigenrId,designerName,item1,item2,item3;
     private Boolean isHandWork,isWorkComplete;
    private long itemCount;

    public  orderbook(){


    }
    public orderbook(String designerName,String desigenrId,Boolean isWorkComplete,String orderNo,String customerName,String orderDateString,Boolean isHandWork ,String item1,long itemCount)
    {

        this.itemCount=itemCount;
        this.item1=item1;
        this.designerName=designerName;
        this.desigenrId=desigenrId;
        this.isWorkComplete=isWorkComplete;
        this.orderNo=orderNo;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;


    }
    public orderbook(String designerName,String desigenrId
            ,Boolean isWorkComplete,String orderNo,String customerName,String orderDateString,Boolean isHandWork ,String item1,String item2,long itemCount)
    {
        this.itemCount=itemCount;
        this.item1=item1;
        this.item2=item2;
        this.designerName=designerName;
        this.desigenrId=desigenrId;
        this.isWorkComplete=isWorkComplete;
        this.orderNo=orderNo;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;


    }
    public orderbook(String designerName,String desigenrId,Boolean isWorkComplete,String orderNo,String customerName,String orderDateString,Boolean isHandWork ,String item1,String item2,String item3,long itemCount)
    {
        this.itemCount=itemCount;
        this.item1=item1;
        this.item2=item2;
        this.item3=item3;
        this.designerName=designerName;
        this.desigenrId=desigenrId;
        this.isWorkComplete=isWorkComplete;
        this.orderNo=orderNo;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;


    }



    public orderbook(String orderNo,String desigenrId,Boolean isWorkComplete,String customerName,String orderDateString,Boolean isHandWork,long itemCount,String item1,String item2 )
    {

        this.orderNo=orderNo;
        this.desigenrId=desigenrId;
        this.isWorkComplete=isWorkComplete;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;
        this.item1=item1;
        this.item2=item2;
        this.itemCount=itemCount;

    }
    public orderbook(String orderNo,String desigenrId,Boolean isWorkComplete,String customerName,String orderDateString,Boolean isHandWork,long itemCount,String item1,String item2 ,String item3)
    {

        this.orderNo=orderNo;
        this.desigenrId=desigenrId;
        this.isWorkComplete=isWorkComplete;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;
        this.item1=item1;
        this.item2=item2;
        this.item3=item3;
        this.itemCount=itemCount;

    }
    public orderbook(String orderNo,String desigenrId,Boolean isWorkComplete,String customerName,String orderDateString,Boolean isHandWork,long itemCount,String item1 )
    {

        this.orderNo=orderNo;
        this.desigenrId=desigenrId;
        this.isWorkComplete=isWorkComplete;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;
        this.item1=item1;
        this.itemCount=itemCount;

    }


    public long getItemCount() {
        return itemCount;
    }

    public String getItem1() {
        return item1;
    }

    public String getItem2() {
        return item2;
    }

    public String getItem3() {
        return item3;
    }

    public String getDesignerId() {

        return desigenrId;
    }

    public void setDesignerName(String designerName) {

        this.designerName=designerName;

    }

    public String getDesignerName() {


        return designerName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public Boolean getIsWorkComplete() {
        return isWorkComplete;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getItemName() {
        return itemName;
    }

    public Boolean getIsHandWork() {
        return isHandWork;
    }

    public String getOrderDateString() {
        return orderDateString;
    }

//    public void designerIdToName()
//
//    {
//
//        databaseUsers=database.getReference("users");
//        databaseUsers.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                setDesignerName((String) dataSnapshot.child("Pc11KFoS1JMqxRtD5OwFMmBaaFF2").child("name").getValue());
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


}
