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

public class OrderActivity extends AppCompatActivity  {

    TextView orderDateText;
    TextView delDateText;
    TextView pickDelDateText;
    TextView addOrderTextView;
    TextInputEditText customerNameEditText;
    NachoTextView itemNameEditText;

    CardView orderDateCard, delDateCard;



    RadioGroup isHandWork;
    RadioButton handWorkOn, handWorkOff;

    ChipGroup itemSuggestionChipGroup;



    private String deliveryDateString = "1";
    private String orderDateString;
    private String customerName = "";

    String orderNo;



    List<order> order;
    List<String> stringChipList;
    List<com.hootsuite.nachos.chip.Chip> chipList;
    public int chipListLength;

    Map<String, String> items = new HashMap<>();
    ;
    HashMap<String, String> itemsToOrder = new HashMap<>();
    HashMap<String, String> itemsSuggestionList = new HashMap<>();

    final int VISIBILE = 0;
    final int INVISBILE = 4;
    final int GONE = 8;

    //VARIABLES

    //FINAL VARIABLES
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    //OBJECTS
    common common = new common();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);





        fetchAllItems();

        common.fetchNoOfTotalOrders();
        common.fetchNoOfUserTotalOrders();


        order = new ArrayList<>();
        stringChipList = new ArrayList<>();
        chipList = new ArrayList<>();


        //Binding
        orderDateText =  findViewById(R.id.orderDateText);
        delDateText =  findViewById(R.id.delDateText);
        pickDelDateText = findViewById(R.id.pickDelDateText);

        addOrderTextView =  findViewById(R.id.addOrderTextView);
        orderDateCard = findViewById(R.id.orderDateCard);
        delDateCard =  findViewById(R.id.delDateCard);

        customerNameEditText = (TextInputEditText) findViewById(R.id.customerName);
        itemNameEditText = (NachoTextView) findViewById(R.id.itemNameEditText);

        isHandWork =  findViewById(R.id.isHandWork);
        handWorkOn =  findViewById(R.id.handWorkOn);
        handWorkOff =  findViewById(R.id.handWorkOff);









        itemSuggestionChipGroup = (ChipGroup) findViewById(R.id.itemSuggestionChipGroup);


        getOrderNo();
        setTodayDate();
        addOrderTextView.setText(getString(R.string.addorder, getOrderNo()));
        //Todo (6): Make pickdates only one object

