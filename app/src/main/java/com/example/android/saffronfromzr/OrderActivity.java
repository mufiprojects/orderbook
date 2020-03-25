package com.example.android.saffronfromzr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Visibility;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hootsuite.nachos.ChipConfiguration;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.ChipCreator;
import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.tokenizer.ChipTokenizer;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class OrderActivity extends AppCompatActivity {
     TextView orderDateText;
     TextView delDateText;
     TextView pickDelDateText;
     TextView addOrderTextView;

    static int orderDate;
    static int orderMonth;
    static int orderYear;
    static int isOrder;
    static int isDel;
    static int delDate;
    static int delMonth;
    static int delYear;
    public static final String DATE_FORMAT="dd/MM/yyyy";

    int todayDate, todayMonth, todayYear;




    CardView orderDateCard, delDateCard, handWorkCard;
    LinearLayout handWorkLayout,itemNotFoundLayout;
    ProgressBar progressBar;
    CalendarView pickOrderDate;


    TextInputEditText orderNoEditText, customerNameEditText;
    NachoTextView itemNameEditText;
    RadioGroup isHandWork;
    RadioButton handWorkOn,handWorkOff;

    DatabaseHelper myDb;
    FloatingActionButton cancel_fab;

    FirebaseAuth mAuth;

     FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseOrders;
    DatabaseReference databaseItems;
    DatabaseReference databaseDeliveryDates;

     FrameLayout fragmentContainer;

     MaterialButton backButton;
     MaterialButton addItemButton;

     ChipGroup itemSuggestionChipGroup;

     Toolbar toolbar;
    private String deliveryDateString="1";
    private String orderDateString;
    private String customerName="";
    private int orderno=0;

     Boolean alreadyAdded =false;
     Boolean isHandWorkChecked=false;

    List<order> order;
    List<String> stringChipList;
    List<com.hootsuite.nachos.chip.Chip> chipList;
    public int chipListLength;
    HashMap<String,String> items=new HashMap<>();
    HashMap<String, Boolean> itemsToOrder = new HashMap<>();
    HashMap<String,String> itemsSuggestionList=new HashMap<>();
    final  int VISIBILE=0;
    final int INVISBILE=4;
    final int GONE=8;
    //JAVA DATA TYPES


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        myDb = new DatabaseHelper(this);
        mAuth=FirebaseAuth.getInstance();
        databaseOrders=database.getReference("orders");
        databaseItems=database.getReference("items");
        databaseDeliveryDates=database.getReference("deliveryDates");

//        databaseOrders= FirebaseDatabase.getInstance().getReference("orders");
//       databaseItems=FirebaseDatabase.getInstance().getReference("items").push();

        order=new ArrayList<>();
        stringChipList =new ArrayList<>();
        chipList=new ArrayList<>();
//        todayYear = calendar.get(Calendar.YEAR);
//        todayMonth = calendar.get(Calendar.MONTH) + 1;
//        todayDate = calendar.get(Calendar.DATE);


        //Binding
        orderDateText = (TextView) findViewById(R.id.orderDateText);
//        dateForDelText=(TextView) findViewById(R.id.dateForDelText);
        delDateText = (TextView) findViewById(R.id.delDateText);
        pickDelDateText=findViewById(R.id.pickDelDateText);

        addOrderTextView=(TextView)findViewById(R.id.addOrderTextView);
        orderDateCard = (CardView) findViewById(R.id.orderDateCard);
        delDateCard = (CardView) findViewById(R.id.delDateCard);
//        handWorkCard = (CardView) findViewById(R.id.handWorkCard);


        pickOrderDate = (CalendarView) findViewById(R.id.pickOrderDate);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        orderNoEditText = (TextInputEditText) findViewById(R.id.orderNo);


        customerNameEditText = (TextInputEditText) findViewById(R.id.customerName);

        itemNameEditText = (NachoTextView) findViewById(R.id.itemNameEditText);

        isHandWork = (RadioGroup) findViewById(R.id.isHandWork);
        handWorkOn=(RadioButton)findViewById(R.id.handWorkOn);
        handWorkOff=(RadioButton)findViewById(R.id.handWorkOff);

        handWorkLayout = (LinearLayout) findViewById(R.id.handWorkLayout);
        itemNotFoundLayout=(LinearLayout) findViewById(R.id.itemNotFoundLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        cancel_fab=(FloatingActionButton) findViewById(R.id.cancel_fab);

        backButton=(MaterialButton)findViewById(R.id.backButton);
        addItemButton=(MaterialButton)findViewById(R.id.addItemButton);

        itemSuggestionChipGroup=(ChipGroup) findViewById(R.id.itemSuggestionChipGroup);
            setTodayDate();
//            orderDateToString();

//        //Setting for default is Today Date
////        if (orderDate == 0 && orderMonth == 0 && orderYear == 0) {

//
////        }
//        else if (orderDate >= 1 && orderMonth >= 1 && orderYear >= 1) {
//            selectedOrderDate();
//            orderDateToString();
//        }

        //When user pick a date from calender_view_fragment

        //Checking delDate selected if not TEXT is PICK DELIVERY DATE
//        if (delDate == 0 && delMonth == 0 && delYear == 0) {
//            delDateToString();
//        }
//        //if yes shows picked DELIVERY DATE
//        else {
//            picDelDateText.setVisibility(View.GONE);
//            delDateToString();
//            delDateText.setText(delDate + "/" + delMonth + "/" + delYear);
//        }


        orderDateCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                isOrder = 1; // setting orderCard is true
//                isDel = 0;
//
//                fragmentContainer.setVisibility(View.VISIBLE);
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                calender_view_fragment calender_view_fragment = new calender_view_fragment();
//                fragmentTransaction.add(R.id.fragmentContainer, calender_view_fragment);
//                fragmentTransaction.commit();
                Boolean isOrderDateCardSelected=true;
                openBottonCalender(isOrderDateCardSelected);


            }
        });
        delDateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                isDel = 1; //setting deliveryCard is true
