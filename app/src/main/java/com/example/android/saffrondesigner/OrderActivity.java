package com.example.android.saffrondesigner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class OrderActivity extends AppCompatActivity {
    static TextView orderDateText;
    static TextView delDateText;


    static int orderDate;
    static int orderMonth;
    static int orderYear;
    static int isOrder;
    static int isDel;
    static int delDate;
    static int delMonth;
    static int delYear;
    int todayDate, todayMonth, todayYear;


    CardView orderDateCard, delDateCard, handWorkCard;
    LinearLayout handWorkLayout;
    ProgressBar progressBar;
    CalendarView pickOrderDate;

    TextInputEditText orderNo, customerName, itemName;
    RadioGroup isHandWork;

    DatabaseHelper myDb;
    FloatingActionButton cancel_fab;


    static FrameLayout fragmentContainer;

    //JAVA DATA TYPES


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Calendar calendar = Calendar.getInstance();

        myDb = new DatabaseHelper(this);

        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH) + 1;
        todayDate = calendar.get(Calendar.DATE);


        //Binding
        orderDateText = (TextView) findViewById(R.id.orderDateText);
//        dateForDelText=(TextView) findViewById(R.id.dateForDelText);
        delDateText = (TextView) findViewById(R.id.delDateText);

        orderDateCard = (CardView) findViewById(R.id.orderDateCard);
        delDateCard = (CardView) findViewById(R.id.delDateCard);
        handWorkCard = (CardView) findViewById(R.id.handWorkCard);


        pickOrderDate = (CalendarView) findViewById(R.id.pickOrderDate);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        orderNo = (TextInputEditText) findViewById(R.id.orderNo);


        customerName = (TextInputEditText) findViewById(R.id.customerName);

        itemName = (TextInputEditText) findViewById(R.id.itemName);

        isHandWork = (RadioGroup) findViewById(R.id.isHandWork);

        handWorkLayout = (LinearLayout) findViewById(R.id.handWorkLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        cancel_fab=(FloatingActionButton) findViewById(R.id.cancel_fab);

        //Setting for default is Today Date
        if (orderDate == 0 && orderMonth == 0 && orderYear == 0) {
            orderDateDefault();
            orderDateToString();

        }
        else if (orderDate >= 1 && orderMonth >= 1 && orderYear >= 1) {
            selectedOrderDate();
            orderDateToString();
        }

        //When user pick a date from calender_view_fragment

        //Checking delDate selected if not TEXT is PICK DELIVERY DATE
        if (delDate == 0 && delMonth == 0 && delYear == 0) {
            delDateDefault();
            delDateToString();
        }
        //if yes shows picked DELIVERY DATE
        else {
            delDateToString();
            delDateText.setText(delDate + "/" + delMonth + "/" + delYear);
        }


        orderDateCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                isOrder = 1; // setting orderCard is true
                isDel = 0;

                fragmentContainer.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                calender_view_fragment calender_view_fragment = new calender_view_fragment();
                fragmentTransaction.add(R.id.fragmentContainer, calender_view_fragment);
                fragmentTransaction.commit();


            }
        });
        delDateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDel = 1; //setting deliveryCard is true
                isOrder = 0;

                fragmentContainer.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                calender_view_fragment calender_view_fragment = new calender_view_fragment();
                fragmentTransaction.add(R.id.fragmentContainer, calender_view_fragment);
                fragmentTransaction.commit();
            }
        });
        if (delDate >= 1) //When deliveryDate is greater than 0 orderNo Text Keyboard shows
        {


            orderNo.requestFocus();

            keyBoardUp();

        }
orderNo.addTextChangedListener(new TextWatcher() {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



    }

    @Override
    public void afterTextChanged(Editable editable) {
        String orderno=orderNo.getText().toString().trim();
        if (myDb.rowIdExists(orderno))
        {
            orderNo.setError(orderno+" Already exits");

        }



    }
});

        customerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {



                getCustomerName();
            }
        });
            itemName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                }

                @Override
                public void afterTextChanged(Editable editable) {
                    getItemName();
                    if (validation()) {
                        handWorkCard.setVisibility(View.VISIBLE);
                    }

                }
            });
            isHandWork.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                int isHandWork;

                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                    switch (checkedId) {
                        case R.id.handWorkOn:
                            isHandWork = 1;

                            progressBarOn();
                            break;
                        case R.id.handWorkOff:
                            isHandWork = 0;
                            progressBarOn();
                            break;

                    }
                    if (isHandWork == 1 || isHandWork == 0) {

                        boolean isInserted = myDb.insertData(getOrderNo(), orderDateToString(), delDateToString(), getCustomerName(), getItemName(), isHandWork);


                        if (isInserted = true) {

                            dataReset();
                            progressBarOff();


                        } else

                            Toast.makeText(OrderActivity.this, "Something Went Wrong Try Again", Toast.LENGTH_SHORT).show();


                    }

                }
            });


        cancel_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMainActivity();
            }
        });


        }

        public String getCustomerName()
        {
          String getCustomerNam=customerName.getText().toString().toLowerCase().trim();
          return getCustomerNam;
        }
        public int getOrderNo()
        {
            String value = orderNo.getText().toString();
             int getOrderNo = Integer.parseInt(value);
            return getOrderNo;

        }
        public String getItemName()
        {
             String getItemName = itemName.getText().toString().toLowerCase().trim();
             return getItemName;
        }
        public String orderDateToString()
        {
            String orderDateString = Integer.toString(orderDate) + Integer.toString(orderMonth) + Integer.toString(orderYear);
            return orderDateString;
        }
    public String delDateToString()
    {
        String delDateString = Integer.toString(delDate) + Integer.toString(delMonth) + Integer.toString(delYear).trim();

        return delDateString;
    }


    public void progressBarOn () {
            handWorkLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        public void progressBarOff () {

            handWorkCard.setVisibility(View.GONE);
            handWorkLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        }
        public void keyBoardUp()
        {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

        }
        public void dataReset() {
            orderDate = 0;
            orderMonth = 0;
            orderYear = 0;
            delDate = 0;
            delMonth = 0;
            delYear = 0;
            orderNo.setText("");
            customerName.setText("");
            itemName.setText("");
            itemName.clearFocus();
            customerName.clearFocus();
            orderNo.clearFocus();
            orderDateDefault();
            delDateDefault();

        }
        public void orderDateDefault ()
        {


            orderDate = todayDate;
            orderMonth = todayMonth;
            orderYear = todayYear;
            orderDateText.setText(orderDate + "/" + orderMonth + "/" + orderYear);


        }
        public void selectedOrderDate ()
        {

            orderDateText.setText(orderDate + "/" + orderMonth + "/" + orderYear);

        }
        public void delDateDefault ()
        {
            delDateText.setText("PICK DELIVERY DATE");
        }
        public boolean validation () {
        String customerName=getCustomerName();

            boolean validation=true;
            if ( !delDateToString().equals("000")&&!customerName.isEmpty()) {
                validation = true; //validation OK

            } else
                validation = false;



            return validation;

        }
        public void toMainActivity()
        {
            Intent MainActivity=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(MainActivity);
        }


    }