//        orderDateCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openBottonCalender(true);
//            }
//        });

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
                //Todo (7): resetItemSuggestion(), Why this function running before and after text changes of customerNameEditText
                //Defines in itemNameEdit


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                resetItemSuggestion();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getCustomerName();
                if (validation()) {
                    setHandWorkLayoutVisibility(VISIBILE);
                    setAddOrderTextViewVisibility(INVISBILE);

                } else {
                    setHandWorkLayoutVisibility(GONE);
                    setAddOrderTextViewVisibility(VISIBILE);

                }

            }
        });
        itemNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                getCustomerName();
                if (getItemName().isEmpty()) {
                    //getItemName() returns a string value
                    //that is un chipped string
                    //if it is empty that is called we want to resetItemSuggestion
                    // for work addChips() in setItemSuggestionList()
                    //if we don't reset it next item search wii not perform

                    resetItemSuggestion();

                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                itemNameEditText.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

                if (getItemName().isEmpty()) {
                    resetItemSuggestion();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                getItemName();
                setItemsSuggestionList(getItemName());

                if (validation()) {
                    setHandWorkLayoutVisibility(VISIBILE);
                    setAddOrderTextViewVisibility(INVISBILE);

                } else {
                    setHandWorkLayoutVisibility(GONE);
                    setAddOrderTextViewVisibility(VISIBILE);

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
                //Todo (9): itemNameEditTextToHashmap(), What this function do ?
                //Converts items in itemNameEditText to items hashmap for items tree in Db

                addOrder(orderDateString, deliveryDateString, customerName, isHandWork, itemsToOrder);
                progressBarOff();         //Todo (10): Make ProgressBarOff() after only order uploads in DB


            }

        });


    }


    private void addOrder(String orderDateString, String deliveryDateString,
                          String customerName,Boolean isHandWork,HashMap<String, String> items) {

        order order=new order(currentUser+deliveryDateString,currentUser,
                orderDateString,deliveryDateString,customerName,isHandWork,items,
                false);
        DatabaseReference databaseOrders=FirebaseDatabase.getInstance().getReference("orders");

        databaseOrders.child(getOrderNo()).setValue(order);
        //Adding order under orders child.
        makeOrderActive();
        //Making orderActive for workflow.
        common.incrementNoOfOrder("totalOrder");
        //Increment Total order
        addItemsToDatabase();
        //Add items under items child
        backToLastActivity();


    }
    //1. Inserting values to items tree
    //2. items is Hash Map
    private void addItemsToDatabase()
    {

       DatabaseReference databaseItems = FirebaseDatabase.getInstance().getReference("items");
        databaseItems.setValue(items);



    }
    private void makeOrderActive()
    {
        //WorkFlow
        DatabaseReference databaseItems = FirebaseDatabase.getInstance().getReference("orderNoActive");
        databaseItems.child(getOrderNo()).setValue(true);
    }
    //Getting  value from edit text to items HashMap
    public void itemNameEditTextToHashMap()
    {
        int i=1;

        for (com.hootsuite.nachos.chip.Chip chip:itemNameEditText.getAllChips())
        {
            CharSequence text=chip.getText();
            String itemKey=text.toString().replaceAll("\\s","").toLowerCase();
            String itemName=text.toString().toLowerCase();
            items.put(itemKey,itemName);
            //items tree in DB
            itemsToOrder.put("item"+i,itemKey);
            //order tree
            i++;

        }
    }
    //Todo (11) fetchAllItems() , In future avoid this function
    //fetch items tree to items HashMap for preventing deletes for previous items in items tree
    public Boolean fetchAllItems()
    {
        DatabaseReference databaseItems = FirebaseDatabase.getInstance().getReference("items");
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



    private void backToLastActivity() {
        OrderActivity.super.finish();
    }







        //GETTER METHODS
        private String getOrderNo() {
            Intent intent = getIntent();
            return orderNo = intent.getStringExtra("orderNo");
            // Set Variable orderNo by intent putExtra()
        }
        public String getCustomerName()
        {
           customerName=customerNameEditText.getText().toString().toLowerCase().trim();
          return customerName;
        }

        public String getItemName(){
            String ItemName;
            int ItemNameLength=itemNameEditText.getTokenValues().toString().length();
            ItemName= itemNameEditText.getTokenValues().toString().substring(1,ItemNameLength-1).
                    toLowerCase().trim();
            return ItemName;}
            //GETTER METHODS






    public void progressBarOn () {
        ProgressBar progressBar=findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        public void progressBarOff () {
            ProgressBar progressBar=findViewById(R.id.progressBar);

            progressBar.setVisibility(View.GONE);

        }



        //SETTING SYSTEM DATE

        public void setTodayDate ()
        {
            Calendar calendar = Calendar.getInstance();
            orderDateString= dateToString(calendar.getTime());
            orderDateText.setText(orderDateString);
        }

        public boolean validation() {
        return !deliveryDateString.equals("1") && !customerName.isEmpty()  && !itemNameEditText.getAllChips().isEmpty();

            //2.delivery date must be contain and must be future date.
            //3.order no must be number and no in database - primary key
            //4.Customer name must be contain
            //5.At least one item
            //6.if all true VisibleHandWorkLayout()


        }

        public void setHandWorkLayoutVisibility(int visibility)
        {
            LinearLayout handWorkLayout;
            handWorkLayout=findViewById(R.id.handWorkLayout);
            handWorkLayout.setVisibility(visibility);

        }
       public void setAddOrderTextViewVisibility(int visibility)
        {
            addOrderTextView.setVisibility(visibility);
        }


//         .setTab0DisplayMinutes(false)
//                        .setTab0DisplayHours(false)
//                        .setTab0DisplayDays(true)
//                        .setTab1DisplayMinutes(false)
//                        .setTab1DisplayHours(false)
//                        .setTab1DisplayDays(true)
//              .mainColor(getResources().getColor(R.color.white))
//            .titleTextColor(getResources().getColor(R.color.white))
//            .bottomSheet()

        //This function bottom up the calenderView
        public void openBottonCalender(Boolean isOrderDateCardSelected) {
        //If user clicks on orderDateCard
            if (isOrderDateCardSelected) {
                //Todo(13):Fix Bug
                new DoubleDateAndTimePickerDialog.Builder(this)


                        .title("SELECT DATES")
                        .tab0Text("ORDER DATE")
                        .tab1Text("DELIVERY DATE")
                        .backgroundColor(getResources().getColor(R.color.white))
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

                                }
                                else
                                {
                                    setHandWorkLayoutVisibility(GONE);
                                    setAddOrderTextViewVisibility(VISIBILE);

                                }


                            }
                        }).display();


            }
            //DELIVERY DATE CARD
            else
            {

                new SingleDateAndTimePickerDialog.Builder(this)

                        .title("PICK DELIVERY DATE")
                        .setTimeZone(TimeZone.getDefault())
                        .displayDays(false)
                        .displayMonth(true)
                        .displayYears(true)
                        .displayDaysOfMonth(true)
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
                                Log.d("selectedDate",dateToString(date));
                                deliveryDateString=dateToString(date);
                                setPickDelDateTextViewVisibility(GONE);
                                delDateText.setText(deliveryDateString);

                            }
                        })
                        .display();



            }
                        common.keyBoardUp(this);
            if (validation()) {
                setHandWorkLayoutVisibility(VISIBILE);
                setAddOrderTextViewVisibility(INVISBILE);

            }
            else
            {
                setHandWorkLayoutVisibility(GONE);
                setAddOrderTextViewVisibility(VISIBILE);

            }
        }

        //CONVERTS DATE AND RETURN TO STRING IN DD/MM/YYYY FORMAT
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



    //METHODS BELONG TO ITEM SUGGESTION FEATURE IN APP
        //Set items suggestion by fetching itemKey and itemName in items tree and
        //and pass itemName to addChips() by parameter
        public void setItemsSuggestionList(String itemName)

        {
            DatabaseReference databaseItems = FirebaseDatabase.getInstance().getReference("items");

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
                //Get all chips values in String
                chipListLength = stringChipList.size();
                // Setting stringChipList list size

                if (chipListLength == 0) {

                    itemNameEditText.setText(chip.getText());
                } else {
                    stringChipList.add(chipListLength, chip.getText().toString());
                    itemNameEditText.setText(stringChipList);
                }
                itemNameEditText.addChipTerminator('\n',
                        ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

                //If chipsListLength=0
                //1. Set itemNameEditText = chip text value
                //else
                //1.Add chip text to stringChipList where position = stringChipListLength
                //2.set all values in stringChipList in itemNameEditText

                //  set itemNameEdit Values chips by chipTerminator or '\n'

            }

        });
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
    //METHODS BELONG TO ITEM SUGGESTION FEATURE IN APP



    //UNUSED METHODS MAY BE USED IN FUTURE
    public void dataReset() {

        setTodayDate();
        delDateText.setText("");


        customerNameEditText.clearFocus();
        customerNameEditText.setText("");

        itemNameEditText.clearFocus();
        itemNameEditText.setText("");
        resetItemSuggestion();
        items.clear();

        stringChipList.clear();


        setPickDelDateTextViewVisibility(VISIBILE);

        setHandWorkLayoutVisibility(GONE);
        setAddOrderTextViewVisibility(VISIBILE);

        fetchAllItems();



    }
    //UNUSED METHODS MAY BE USED IN FUTURE


    }





