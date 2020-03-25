package com.example.android.saffronfromzr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.android.saffronfromzr.OrderActivity.delDate;
import static com.example.android.saffronfromzr.OrderActivity.delMonth;
import static com.example.android.saffronfromzr.OrderActivity.delYear;

import static com.example.android.saffronfromzr.OrderActivity.isDel;
import static com.example.android.saffronfromzr.OrderActivity.isOrder;
import static com.example.android.saffronfromzr.OrderActivity.orderDate;
import static com.example.android.saffronfromzr.OrderActivity.orderMonth;
import static com.example.android.saffronfromzr.OrderActivity.orderYear;

public class calender_view_fragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.calender_view_fragment,container,false);
        if (delDate>=1) {
            keyboardHide(view);
        }
     CalendarView  calendarView=(CalendarView) view.findViewById(R.id.pickOrderDate);



        final TextView calenderViewText=(TextView) view.findViewById(R.id.calenderViewText);
      if (isOrder==1) {
    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int selectedYear, int selectedMonth, int selectedDate) {
            orderYear = selectedYear;
            orderMonth = selectedMonth + 1;
            orderDate = selectedDate;


            calenderViewText.setText("PICK DELIVERY DATE");
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int selectedYear, int selectedMonth, int selectedDate) {
                    delDate = selectedDate;
                    delMonth = selectedMonth + 1;
                    delYear = selectedYear;
                    stopCurrentFragment();
                  startOrderActivity();



                }
            });


        }
    });
}
else if (isDel==1) // if delivery date is  picked
{
    calenderViewText.setText("PICK DELIVERY DATE");
    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int selectedYear, int selectedMonth, int selectedDate) {
            delDate = selectedDate;
            delMonth = selectedMonth + 1;
            delYear = selectedYear;
            stopCurrentFragment();
            startOrderActivity();
        }
    });

}

        return view;

    }
    public void startOrderActivity(){
        startActivity(new Intent(getActivity(), OrderActivity.class));
    }
    public void getCurrentFragment(){
        getActivity();

    }
    public void stopCurrentFragment(){




        getFragmentManager().popBackStack();


    }
    public   void  keyboardHide(View view)
    {
        InputMethodManager inputMethodManager=(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
    }


}
