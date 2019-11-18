package com.example.android.saffrondesigner;

import java.io.Serializable;

public class orderbook  {
   int orderNo,isHandWork,isWorkComplete;
     String customerName,itemName,orderDateString;

    public  orderbook(){


    }

    public int getOrderNo() {
        return orderNo;
    }

    public int getIsWorkComplete() {
        return isWorkComplete;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getItemName() {
        return itemName;
    }

    public int getIsHandWork() {
        return isHandWork;
    }

    public String getOrderDateString() {
        return orderDateString;
    }
}
