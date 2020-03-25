package com.example.android.saffronfromzr;

import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class me_fragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private ProgressBar progressBar;


    public me_fragment() {

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        progressBar=view.findViewById(R.id.progressBar);
        setProgressBarOn();
        recyclerView = view.findViewById(R.id.orderListRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
        return view;
    }

    private void fetch() {
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().
                getReference("orders").child(uid);

        FirebaseRecyclerOptions<orderbook> options =
                new FirebaseRecyclerOptions.Builder<orderbook>()
                        .setQuery(query, new SnapshotParser<orderbook>() {
                            @NonNull

                            @Override
                            public orderbook parseSnapshot(@NonNull DataSnapshot snapshot) {

                                String orderNo=snapshot.getKey();

                                String customerName=(String) snapshot.child("customerName")
                                        .getValue();

                                Boolean isHandWork=(Boolean) snapshot.child("handWork").getValue();
                                String orderDate=(String) snapshot.child("orderDate").getValue();
                                setProgressBarOff();
                                return new orderbook(orderNo,customerName,orderDate,isHandWork);

                            }
                        }).build();
        adapter = new FirebaseRecyclerAdapter<orderbook, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i,
                                            @NonNull orderbook orderbook) {
                viewHolder.setCustomerName(orderbook.getCustomerName());
                viewHolder.setOrderNo(orderbook.getOrderNo());
                viewHolder.setOrderDate(orderbook.getOrderDateString());
                viewHolder.designerNameLayout.setVisibility(View.INVISIBLE);
                

                if (orderbook.isHandWork)
                {
                    viewHolder.setHandWorkOnImageView();
                }
                else
                {
                    viewHolder.setHandWorkOffImageView();
                }

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
        public  LinearLayout designerNameLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            cardNo=itemView.findViewById(R.id.cardNo);
            customerName = itemView.findViewById(R.id.customerNameOnCard);
            orderNo=itemView.findViewById(R.id.orderNoOnCard);
            orderDate=itemView.findViewById(R.id.orderDateOnCard);
            handWorkOnImageView=itemView.findViewById(R.id.handWorkOnImageView);
            handWorkOffImageView=itemView.findViewById(R.id.handWorkOffImageView);
            designerNameLayout=itemView.findViewById(R.id.designerNameLayout);


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
    }

}