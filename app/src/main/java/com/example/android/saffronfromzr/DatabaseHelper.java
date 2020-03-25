package com.example.android.saffronfromzr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "saffron.db";
    public static final int DATABASE_VERSION=2;
    public static final String TABLE_NAME = "SAFFRON";
    public static final String COLUMN_ORDERNO = "ORDER_NO";
    public static final String COLUMN_ORDERDATE = "ORDER_DATE";
    public static final String COLUMN_DELIVERYDATE = "DELIVERY_DATE";
    public static final String COLUMN_CUSTOMERNAME = "CUSTOMER_NAME";
    public static final String COLUMN_ITEMNAME = "ITEM_NAME";
    public static final String COLUMN_ISHANDWORK  = "IS_HANDWORK";
    public static final String COLUMN_ISWORKCOMPLETE="IS_WORK_COMPLETE";

    private static final  String DATABASE_ALTER_ISCOMPLETE="ALTER TABLE "+
            TABLE_NAME +" ADD COLUMN " + COLUMN_ISWORKCOMPLETE +" INTEGER";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db=this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME +
                 " ("+ COLUMN_ORDERNO  +" INTEGER PRIMARY KEY,"
                + COLUMN_ORDERDATE +"   TEXT NOT NULL, "
                + COLUMN_DELIVERYDATE +"  TEXT NOT NULL, "
                + COLUMN_CUSTOMERNAME +"  TEXT NOT NULL, "
                + COLUMN_ITEMNAME +"  TEXT NOT NULL, "
                + COLUMN_ISHANDWORK +"  INTEGER NOT NULL, "
                + COLUMN_ISWORKCOMPLETE +" INTEGER "
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion<3) {
        db.execSQL(DATABASE_ALTER_ISCOMPLETE);
        }
    }
    public boolean insertData(int orderNo,String orderDate,String delDate,String customerName, String itemName,int isHandWork){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_ORDERNO,orderNo);
        contentValues.put(COLUMN_ORDERDATE,orderDate);
        contentValues.put(COLUMN_DELIVERYDATE,delDate);
        contentValues.put(COLUMN_CUSTOMERNAME,customerName);
        contentValues.put(COLUMN_ITEMNAME,itemName);
        contentValues.put(COLUMN_ISHANDWORK,isHandWork);
       long isInsert= db.insert(TABLE_NAME,null,contentValues);
       if (isInsert==-1)
           return false;
       else
           return true;

    }
    public List<orderbook> getData(String deliveryDate )
    {
        List<orderbook> orderbookArrayList=new ArrayList<>();
        String ORDER_DATA_GET_QUERY="SELECT * FROM "+ TABLE_NAME +" WHERE "+ COLUMN_DELIVERYDATE+"="+deliveryDate;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(ORDER_DATA_GET_QUERY,null);
        try{
            if (cursor.moveToFirst()){
                do {
                    orderbook orderbook=new orderbook();

                    orderbook.orderDateString=cursor.getString(cursor.getColumnIndex(COLUMN_ORDERDATE));
                    orderbook.customerName=cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMERNAME));
                    orderbook.itemName=cursor.getString(cursor.getColumnIndex(COLUMN_ITEMNAME));

                    orderbook.isWorkComplete=cursor.getInt(cursor.getColumnIndex(COLUMN_ISWORKCOMPLETE));

                    orderbookArrayList.add(orderbook);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(null,"Error while to get data from database");
        }finally {
            if (cursor!=null&&cursor.isClosed()){
                cursor.close();
            }
        }
        return orderbookArrayList;


    }
    public void InsertWorkComplete( int isWorkComplete,int orderNo)
    {
        SQLiteDatabase db=this.getWritableDatabase();
       ContentValues contentValues=new ContentValues();
       contentValues.put(COLUMN_ISWORKCOMPLETE,isWorkComplete);
    db.update(TABLE_NAME, contentValues, COLUMN_ORDERNO +" = " + Integer.toString(orderNo),null);


    }
    public void postPondDelDate(int orderNo,String newDelDate)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_DELIVERYDATE,newDelDate);
        db.update(TABLE_NAME,contentValues,COLUMN_ORDERNO +" = "+ Integer.toString(orderNo),null);
    }

    public  long countRows(String delDate)
    {
        SQLiteDatabase db=getReadableDatabase();
         long  countRowsNo=0;


                 countRowsNo =DatabaseUtils.queryNumEntries(db,TABLE_NAME, COLUMN_DELIVERYDATE +" = "+delDate);

        return countRowsNo;

    }
    public boolean rowIdExists(String orderNo)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select ORDER_NO from " + TABLE_NAME + " where ORDER_NO=?", new String[]{orderNo});
        boolean exists=(cursor.getCount()>0);
        cursor.close();
        return exists;
    }

}