//                isOrder = 0;
//
//                fragmentContainer.setVisibility(View.VISIBLE);
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                calender_view_fragment calender_view_fragment = new calender_view_fragment();
//                fragmentTransaction.add(R.id.fragmentContainer, calender_view_fragment);
//                fragmentTransaction.commit();

                openBottonCalender(false);
            }
        });
        if (delDate >= 1) //When deliveryDate is greater than 0 orderNo Text Keyboard shows
        {


            orderNoEditText.requestFocus();

            keyBoardUp();

        }
orderNoEditText.addTextChangedListener(new TextWatcher() {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



    }

    @Override
    public void afterTextChanged(Editable editable) {
        getOrderNo();
        if (isHandWorkChecked) {
            if (checkRadioGroupActive(isHandWork)) {
                setRadioGroupUncheck(isHandWork);
            }
        }
        if (validation())
        {
            setHandWorkLayoutVisibility(VISIBILE);
            setAddOrderTextViewVisibility(INVISBILE);
            backButtonVisibility(GONE);
        }
        else
        {
            setHandWorkLayoutVisibility(GONE);
            setAddOrderTextViewVisibility(VISIBILE);
            backButtonVisibility(VISIBILE);
        }
//       orderno =getOrderNo();
//        if (myDb.rowIdExists(orderno))
//        {
//            orderNo.setError(orderno+" Already exits");
//
//        }



    }
});

        customerNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                resetItemSuggestion();

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                resetItemSuggestion();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getCustomerName();
                if (validation())
                {
                    setHandWorkLayoutVisibility(VISIBILE);
                    setAddOrderTextViewVisibility(INVISBILE);
                    backButtonVisibility(GONE);
                }
                else
                {
                    setHandWorkLayoutVisibility(GONE);
                    setAddOrderTextViewVisibility(VISIBILE);
                    backButtonVisibility(VISIBILE);
                }

            }
        });
            itemNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    orderDateToString();
                    delDateToString();
                    getCustomerName();
                    if (getItemName().isEmpty())
                    {
                        resetItemSuggestion();

                    }

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    itemNameEditText.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

                    if (getItemName().isEmpty())
                    {
                       resetItemSuggestion();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(isHandWorkChecked){
                    resetRadioButton();
                   }

                    getItemName();
                    setItemsSuggestionList(getItemName());

                    if (validation()) {
                        setHandWorkLayoutVisibility(VISIBILE);
                        setAddOrderTextViewVisibility(INVISBILE);
                        backButtonVisibility(GONE);
                    }
                    else
                    {
                        setHandWorkLayoutVisibility(GONE);
                        setAddOrderTextViewVisibility(VISIBILE);
                        backButtonVisibility(VISIBILE);
                    }

                }
            });


            isHandWork.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                Boolean isHandWork;

                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                    switch (checkedId) {
                        case R.id.handWorkOn:
                            isHandWork = true;
                            progressBarOn();
                            break;
                        case R.id.handWorkOff:
                            isHandWork = false;
                            progressBarOn();
                            break;

                    }
                    itemNameEditTextToHashMap();

                    addOrder(orderDateString,deliveryDateString,customerName,isHandWork,itemsToOrder);

                        dataReset();

                        progressBarOff();

