package com.example.android.saffronfromzr;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties

public class order {
    private String orderDate;
    private String deliveryDate;
    private String customerName;
    private Boolean isHandWork;
    HashMap<String,Boolean> items=new HashMap<>();
    public order()
    {

    }


    public order(String orderDate, String deliveryDate, String customerName,Boolean isHandWork,HashMap<String, Boolean> items)
    {

        this.orderDate=orderDate;
        this.deliveryDate=deliveryDate;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.items=items;

    }


    public String getOrderDate() {
        return orderDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Boolean getHandWork() {
        return isHandWork;
    }

    public HashMap<String, Boolean> getItems() {
        return items;
    }
}
