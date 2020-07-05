package com.example.dat.drinksever.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Model.Order;
import com.example.dat.drinksever.R;
import com.example.dat.drinksever.Utils.Common;
import com.example.dat.drinksever.ViewOrderDetailActivity;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    List<Order> orderList;



    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_layout, viewGroup, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position) {

        viewHolder.txt_order_id.setText(new StringBuilder("#").append(orderList.get(position).getOrderId()));
        viewHolder.txt_order_price.setText(new StringBuilder("$").append(orderList.get(position).getOrderPrice()));
        viewHolder.txt_order_address.setText(orderList.get(position).getOrderAddress());
        viewHolder.txt_order_status.setText(new StringBuilder("Order Status: ")
                .append(Common.convertCodeToStatus(orderList.get(position).getOrderStatus())));
        viewHolder.txt_order_coment.setText(new StringBuilder("Comment: ").append(orderList.get(position).getOrderComment()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentOrder = orderList.get(position);
                context.startActivity(new Intent(context, ViewOrderDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_order_id, txt_order_price, txt_order_address,
                txt_order_coment, txt_order_status,txt_oder_phone;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_price = itemView.findViewById(R.id.txt_order_price);
            txt_order_address = itemView.findViewById(R.id.txt_order_address);
            txt_order_coment = itemView.findViewById(R.id.txt_order_comment);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);

        }
    }
}


