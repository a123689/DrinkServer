package com.example.dat.drinksever.Adapter.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Interface.IItemClickListener;
import com.example.dat.drinksever.R;

public class DrinkListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView img_product;
    public TextView txt_drink_name, txt_price;

    IItemClickListener itemClickListener;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DrinkListViewHolder(@NonNull View itemView) {
        super(itemView);
        img_product = itemView.findViewById(R.id.img_product);
        txt_drink_name = itemView.findViewById(R.id.txt_drink_name);
        txt_price = itemView.findViewById(R.id.txt_price);
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, false);
    }


}