//                        boolean isInserted = myDb.insertData(getOrderNo(), orderDateToString(), delDateToString(), getCustomerName(), getItemName(), isHandWork);


//                        if (isInserted = true) {
//
//                            dataReset();
//                            progressBarOff();
//
//
//                        } else
//
//                            Toast.makeText(OrderActivity.this, "Something Went Wrong Try Again", Toast.LENGTH_SHORT).show();




                }

            });




        cancel_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        }

    private void resetRadioButton() {
        if(handWorkOn.isChecked())
        {
            handWorkOn.setChecked(false);
            isHandWorkChecked=false;
        }
        else
            handWorkOff.setChecked(false);
        isHandWorkChecked=false;
    }


    private void addChips(String itemName) {

        final Chip chip = new Chip(this);
        chip.setText(itemName + '\n');
        chip.setCheckable(false);
        chip.setClickable(true);
        itemSuggestionChipGroup.addView(chip);
        itemSuggestionChipGroup.setVisibility(View.VISIBLE);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringChipList = itemNameEditText.getChipValues();
                chipList=itemNameEditText.getAllChips();
                chipListLength=stringChipList.size();
                if (chipListLength==0) {

                    itemNameEditText.setText(chip.getText());
                    itemNameEditText.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

                }
                else {
//                    Toast.makeText(OrderActivity.this, "else runned", Toast.LENGTH_SHORT).show();
                    stringChipList.add(chipListLength, chip.getText().toString());
                    itemNameEditText.setText(stringChipList);
                    itemNameEditText.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
                }

            }

        });
    }

    private void addItemNotInSuggestion(String itemName)
    {
        addItemButton.setText("Add " +itemName);
        itemNotFoundLayout.setVisibility(View.VISIBLE);
    }
    private void addOrder(String orderDateString, String deliveryDateString, String customerName,Boolean isHandWork,HashMap<String, Boolean> items) {
        order order=new order(orderDateString,deliveryDateString,customerName,isHandWork,items);
        String orderNoString=Integer.toString(getOrderNo());
        databaseOrders.child(getCurrentUserToString()).child(orderNoString).setValue(order);
        databaseDeliveryDates.child(orderDateString.replace("/","")).child(orderNoString).setValue(true);
        addItemsToDatabase();


    }
    private void addItemsToDatabase(){

        databaseItems.setValue(items);

    }


    public String getCustomerName()
        {
           customerName=customerNameEditText.getText().toString().toLowerCase().trim();
          return customerName;
        }
        public int getOrderNo()
        {
            String value = orderNoEditText.getText().toString();
            if (value.isEmpty())
            {
                return orderno=0;
            }
            else {
                orderno = Integer.parseInt(value);

                return orderno;
            }

        }
        public String getItemName(){
            String ItemName;
            int ItemNameLength=itemNameEditText.getTokenValues().toString().length();
            ItemName= itemNameEditText.getTokenValues().toString().substring(1,ItemNameLength-1).toLowerCase().trim();
            return ItemName;}
