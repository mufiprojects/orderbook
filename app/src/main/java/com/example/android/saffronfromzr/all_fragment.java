package com.example.android.saffronfromzr;

import android.content.Context;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class all_fragment extends Fragment {
    private RecyclerView recyclerView;


    private FirebaseRecyclerAdapter adapter;
    private ProgressBar progressBar;







    String deliveryDate;


    int count = 0;
   private long itemCount;




   private FirebaseRecyclerOptions<orderbook> options;
    private common common=new common();

    public all_fragment() {

    }
    public all_fragment(String deliveryDate)
    {
        this.deliveryDate=deliveryDate;
    }


    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_fragment, container, false);
        MainActivity mainActivity=new MainActivity();
        deliveryDate=mainActivity.deliveryDate;

        progressBar = view.findViewById(R.id.progressBar);
        setProgressBarOn();

        recyclerView = view.findViewById(R.id.orderListRecyclerView);
         LinearLayoutManager linearLayoutManager;
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetchData();
        fetchDataToAdapter();

        return view;
    }





    public void fetchData() {

        final Query query = FirebaseDatabase.getInstance().
                getReference().child("orders").orderByChild("deliveryDate").equalTo(deliveryDate);


        options =
                new FirebaseRecyclerOptions.Builder<orderbook>()
                        .setQuery(query, new SnapshotParser<orderbook>() {
                            @NonNull

                            @Override
                            public orderbook parseSnapshot(@NonNull final DataSnapshot snapshot) {


                                String orderNo = snapshot.getKey();
                                String customerName = (String) snapshot.child("customerName").getValue();
                                Boolean isWorkComplete=(Boolean)snapshot.child("workComplete").getValue();
                                Boolean isHandWork = (Boolean) snapshot.child("handWork").getValue();
                                String orderDate = (String) snapshot.child("orderDate").getValue();
                                String designerId = (String) snapshot.child("designerId").getValue();
                                itemCount=snapshot.child("items").getChildrenCount();
                                String item1=snapshot.child("items").child("item1").getValue().toString();
                                setProgressBarOff();

                                if (itemCount==2)
                                {
                                    String item2=snapshot.child("items").child("item2").getValue().toString();
                                    return new orderbook(common.userHashMap.get(designerId),designerId,isWorkComplete, orderNo, customerName, orderDate, isHandWork,item1,item2,2);





                                }
                                else if (itemCount>=3)
                                {
                                    String item2=snapshot.child("items").child("item2").getValue().toString();
                                    String item3=snapshot.child("items").child("item3").getValue().toString();
                                    return new orderbook(common.userHashMap.get(designerId),designerId,isWorkComplete,orderNo, customerName, orderDate, isHandWork,item1,item2,item3,itemCount);



                                }


                                return new orderbook(common.userHashMap.get(designerId),designerId,isWorkComplete, orderNo, customerName, orderDate, isHandWork,item1,itemCount);


                            }
                        }).build();
    }
    public void fetchDataToAdapter()

    {
        adapter = new FirebaseRecyclerAdapter<orderbook, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i,
                                            @NonNull final orderbook orderbook) {
                if (orderbook.getIsWorkComplete())
                {
                    viewHolder.showWorkCompleteTab();
                }
                viewHolder.cardNo.setText(Integer.toString(i+1));
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

                viewHolder.itemNo1TextView.setText("1");
                viewHolder.item1TextView.setText(common.makeFirstLetterCap(common.items.get(orderbook.getItem1())));
                if (itemCount>1)
                {
                    viewHolder.showItemsTextView(itemCount,orderbook);
                }
                viewHolder.designerNameLayout.setVisibility(View.VISIBLE);
                viewHolder.setDesignerNameText(common.makeFirstLetterCap(orderbook.getDesignerName()));
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






    public void setProgressBarOn()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarOff(){
        progressBar.setVisibility(View.GONE);
    }


    private void toOrderDetailsActivity(orderbook orderbook) {
        Intent orderDetailsActivity = new Intent(getContext(), orderDetailsActivity.class);
        common.putExtra(orderDetailsActivity,orderbook);
        startActivity(orderDetailsActivity);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public  TextView cardNo;
        public TextView customerName;
        public  TextView orderNo;
        public TextView orderDate;
        public  ImageView handWorkOnImageView;
        public  ImageView handWorkOffImageView;
        public TextView designerNameText;
        LinearLayout designerNameLayout;
          LinearLayout orderCompletedLayout;

        public    LinearLayout item1Layout;
        public  TextView itemNo1TextView;
        public TextView item1TextView;
        public  LinearLayout item2Layout;
        public  TextView itemNo2TextView;
        public TextView item2TextView;
        public  LinearLayout item3Layout;
        public  TextView itemNo3TextView;
        public TextView item3TextView;
        public  TextView noOfMoreItems;

        public ViewHolder(View itemView) {
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


            item1Layout=itemView.findViewById(R.id.item1Layout);
            itemNo1TextView=itemView.findViewById(R.id.itemNo1TextView);
            item1TextView=itemView.findViewById(R.id.item1TextView);

            item2Layout=itemView.findViewById(R.id.item2Layout);
            itemNo2TextView=itemView.findViewById(R.id.itemNo2TextView);
            item2TextView=itemView.findViewById(R.id.item2TextView);

            item3Layout=itemView.findViewById(R.id.item3Layout);
            itemNo3TextView=itemView.findViewById(R.id.itemNo3TextView);
            item3TextView=itemView.findViewById(R.id.item3TextView);
            noOfMoreItems=itemView.findViewById(R.id.noOfMoreItems);



        }

        public  void showWorkCompleteTab()
        {

            orderCompletedLayout.setVisibility(View.VISIBLE);

        }

        public void setDesignerNameText(String designerNameText) {
            this.designerNameText.setText(designerNameText);
        }

        public void setCustomerName(String customerName) {
            this.customerName.setText(customerName);

        }
        public void setOrderNo(String orderNo){
            this.orderNo.setText(orderNo);
        }

        public void setHandWorkOnImageView() {
            handWorkOffImageView.setVisibility(View.GONE);
            this.handWorkOnImageView.setVisibility(View.VISIBLE);
        }

        public void setHandWorkOffImageView() {
            handWorkOnImageView.setVisibility(View.GONE);
            handWorkOffImageView.setVisibility(View.VISIBLE);

        }


        public void setOrderDate(String orderDate) {
            this.orderDate.setText(orderDate);
        }


        public void setCardNo(int cardNo) {
            this.cardNo.setText(cardNo);
        }
        public void showItemsTextView(long noOfItems,orderbook orderbook)
        {

            if (noOfItems==2)
            {


                item2TextView.setText(common.makeFirstLetterCap(common.items.get(orderbook.getItem2())));
                itemNo2TextView.setText("2");
                Log.d("items","if item is 2"+common.items.get(orderbook.getItem2()));
                item2Layout.setVisibility(View.VISIBLE);

            }
            else if (noOfItems>2)
            {
                item2TextView.setText(common.makeFirstLetterCap(common.items.get(orderbook.getItem2())));
                itemNo2TextView.setText("2");
                item2Layout.setVisibility(View.VISIBLE);

                Log.d("items","if item is greater than 2"+common.items.get(orderbook.getItem2()));



                item3TextView.setText(common.makeFirstLetterCap(common.items.get(orderbook.getItem3())));
                itemNo3TextView.setText("3");
                item3Layout.setVisibility(View.VISIBLE);



                Log.d("items","if item is greater than 2"+common.items.get(orderbook.getItem3()));

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


}