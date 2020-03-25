package com.example.android.saffronfromzr;

public class orderbook  {
   int isWorkComplete;
     String customerName,itemName,orderDateString,orderNo;
     Boolean isHandWork;

    public  orderbook(){


    }
    public orderbook(String orderNo,String customerName,String orderDateString,Boolean isHandWork )
    {
        this.orderNo=orderNo;
        this.customerName=customerName;
        this.isHandWork=isHandWork;
        this.orderDateString=orderDateString;

    }
    public String getOrderNo() {
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

    public Boolean getIsHandWork() {
        return isHandWork;
    }

    public String getOrderDateString() {
        return orderDateString;
    }


}