//

//             else
//             {
//                 itemNameEditText.setText("");
//                 ItemName= itemNameEditText.getText().toString().toLowerCase().trim();
//                 return ItemName;
//             }
             //1.



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


            handWorkLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        }
        public void keyBoardUp()
        {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

        }
        public void dataReset() {
//            orderDate = 0;
//            orderMonth = 0;
//            orderYear = 0;
//            delDate = 0;
//            delMonth = 0;
//            delYear = 0;
            setTodayDate();
            delDateText.setText("");

            orderNoEditText.clearFocus();
            orderNoEditText.setText("");

            customerNameEditText.clearFocus();
            customerNameEditText.setText("");

            itemNameEditText.clearFocus();
            itemNameEditText.setText("");
                resetItemSuggestion();
                items.clear();

                stringChipList.clear();


            setPickDelDateTextViewVisibility(VISIBILE);
            isHandWorkChecked=true;
            setHandWorkLayoutVisibility(GONE);
            setAddOrderTextViewVisibility(VISIBILE);
            backButtonVisibility(VISIBILE);
            Toast.makeText(this, "Data Resetted", Toast.LENGTH_SHORT).show();


        }

        public void setTodayDate ()
        {
//            orderDate = todayDate;
//            orderMonth = todayMonth;
//            orderYear = todayYear;
            Calendar calendar = Calendar.getInstance();
            orderDateString= dateToString(calendar.getTime());
            orderDateText.setText(orderDateString);
        }
        public void selectedOrderDate ()
        {

            orderDateText.setText(orderDate + "/" + orderMonth + "/" + orderYear);

        }
        public void delDateDefault ()
        {
            delDateText.setText(R.string.pick);
        }
        public boolean validation() {
        return !orderDateString.isEmpty() && !deliveryDateString.equals("1") && !customerName.isEmpty() && orderno>0 &&!itemNameEditText.getAllChips().isEmpty();

            //1.orderdate must be contain
            //2.delivery date must be contain and must be future date.
            //3.orderno must be number and no in database - primary key
            //4.cutsomer name must be conatin
            //5.atleast one item
            //6.if all true visibileHandWorkLayout()


        }
        public void toMainActivity()
        {
            Intent MainActivity=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(MainActivity);
        }
        public void setHandWorkLayoutVisibility(int visibility)
        {
            handWorkLayout.setVisibility(visibility);

        }
       public void setAddOrderTextViewVisibility(int visibility)
        {
            addOrderTextView.setVisibility(visibility);
        }

        public void backButtonVisibility(int Visibility)
        {
           backButton.setVisibility(Visibility);
        }


        public void openBottonCalender(Boolean isOrderDateCardSelected) {
            if (isOrderDateCardSelected) {
                new DoubleDateAndTimePickerDialog.Builder(this)

                        .title("SELECT DATES")
                        .tab0Text("ORDER DATE")
                        .tab1Text("DELIVERY DATE")
                        .setTab0DisplayMinutes(false)
                        .setTab0DisplayHours(false)
                        .setTab0DisplayDays(true)
                        .setTab1DisplayMinutes(false)
                        .setTab1DisplayHours(false)
                        .setTab1DisplayDays(true)
                        .mainColor(getResources().getColor(R.color.white))
                        .titleTextColor(getResources().getColor(R.color.white))
                        .bottomSheet()
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {
                                orderDateString=dateToString(dates.get(0));
                                orderDateText.setText(orderDateString);
                                deliveryDateString=dateToString(dates.get(1));
                                setPickDelDateTextViewVisibility(GONE);
                                delDateText.setText(deliveryDateString);
                                if (validation()) {
                                    setHandWorkLayoutVisibility(VISIBILE);
                                    setAddOrderTextViewVisibility(INVISBILE);
                                    backButtonVisibility(GONE);
                                }
                                else
                                {
                                    setHandWorkLayoutVisibility(GONE);
                                    setAddOrderTextViewVisibility(VISIBILE);
                                    backButtonVisibility(VISIBILE);
                                }


                            }
                        }).display();
                keyBoardUp();

            }
            else
            {
                new SingleDateAndTimePickerDialog.Builder(this)

                        .title("PICK DELIVERY DATE")
                        .displayDays(true)
                        .displayMonth(true)
                        .displayYears(true)
                        .displayHours(false)
                        .displayMinutes(false)
                        .displayAmPm(false)
                        .mustBeOnFuture()
                        .mainColor(getResources().getColor(R.color.primaryColor))
                        .titleTextColor(getResources().getColor(R.color.primaryColor))
                        .bottomSheet()
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                deliveryDateString=dateToString(date);
                                setPickDelDateTextViewVisibility(GONE);
                                delDateText.setText(deliveryDateString);
                                Toast.makeText(OrderActivity.this, deliveryDateString, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .display();
                keyBoardUp();
                if (validation()) {
                    setHandWorkLayoutVisibility(VISIBILE);
                    setAddOrderTextViewVisibility(INVISBILE);
                    backButtonVisibility(GONE);
                }
                else
                {
                    setHandWorkLayoutVisibility(GONE);
                    setAddOrderTextViewVisibility(VISIBILE);
                    backButtonVisibility(VISIBILE);
                }
            }
        }
        public String dateToString(Date selectedDate)
        {

            SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateToString=dateFormat.format(selectedDate);
            Toast.makeText(this, dateToString, Toast.LENGTH_SHORT).show();
            return dateToString;
        }
        public void setPickDelDateTextViewVisibility(int Visibility)
        {
            pickDelDateText.setVisibility(Visibility);
        }

        public void itemNameEditTextToHashMap()
        {
            for (com.hootsuite.nachos.chip.Chip chip:itemNameEditText.getAllChips())
            {
                CharSequence text=chip.getText();
                items.put(text.toString().replaceAll("\\s","").toLowerCase(),text.toString());
                itemsToOrder.put(text.toString().replaceAll("\\s","").toLowerCase(),true);

            }
        }
        public void setItemsSuggestionList(String itemName)

        {
            Query queryRef=databaseItems.startAt(itemName).endAt(itemName+"\uf8ff");
            databaseItems.startAt(itemName).endAt(itemName+"\uf8ff").limitToFirst(3).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds: dataSnapshot.getChildren())
                    {

                        try {
                            String itemKey=ds.getKey();
                            String itemName = ds.getValue().toString();

                            if (!itemsSuggestionList.containsKey(itemKey)) {
                                addChips(itemName);
                            }
                            itemsSuggestionList.put(itemKey,itemName);

                        }
                        catch (NullPointerException e)
                        {
                            Toast.makeText(OrderActivity.this, "Suggestions blocked ", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public boolean isItemNameEditTextIsFocused()
        {

             return itemNameEditText.isFocused();
        }
       public void clearItemSuggestionList()
        {
            itemsSuggestionList.clear();
        }
        public void clearItemSuggestionChipGroup()
        {
            itemSuggestionChipGroup.removeAllViews();
        }
         public void resetItemSuggestion()
         {
             clearItemSuggestionList();
             clearItemSuggestionChipGroup();

         }
         public boolean checkRadioGroupActive(RadioGroup radioGroup)
         {
             int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
             return checkedRadioButtonId == -1;
         }
         public void setRadioGroupUncheck(RadioGroup radioGroup)
         {
             radioGroup.clearCheck();
         }
         public  String getCurrentUserToString()
         {
           return   mAuth.getCurrentUser().getUid();
         }

    public void setItemSuggestionChipGroupVisibility(int Visibility) {
        itemSuggestionChipGroup.setVisibility(Visibility);
    }




    }





