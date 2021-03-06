package com.example.android.saffronfromzr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.android.saffronfromzr.MainActivity.deliveryDate;
import static com.example.android.saffronfromzr.common.items;
import static com.example.android.saffronfromzr.common.userHashMap;

public class uncompleted extends AppCompatActivity {
    private RecyclerView recyclerView;

    //FIREBASE OBJECTS
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerOptions<orderbook> options;
    //VARIABLES
    private long itemCount;

    //OBJECTS
    private common common = new common();
    public uncompleted()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uncompleted);
        recyclerView = findViewById(R.id.unCompletedRecyclerView);

//        LinearLayoutManager linearLayoutManager;
//        linearLayoutManager = new LinearLayoutManager
//                (this, LinearLayoutManager.VERTICAL,
//                false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);

        int numberOfColumns=2;
        recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColumns));


        fetchData();
        fetchDataToAdapter();
    }
    private void fetchData() {
        final Query query = FirebaseDatabase.getInstance().
                getReference().child("orders").startAt("900").orderByKey();
        //Query for fetch data

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
    private void fetchDataToAdapter()

    {
        adapter = new FirebaseRecyclerAdapter<orderbook, uncompleted.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull uncompleted.ViewHolder viewHolder, int i,
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
//                        toOrderDetailsActivity(orderbook);
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
                item2Layout.setVisibility(View.VISIBLE);

            }
            else if (noOfItems>2)
            {
                item2TextView.setText(common.makeFirstLetterCap(items.get(orderbook.getItem2())));
                item2Layout.setVisibility(View.VISIBLE);

                item3TextView.setText(common.makeFirstLetterCap(items.get(orderbook.getItem3())));
                item3Layout.setVisibility(View.VISIBLE);

                if (noOfItems>3)
                {
                    String remainItems=Long.toString(noOfItems-3);
                    noOfMoreItems.setVisibility(View.VISIBLE);
                    noOfMoreItems.setText(getString(R.string.remainItems,remainItems));

                }
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

}