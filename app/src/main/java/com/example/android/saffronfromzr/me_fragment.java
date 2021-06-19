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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.android.saffronfromzr.MainActivity.deliveryDate;
import static com.example.android.saffronfromzr.common.currentUser;
import static com.example.android.saffronfromzr.common.items;

public class me_fragment extends Fragment  {

    //XML
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noOrderMsgText;

    //Firebase objects
    private FirebaseRecyclerAdapter adapter;

    //VARIABLES
    private long itemCount;
//    private Boolean isEmpty=true;


    //OBJECTS
   private common common=new common();


   //CONSTRUCTOR
    public me_fragment() {

    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.orderListRecyclerView);
        noOrderMsgText=view.findViewById(R.id.noOrderMsgText);



        //SETTING PROGRESS BAR ON BEFORE fetchData()
        LinearLayoutManager linearLayoutManager;
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetch();
        //1.fetchData(), fetch all orders ie. selected delivery date orders

        return view;

    }
    private void showNoOrderMsg()
    {
        noOrderMsgText.setVisibility(View.VISIBLE);
    }
    //Fetch data from Fire base and add to Fire base RecyclerAdapter
    private void fetch() {


            Query query = FirebaseDatabase.getInstance().
                getReference("orders").orderByChild("designerId_deliveryDate").
                    equalTo(currentUser+deliveryDate);
            checkDataExists(query);



        FirebaseRecyclerOptions<orderbook> options =
                new FirebaseRecyclerOptions.Builder<orderbook>()
                        .setQuery(query, new SnapshotParser<orderbook>() {
                            @NonNull

                            @Override
                            public orderbook parseSnapshot(@NonNull DataSnapshot snapshot) {


                                setProgressBarOn();
//                                isEmpty=false;
                                String orderNo=snapshot.getKey();
                                Boolean isWorkComplete=(Boolean) snapshot.child("workComplete").

                                        getValue();
                                String designerId=(String) snapshot.child("designerId").getValue();
                                String customerName=(String) snapshot.child("customerName")
                                        .getValue();
                                Boolean isHandWork=(Boolean) snapshot.child("handWork").getValue();
                                String orderDate=(String) snapshot.child("orderDate").getValue();

                                itemCount=snapshot.child("items").getChildrenCount();
                                String item1=(String) snapshot.child("items").child("item1")
                                        .getValue();

                                setProgressBarOff();

                                if (itemCount==2)
                                {
                                  String item2=(String)snapshot.child("items").child("item2").
                                          getValue();


                                    return new orderbook(orderNo,designerId,isWorkComplete,
                                            customerName,orderDate,isHandWork,2,item1,
                                            item2);
                                }
                                else if (itemCount>=3)
                                {
                                    String item2=(String) snapshot.child("items").child("item2")
                                            .getValue();

                                  String item3=(String) snapshot.child("items").child("item3")
                                          .getValue();

                                    return new orderbook(orderNo,designerId,isWorkComplete,
                                            customerName,orderDate,isHandWork,itemCount,
                                            item1,item2,item3);

                                }


                                return new orderbook(orderNo,designerId,isWorkComplete,
                                        customerName,orderDate,isHandWork,1,item1);

                            }
                        }).build();


        adapter = new FirebaseRecyclerAdapter<orderbook,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i,
                                            @NonNull final orderbook orderbook) {
                viewHolder.cardNo.setText(getString(R.string.numValue,i+1));
                viewHolder.setCustomerName(common.makeFirstLetterCap(orderbook.getCustomerName()));
                viewHolder.setOrderNo(orderbook.getOrderNo());
                viewHolder.setOrderDate(orderbook.getOrderDateString());

                if (orderbook.getIsWorkComplete())
                {
                    viewHolder.showWorkCompleteTab();
                }

                if (orderbook.getIsHandWork())
                {
                    viewHolder.setHandWorkOnImageView();
                }
                else
                {
                    viewHolder.setHandWorkOffImageView();
                }

                viewHolder.item1TextView.setText(common.makeFirstLetterCap
                        (items.get(orderbook.getItem1())));
                if (itemCount>1)
                {
                    viewHolder.showItemsTextView(itemCount,orderbook);
                }

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



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView cardNo;
        private TextView customerName;
        private TextView orderNo;
        private TextView orderDate;

        private TextView item1TextView;
        private TextView item2TextView;
        private TextView item3TextView;
        private TextView noOfMoreItems;

        private ImageView handWorkOnImageView;
        private ImageView handWorkOffImageView;

        private LinearLayout designerNameLayout;
        private LinearLayout orderCompletedLayout;
        private LinearLayout item1Layout;
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

            designerNameLayout=itemView.findViewById(R.id.designerNameLayout);

            orderCompletedLayout=itemView.findViewById(R.id.orderCompletedLayout);

            item1Layout=itemView.findViewById(R.id.item1Layout);

            item1TextView=itemView.findViewById(R.id.item1TextView);

            item2Layout=itemView.findViewById(R.id.item2Layout);

            item2TextView=itemView.findViewById(R.id.item2TextView);

            item3Layout=itemView.findViewById(R.id.item3Layout);

            item3TextView=itemView.findViewById(R.id.item3TextView);
            noOfMoreItems=itemView.findViewById(R.id.noOfMoreItems);





        }
        //this function makes workCompleteTab in listItem of cardView visible
        private   void showWorkCompleteTab()
        {

            orderCompletedLayout.setVisibility(View.VISIBLE);

        }

        private void setCustomerName(String customerName) {
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

        public void setOrderDate(String orderDate) {
            this.orderDate.setText(orderDate);
        }

        private void showItemsTextView(long noOfItems,orderbook orderbook)
        {

            if (noOfItems==2)
            {


                item2TextView.setText(common.makeFirstLetterCap(items.
                        get(orderbook.getItem2())));

                item2Layout.setVisibility(View.VISIBLE);

            }
            else if (noOfItems>2)
            {
                item2TextView.setText(common.makeFirstLetterCap(items.
                        get(orderbook.getItem2())));

                item2Layout.setVisibility(View.VISIBLE);




                item3TextView.setText(common.makeFirstLetterCap(items.
                        get(orderbook.getItem3())));

                item3Layout.setVisibility(View.VISIBLE);




                if (noOfItems>3)
                {

                    String remainItems=Long.toString(noOfItems-3);
                    Log.d(TAG, "remainItems: "+remainItems);
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