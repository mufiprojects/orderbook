package com.example.android.saffronfromzr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static com.example.android.saffronfromzr.common.currentUser;

public class OrderActivity extends AppCompatActivity {

     TextView orderDateText;
     TextView delDateText;
     TextView pickDelDateText;
     TextView addOrderTextView;
     TextInputEditText  customerNameEditText;
     NachoTextView itemNameEditText;

     CardView orderDateCard, delDateCard;
     LinearLayout handWorkLayout;

     ProgressBar progressBar;
     RadioGroup isHandWork;
     RadioButton handWorkOn,handWorkOff;
     MaterialButton backButton;
     MaterialButton addItemButton;
     ChipGroup itemSuggestionChipGroup;




     //FIREBASE DATABASE
     FirebaseDatabase database=FirebaseDatabase.getInstance();
     DatabaseReference databaseOrders;
     DatabaseReference databaseItems;
     DatabaseReference databaseDeliveryDates;
     DatabaseReference noOfOrdersComplete;



    private String deliveryDateString="1";
    private String orderDateString;
    private String customerName="";
    private int orderno;
    String orderNo;

     Boolean alreadyAdded =false;
     Boolean isHandWorkChecked=false;

    List<order> order;
    List<String> stringChipList;
    List<com.hootsuite.nachos.chip.Chip> chipList;
    public int chipListLength;
//    HashMap<String,String> items=new HashMap<>();

    Map<String, String> items=new HashMap<>();;
    HashMap<String, String> itemsToOrder = new HashMap<>();
    HashMap<String,String> itemsSuggestionList=new HashMap<>();

    final  int VISIBILE=0;
    final int INVISBILE=4;
    final int GONE=8;

    //VARIABLES
    Boolean iSFetchAllItems=true;
    //FINAL VARIABLES
    public static final String DATE_FORMAT="dd/MM/yyyy";

    //OBJECTS
    common common=new common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);



        databaseOrders=database.getReference("orders");
        databaseItems=database.getReference("items");
        databaseDeliveryDates=database.getReference("deliveryDates");
        noOfOrdersComplete = database.getReference("noOfOrdersComplete");
        fetchAllItems();
        common.fetchNoOfTotalOrders();
        common.fetchNoOfUserTotalOrders();

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









        customerNameEditText = (TextInputEditText) findViewById(R.id.customerName);

        itemNameEditText = (NachoTextView) findViewById(R.id.itemNameEditText);

        isHandWork = (RadioGroup) findViewById(R.id.isHandWork);
        handWorkOn=(RadioButton)findViewById(R.id.handWorkOn);
        handWorkOff=(RadioButton)findViewById(R.id.handWorkOff);

        handWorkLayout = (LinearLayout) findViewById(R.id.handWorkLayout);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        backButton=(MaterialButton)findViewById(R.id.backButton);
        addItemButton=(MaterialButton)findViewById(R.id.addItemButton);

        itemSuggestionChipGroup=(ChipGroup) findViewById(R.id.itemSuggestionChipGroup);
        getOrderNo();
            setTodayDate();
            addOrderTextView.setText(getString(R.string.addorder,getOrderNo()));

            orderDateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBottonCalender(true);
                }
            });
            delDateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBottonCalender(false);
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






        }

    private String getOrderNo() {
        Intent intent=getIntent();
        Toast.makeText(this, orderNo=intent.getStringExtra("orderNo"), Toast.LENGTH_SHORT).show();
       return orderNo=intent.getStringExtra("orderNo");

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


    private void addOrder(String orderDateString, String deliveryDateString,
                          String customerName,Boolean isHandWork,HashMap<String, String> items) {

        order order=new order(currentUser+deliveryDateString,currentUser,
                orderDateString,deliveryDateString,customerName,isHandWork,items,
                false);
        databaseOrders.child(getOrderNo()).setValue(order);
        //Adding order under orders child.
        common.incrementNoOfOrder("totalOrder");
        //Increment Total order
        addItemsToDatabase();
        //Add items under items child



    }


    //1. Inserting values to items tree
    //2. items is hashmap
    private void addItemsToDatabase()
    {


        databaseItems.setValue(items);



    }


    public String getCustomerName()
        {
           customerName=customerNameEditText.getText().toString().toLowerCase().trim();
          return customerName;
        }

        public String getItemName(){
            String ItemName;
            int ItemNameLength=itemNameEditText.getTokenValues().toString().length();
            ItemName= itemNameEditText.getTokenValues().toString().substring(1,ItemNameLength-1).toLowerCase().trim();
            return ItemName;}







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

            setTodayDate();
            delDateText.setText("");
            iSFetchAllItems=true;

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
            fetchAllItems();



        }

        public void setTodayDate ()
        {
            Calendar calendar = Calendar.getInstance();
            orderDateString= dateToString(calendar.getTime());
            orderDateText.setText(orderDateString);
        }

        public boolean validation() {
        return !deliveryDateString.equals("1") && !customerName.isEmpty()  && !itemNameEditText.getAllChips().isEmpty();

            //2.delivery date must be contain and must be future date.
            //3.orderno must be number and no in database - primary key
            //4.cutsomer name must be conatin
            //5.atleast one item
            //6.if all true visibileHandWorkLayout()


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


        //This function bottom up the calenderView
        public void openBottonCalender(Boolean isOrderDateCardSelected) {
        //If user clicks on orderDateCard
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
            //DELIVERY DATE CARD
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
            return dateToString;
        }
        public void setPickDelDateTextViewVisibility(int Visibility)
        {
            pickDelDateText.setVisibility(Visibility);
        }


        //getting value from edit text to items hashmap
        public void itemNameEditTextToHashMap()
        {
            int i=1;

            for (com.hootsuite.nachos.chip.Chip chip:itemNameEditText.getAllChips())
            {
                CharSequence text=chip.getText();
                String itemKey=text.toString().replaceAll("\\s","").toLowerCase();
                String itemName=text.toString().toLowerCase();
                items.put(itemKey,itemName);
                itemsToOrder.put("item"+i,itemKey);
                i++;

            }
        }
        public Boolean fetchAllItems()
        {

            databaseItems.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    String itemKey = dataSnapshot.getKey();
                    String itemName = (String) dataSnapshot.getValue();
                    if (!items.containsKey(itemKey))
                    items.put(itemKey,itemName);


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }
        public void setItemsSuggestionList(String itemName)

        {

            databaseItems.startAt(itemName).endAt(itemName+"\uf8ff").limitToFirst(3).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren())
                    {

                        try {
                            String itemKey=ds.getKey();
                            String itemName =  ds.getValue().toString();

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


    public void setItemSuggestionChipGroupVisibility(int Visibility) {
        itemSuggestionChipGroup.setVisibility(Visibility);
    }






    }





