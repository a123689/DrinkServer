package com.example.dat.drinksever.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Model.Cart;
import com.example.dat.drinksever.R;
import com.example.dat.drinksever.Utils.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    Context context;
    List<Cart> itemList;

    public OrderDetailAdapter(Context context) {
        this.context = context;
        this.itemList = new Gson().fromJson(Common.currentOrder.getOrderDetail(),
                new TypeToken<List<Cart>>() {
                }.getType());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.txt_drink_amountt.setText(String.valueOf(itemList.get(i).getAmount()));
        holder.txt_drink_name.setText(new StringBuilder(itemList.get(i).getName()));
        holder.txt_size.setText(itemList.get(i).getSize() == 0 ? "Size M" : "Size L");
        holder.txt_sugar_ice.setText(
                new StringBuilder("Sugar: ").append(itemList.get(i).getSugar())
                        .append(", Ice: ").append(itemList.get(i).getIce())
        );

        try {

            if (itemList.get(i).getToppingExtras() != null && !itemList.get(i).getToppingExtras().isEmpty()) {
                String topping_format = itemList.get(i).getToppingExtras().replaceAll("\\n", ",");
                topping_format = topping_format.substring(0, topping_format.length() - 1);
                holder.txt_topping.setText(topping_format);
            } else {
                holder.txt_topping.setText("None");
            }
        } catch (Exception e) {

        }

        Picasso.with(context).load(itemList.get(i).getLink()).into(holder.img_order_item);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_order_item;
        TextView txt_drink_name, txt_drink_amountt, txt_sugar_ice, txt_size, txt_topping;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_order_item = itemView.findViewById(R.id.img_order_item);
            txt_drink_name = itemView.findViewById(R.id.txt_drink_name);
            txt_drink_amountt = itemView.findViewById(R.id.txt_drink_amount);
            txt_sugar_ice = itemView.findViewById(R.id.txt_sugar_ice);
            txt_size = itemView.findViewById(R.id.txt_size);
            txt_topping = itemView.findViewById(R.id.txt_topping);

        }
    }
}

