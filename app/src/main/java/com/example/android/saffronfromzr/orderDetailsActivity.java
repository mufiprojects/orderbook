package com.example.android.saffronfromzr;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.saffronfromzr.R;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import static com.example.android.saffronfromzr.MainActivity.editModeOn;

public class orderDetailsActivity extends AppCompatActivity {

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseOrders;

    TextView orderNoView;
    TextView orderDateView;
    TextView deliveryDateView;
    TextView customerNameView;
    TextView itemsCountView;

    TextInputLayout codeTextLayout;
    TextInputEditText codeText;

    RelativeLayout workCompleteLayout; // Bind this local

    ImageView handWorkOnImageView;
    ImageView handWorkOffImageView;



    MaterialButton workCompleteButton; // bind this local
    MaterialButton backBtn;
    MaterialButton editBtn;

    ListView itemsListView;
    long itemsCount;
    String orderNo,customerName,orderDate,deliveryDate,userId,designerId,designerName;
    Boolean isHandWork;

    List<String> itemsList=new ArrayList<>();


    common common=new common();
    private boolean workComplete; // make it local variable

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        orderNoView=findViewById(R.id.orderNo);
        orderDateView=findViewById(R.id.orderDate);
        deliveryDateView=findViewById(R.id.deliveryDateView);
        customerNameView=findViewById(R.id.customerName);
        itemsCountView=findViewById(R.id.itemsCountView);
        handWorkOnImageView=findViewById(R.id.handWorkOnImageView);
        handWorkOffImageView=findViewById(R.id.handWorkOffImageView);


        databaseOrders=database.getReference("orders");

        common.fetchNoOfTotalCompletedOrders();
        common.fetchUserNoOfCompletedOrders();

        getData();

        setDataWithView();

        setUpBackBtn();




    }
    public void setUpBackBtn()
    {
        backBtn=findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDetailsActivity.super.finish();
            }
        });
    }
    public  void getData()
    {
        Intent intent=getIntent();
        orderNo=intent.getStringExtra("orderNo");
        designerId=intent.getStringExtra("designerId");
        customerName=intent.getStringExtra("customerName");
        orderDate=intent.getStringExtra("orderDate");
        deliveryDate=intent.getStringExtra("deliveryDate");
        fetchWorkComplete();
        Log.d("orderDetailsTest","workComplete "+workComplete);
        userId=common.getCurrentUser();
        Log.d("orderDetailsTest","userId "+userId);

        Log.d("orderDetailsTest","designerId "+designerId);

        try {
            itemsCount = intent.getExtras().getLong("itemsCount");
        }
        catch (NullPointerException e)
        {
            Log.d("exception","orderDetailsActivity line 69",e);
        }

        setItemsList(common.makeFirstLetterCap(common.items.get(intent.getStringExtra("item1"))));

        if (itemsCount==2)
        {
            setItemsList(common.makeFirstLetterCap(common.items.get(intent.getStringExtra("item2"))));
        }
        else if(itemsCount>2)
        {
            setItemsList(common.makeFirstLetterCap(common.items.get(intent.
                    getStringExtra("item2"))));

            setItemsList(common.makeFirstLetterCap(common.items.get(intent.
                    getStringExtra("item3"))));

            if (itemsCount>3)
            {
                fetchItemsFromDatabase();
            }
        }


        try {


            isHandWork = intent.getExtras().getBoolean("isHandWork");
        }
        catch (NullPointerException e)
        {
            Log.d("exception","orderDetailsActivity line 55",e);
        }
    }

    private void showWorkCompleteButton() {

        workCompleteButton=findViewById(R.id.workCompleteButton);
        workCompleteButton.setVisibility(View.VISIBLE);
        workCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseOrders.child(orderNo).child("workComplete").setValue(true);

                common.incrementNoOfOrder("completedOrder");

                workCompleteButton.setVisibility(View.INVISIBLE);
                showWorkCompleteTab();
            }
        });




    }

    private void showWorkCompleteTab() {
        workCompleteLayout=findViewById(R.id.workCompleteLayout);
        workCompleteLayout.setVisibility(View.VISIBLE);

    }

    private void showDesignerName() {
        TextView designerNameView;
        designerNameView=findViewById(R.id.designerName);
        designerNameView.setVisibility(View.VISIBLE);
        designerNameView.setText(getString(R.string.orderTakenBy,common.makeFirstLetterCap(common.userHashMap.get(designerId))));


    }

    private void fetchItemsFromDatabase() {

        databaseOrders.child(orderNo).child("items").limitToLast((int)itemsCount-3).orderByKey().
                addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                setItemsList(common.items.get(dataSnapshot.getValue().toString()));


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


    }
    private void fetchWorkComplete()
    {

        databaseOrders.child(orderNo).child("workComplete").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workComplete= (Boolean) dataSnapshot.getValue();

                if (designerId!=null) {
                    if (!userId.equals(designerId)) {
                        showDesignerName();

                        if (workComplete) {
                            showWorkCompleteTab();

                        }
                    } else {
                        editBtnListner();

                        if (workComplete) {
                            showWorkCompleteTab();



                        } else {

                            showWorkCompleteButton();




                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void hideWorkCompleteButton() {

    }

    public void setDataWithView()
    {
        orderNoView.setText(orderNo);
        customerNameView.setText(common.makeFirstLetterCap(customerName));
        orderDateView.setText(getString(R.string.orderDateOnDetails,orderDate));
        deliveryDateView.setText(getString(R.string.deliveryDateOnDetails,deliveryDate));

        setAdapter();

        if (isHandWork)
        {
            setHandWorkOn();
        }
        else
        {
            setHandWorkOff();
        }
    }

    private void setHandWorkOff() {
        handWorkOffImageView.setVisibility(View.VISIBLE);

    }

    private void setHandWorkOn() {
        handWorkOnImageView.setVisibility(View.VISIBLE);

    }
    private void setItemsList(String itemName)
    {
        itemsList.add(common.makeFirstLetterCap(itemName));
    }
    private void setAdapter()
    {
        itemsListView=findViewById(R.id.itemsListView);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                itemsList);

            itemsListView.setAdapter(arrayAdapter);



    }
    private void editBtnListner(){
        editBtn=findViewById(R.id.editButton);
        editBtn.setVisibility(View.VISIBLE);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCodeEditText();

            }
        });

    }
    private void codeTextListner(){
        codeText=findViewById(R.id.codeText);
        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getCode().equals("04885")){
                    openOrderActivity();
                    setEditModeOn();
                }

            }
        });
    }

    private void setEditModeOn() {
       editModeOn=true;

    }

    private void openOrderActivity() {
        Intent orderActivity = new Intent(getApplicationContext(),
                OrderActivity.class);
        orderActivity.putExtra("orderNo",orderNo);
        orderActivity.putExtra("customerName",customerName);
        orderActivity.putExtra("orderDate",orderDate);
        startActivity(orderActivity);
    }

    private void showCodeEditText() {
        codeTextLayout=findViewById(R.id.codeTextLayout);
        codeTextLayout.setVisibility(View.VISIBLE);
        codeTextListner();
    }
    public String getCode() {

        return  codeText.getText().toString().trim();

    }


}




