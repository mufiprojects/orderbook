package com.example.android.saffrondesigner;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class orderListAdapter extends RecyclerView.Adapter<orderListAdapter.orderListHolder> {
    Context context;
    List<orderbook> orderbookList = new ArrayList<>();
    LayoutInflater inflater;
    DatabaseHelper myDb;



    private onItemClickListener onItemClickListener;
    public interface onItemClickListener
    {
        void onItemClick(int position);
    }
    public  void setOnItemClickListener(onItemClickListener listener)
    {
        onItemClickListener=listener;
    }



    public orderListAdapter(Context context, List<orderbook> orderbookList1) {
        this.context = context;
        this.orderbookList = orderbookList1;
        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public orderListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist, parent, false);
        return new orderListHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final orderListHolder holder, final int position) {
        myDb=new DatabaseHelper(context);
        final orderbook orderbook = orderbookList.get(position);
        int  orderNo=orderbook.orderNo;
        holder.orderno.setText(orderbook.orderNo + " ");
        holder.customerName.setText(orderbook.customerName);
        holder.itemName.setText(orderbook.itemName);
        if (orderbook.isWorkComplete==1)
        {
            showCompleted(holder);
        }
        else
     holder.workCompleteSwicth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
             if (holder.workCompleteSwicth.isChecked())
             {
                 myDb.InsertWorkComplete(1,orderbook.orderNo);
                 showCompleted(holder);
             }
         }
     });


        char[] orderDateCharArray = orderbook.orderDateString.trim().toCharArray();
        char[] convertedArray = new char[orderDateCharArray.length + 2];
        int PositionSpaceBetweenMonth = 0;
        int PositionSpaceBetweenYear = 0;
        if (orderDateCharArray.length == 6) {
            PositionSpaceBetweenMonth = 1;
            PositionSpaceBetweenYear = 3;
        } else if (orderDateCharArray.length == 7) {
            PositionSpaceBetweenMonth = 1;
            PositionSpaceBetweenYear = 4;
        } else if (orderDateCharArray.length == 8) {

            PositionSpaceBetweenMonth = 2;
            PositionSpaceBetweenYear = 5;
        }
        if (orderDateCharArray.length == 8 || orderDateCharArray.length == 7 || orderDateCharArray.length == 6) {
            int orderDateCharArrayLength = orderDateCharArray.length + 2;
            Boolean isMonthFinished=false;
            for (int i = 0; i < orderDateCharArrayLength; i++) {


                if (i == PositionSpaceBetweenMonth || i == PositionSpaceBetweenYear) {
                    convertedArray[i + 1] = orderDateCharArray[i];
                    convertedArray[i] = '/';



                }

                else if (isMonthFinished)
                    {
                        convertedArray[i]=orderDateCharArray[i-2];
                    }
                else if (orderDateCharArray.length > i ) {

                    if (i != PositionSpaceBetweenMonth+1 ) { //=3
                        if(i==PositionSpaceBetweenMonth+2 )
                        {
                            convertedArray[i]=orderDateCharArray[i-1];
                            isMonthFinished=true;

                        }
                        else
                        convertedArray[i] = orderDateCharArray[i];
                    }




                }

            }

        }

        holder.orderDateString.setText(String.valueOf(convertedArray));
        if (orderbook.isHandWork==1){
            holder.handWorkOn.setVisibility(View.VISIBLE);
            holder.handWorkOff.setVisibility(View.GONE);
        }
        else if (orderbook.isHandWork==0){
            holder.handWorkOff.setVisibility(View.VISIBLE);
            holder.handWorkOn.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return orderbookList.size();
    }
    public class orderListHolder extends RecyclerView.ViewHolder{
        TextView orderno;
        TextView customerName;
        TextView itemName;
        TextView orderDateString;
        ImageView handWorkOn;
        ImageView handWorkOff;
        SwitchCompat workCompleteSwicth;
        TextView workCompleteSwitchTab;

        public orderListHolder(@NonNull final View itemView, final onItemClickListener listener) {
            super(itemView);
            orderno=itemView.findViewById(R.id.orderNoOnCard);
            customerName=itemView.findViewById(R.id.customerNameOnCard);
            itemName=itemView.findViewById(R.id.itemNameOnCard);
            handWorkOn=itemView.findViewById(R.id.handWorkOnImageView);
            handWorkOff=itemView.findViewById(R.id.handWorkOffImageView);
            orderDateString=itemView.findViewById(R.id.orderDateOnCard);
            workCompleteSwicth=itemView.findViewById(R.id.workCompleteSwitch);
            workCompleteSwitchTab=itemView.findViewById(R.id.workCompleteSwitchTab);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {

                    int position=getAdapterPosition();
                    final orderbook orderbook = orderbookList.get(position);
                    openBottomSheet(view,orderbook.orderNo,position);
                    return false;
                }
            });

        }
    }
    public void showCompleted(orderListHolder holder)
    {
        holder.workCompleteSwicth.setVisibility(View.GONE);
        holder.workCompleteSwitchTab.setVisibility(View.VISIBLE);
    }
    private void openBottomSheet(View v, final int orderNo, final int position){
        final Context context=v.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.postpond_bottom_sheet,null);
        final DatePickerTimeline datePickerTimeline=(DatePickerTimeline)view.findViewById(R.id.postpondDatePicker);

        final Dialog postpondPicker=new Dialog(context,R.style.Theme_MaterialComponents_BottomSheetDialog);
        postpondPicker.setContentView(view);
        postpondPicker.setCancelable(true);
        postpondPicker.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
               LinearLayout.LayoutParams.WRAP_CONTENT );
        postpondPicker.getWindow().setGravity(Gravity.BOTTOM);
        postpondPicker.show();
        Calendar calendar = Calendar.getInstance();

        final int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        datePickerTimeline.setInitialDate(year,month,date);

        datePickerTimeline.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {

                myDb=new DatabaseHelper(context);
                String selectedDeliveryDate=Integer.toString(day)+Integer.toString(month+1)+Integer.toString(year);

                myDb.postPondDelDate(orderNo,selectedDeliveryDate);
                postpondPicker.dismiss();
                orderListAdapter.this.notifyItemRemoved(position);
//                Toast.makeText(context, orderNo+"Selected", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {

            }
        });
    }
}

