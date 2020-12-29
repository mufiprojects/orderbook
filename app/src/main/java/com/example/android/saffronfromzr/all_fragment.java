package com.example.android.saffronfromzr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.android.saffronfromzr.MainActivity.deliveryDate;
import static com.example.android.saffronfromzr.common.items; //ITEM KEY | ITEM NAME
import static com.example.android.saffronfromzr.common.userHashMap; //USER ID | USER NAME

public class all_fragment extends Fragment {

    //XML
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noOrderMsgText;

    //FIREBASE OBJECTS
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerOptions<orderbook> options;


    //VARIABLES
    private long itemCount;

    //OBJECTS
    private common common=new common();


    //CONSTRUCTOR
    public all_fragment() {

    }



    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_fragment, container, false);


        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.orderListRecyclerView);
        noOrderMsgText=view.findViewById(R.id.noOrderMsgText);

//         LinearLayoutManager linearLayoutManager;
//        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,
//                false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);

        int numberOfColumns=2;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),numberOfColumns));

            fetchData();
            fetchDataToAdapter();
            //1.fetchData(), fetch all orders ie. selected delivery date orders
            //2.fetchDataToAdapter, load all that data's to an FirebaseRecyclerAdapter

        return view;
    }
    //Fetch data from Fire base and add to Fire base RecyclerAdapter
    private void fetchData() {
        final Query query = FirebaseDatabase.getInstance().
                getReference().child("orders").orderByChild("deliveryDate").equalTo(deliveryDate);
        //Query for fetch data
        checkDataExists(query);
        options =
                new FirebaseRecyclerOptions.Builder<orderbook>()
                        .setQuery(query, new SnapshotParser<orderbook>() {
                            @NonNull

                            @Override
                            public orderbook parseSnapshot(@NonNull final DataSnapshot snapshot) {


                                String orderNo = snapshot.getKey();
                                String customerName = (String) snapshot.child("customerName")
                                        .getValue();
                                Boolean isWorkComplete=(Boolean)snapshot.child("workComplete")
                                        .getValue();
                                Boolean isHandWork = (Boolean) snapshot.child("handWork")
                                        .getValue();
                                String orderDate = (String) snapshot.child("orderDate")
                                        .getValue();
                                String designerId = (String) snapshot.child("designerId")
                                        .getValue();
                                itemCount=snapshot.child("items").getChildrenCount();
                                String item1=(String) snapshot.child("items").child("item1")
                                        .getValue();
                                setProgressBarOff();

                                if (itemCount==2)
                                {
                                    String item2=(String) snapshot.child("items").child("item2")
                                            .getValue();
                                    return new orderbook(userHashMap.get(designerId),designerId,
                                            isWorkComplete, orderNo, customerName, orderDate,
                                            isHandWork,item1,item2,2);
                                }
                                else if (itemCount>=3)
                                {
                                    String item2=(String) snapshot.child("items").child("item2")
                                            .getValue();
                                    String item3=(String) snapshot.child("items").child("item3")
                                            .getValue();
                                    return new orderbook(userHashMap.get(designerId),designerId,
                                            isWorkComplete,orderNo, customerName, orderDate,
                                            isHandWork,item1,item2,item3,itemCount);
                                }


                                return new orderbook(userHashMap.get(designerId),designerId,
                                        isWorkComplete, orderNo, customerName, orderDate,
                                        isHandWork,item1,itemCount);
                            }
                        }).build();
    }

    private void checkDataExists(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    showNoOrderMsg();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNoOrderMsg() {
            noOrderMsgText.setVisibility(View.VISIBLE);
    }

    private void fetchDataToAdapter()

    {
        adapter = new FirebaseRecyclerAdapter<orderbook, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i,
                                            @NonNull final orderbook orderbook) {
                if (orderbook.getIsWorkComplete())
                {
                    viewHolder.showWorkCompleteTab();
                }

                viewHolder.cardNo.setText(getString(R.string.numValue,i+1));
                viewHolder.setCustomerName(common.makeFirstLetterCap(orderbook.getCustomerName()));
                viewHolder.setOrderNo(orderbook.getOrderNo());
                viewHolder.setOrderDate(orderbook.getOrderDateString());
                if (orderbook.getIsHandWork())
                {
                    viewHolder.setHandWorkOnImageView();
                }
                else
                {
                    viewHolder.setHandWorkOffImageView();
                }


                viewHolder.item1TextView.setText(
                        common.makeFirstLetterCap(items.get(orderbook.getItem1())));
                viewHolder.checkOperationStatus(orderbook,1,orderbook.getItem1());


                if (itemCount>1)
                {
                    viewHolder.showItemsTextView(itemCount,orderbook);
                }


                viewHolder.designerNameLayout.setVisibility(View.VISIBLE);
                viewHolder.setDesignerNameText(
                        common.makeFirstLetterCap(orderbook.getDesignerName()));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toOrderDetailsActivity(orderbook);
                    }
                });



            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist,
                        parent, false);
                return new ViewHolder(view);

            }

        };
        recyclerView.setAdapter(adapter);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardNo;
        private TextView customerName;
        private TextView orderNo;
        private TextView orderDate;
        private TextView designerNameText;


        private TextView item1TextView;
        private TextView item2TextView;
        private TextView item3TextView;
        private TextView noOfMoreItems;

        private TextView item1operationStatusTextView;
        private TextView item2operationStatusTextView;
        private TextView item3operationStatusTextView;

        private ImageView handWorkOnImageView;
        private ImageView handWorkOffImageView;


        private LinearLayout designerNameLayout;
        private LinearLayout orderCompletedLayout;

        private LinearLayout item2Layout;
        private LinearLayout item3Layout;




        private ViewHolder(View itemView) {
            super(itemView);
            cardNo=itemView.findViewById(R.id.cardNo);
            customerName = itemView.findViewById(R.id.customerNameOnCard);
            orderNo=itemView.findViewById(R.id.orderNoOnCard);
            orderDate=itemView.findViewById(R.id.orderDateOnCard);
            handWorkOnImageView=itemView.findViewById(R.id.handWorkOnImageView);
            handWorkOffImageView=itemView.findViewById(R.id.handWorkOffImageView);
            designerNameText=itemView.findViewById(R.id.designerNameText);
            designerNameLayout=itemView.findViewById(R.id.designerNameLayout);
            orderCompletedLayout=itemView.findViewById(R.id.orderCompletedLayout);




            item1TextView=itemView.findViewById(R.id.item1TextView);

            item2Layout=itemView.findViewById(R.id.item2Layout);

            item2TextView=itemView.findViewById(R.id.item2TextView);

            item3Layout=itemView.findViewById(R.id.item3Layout);

            item3TextView=itemView.findViewById(R.id.item3TextView);
            noOfMoreItems=itemView.findViewById(R.id.noOfMoreItems);
            item1operationStatusTextView=itemView.findViewById(R.id.item1OperationStatus);
            item2operationStatusTextView=itemView.findViewById(R.id.item2OperationStatus);
            item3operationStatusTextView=itemView.findViewById(R.id.item3OperationStatus);



        }

        private   void showWorkCompleteTab()
        {

            orderCompletedLayout.setVisibility(View.VISIBLE);

        }

        private void setDesignerNameText(String designerNameText) {
            this.designerNameText.setText(designerNameText);
        }

        public void setCustomerName(String customerName) {
            this.customerName.setText(customerName);

        }
        private void setOrderNo(String orderNo){
            this.orderNo.setText(orderNo);
        }

        private void setHandWorkOnImageView() {
            handWorkOffImageView.setVisibility(View.GONE);
            this.handWorkOnImageView.setVisibility(View.VISIBLE);
        }

        private void setHandWorkOffImageView() {
            handWorkOnImageView.setVisibility(View.GONE);
            handWorkOffImageView.setVisibility(View.VISIBLE);

        }


        private void setOrderDate(String orderDate) {
            this.orderDate.setText(orderDate);
        }


        private void showItemsTextView(long noOfItems,orderbook orderbook)
        {

            if (noOfItems==2)
            {


                item2TextView.setText(common.makeFirstLetterCap(items.get(orderbook.getItem2())));
                checkOperationStatus(orderbook,2,orderbook.getItem2());
                item2Layout.setVisibility(View.VISIBLE);

            }
            else if (noOfItems>2)
            {
                item2TextView.setText(common.makeFirstLetterCap(items.get(orderbook.getItem2())));
                item2Layout.setVisibility(View.VISIBLE);
                checkOperationStatus(orderbook,2,orderbook.getItem2());
                item3TextView.setText(common.makeFirstLetterCap(items.get(orderbook.getItem3())));
                checkOperationStatus(orderbook,3,orderbook.getItem3());
                item3Layout.setVisibility(View.VISIBLE);

                if (noOfItems>3)
                {
                    String remainItems=Long.toString(noOfItems-3);
                    noOfMoreItems.setVisibility(View.VISIBLE);
                    noOfMoreItems.setText(getString(R.string.remainItems,remainItems));

                }
            }

        }

        private void checkOperationStatus(final orderbook orderbook, final int itemNo, final String item) {


            Log.d("operationStatus","method runned");
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            DatabaseReference operationStatusDb= database.getReference("workFlow");


                    operationStatusDb.child("operationStatus")
                    .child(orderbook.getOrderNo())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Log.d("operationStatus","childAdded");
                            ArrayList<String> status=new ArrayList<>();
                            Boolean patternStatus;


                            try {
                                patternStatus = (Boolean) Objects.requireNonNull(dataSnapshot.child(item)
                                        .child("pattern").getValue());
                                if(Objects.requireNonNull(patternStatus)){
                                    status.add("P");}
                            }
                            catch (Exception e){
                                Log.d("exceptions","exception");
                            }


                            try {

                                Boolean handworkStatus = (Boolean) Objects.requireNonNull(dataSnapshot.child(item)
                                        .child("handwork").getValue());
                                if (Objects.requireNonNull(handworkStatus)) {
                                    status.add("H");
                                }
                            }
                            catch (Exception e){
                                Log.d("exceptions","exception");
                            }
                            try {
                                Boolean cuttingStatus = (Boolean) Objects.requireNonNull(dataSnapshot.child(item)
                                        .child("cutting").getValue());
                                if(Objects.requireNonNull(cuttingStatus)){
                                    status.add("C");}
                            }
                            catch (Exception e){
                                Log.d("exceptions","exception");
                            }
                            try {

                                Boolean stitchingStatus = (Boolean) Objects.requireNonNull(dataSnapshot.child(item)
                                        .child("stitching").getValue());
                                if (Objects.requireNonNull(stitchingStatus)) {
                                    status.add("S");

                                }
                            }
                            catch (Exception e){
                                Log.d("exceptions","exception");
                            }

//                                Log.d("operationStatus", String.valueOf(cuttingStatus));

                                    Log.d("status","orderno"+orderbook.getOrderNo()+
                                            "itemName"+item+" "+status.toString());
                                    writeOperation(String.valueOf(status), itemNo);
                                    status.clear();



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        private void writeOperation(String operation,int itemNo) {

            switch (itemNo){
                case 1:
                    item1operationStatusTextView.setText(operation);
                    break;
                case 2:
                    item2operationStatusTextView.setText(operation);
                    break;
                case 3:
                    item3operationStatusTextView.setText(operation);
                    break;
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private void setProgressBarOn()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarOff(){
        progressBar.setVisibility(View.GONE);
    }

    //Moving to orderDetailsActivity by intent
    private void toOrderDetailsActivity(orderbook orderbook) {
        Intent orderDetailsActivity = new Intent(getContext(), orderDetailsActivity.class);
        common.putExtra(orderDetailsActivity,orderbook);
        startActivity(orderDetailsActivity);
    }






}