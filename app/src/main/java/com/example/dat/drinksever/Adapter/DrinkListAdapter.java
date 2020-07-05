package com.example.dat.drinksever.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Adapter.ViewHolder.DrinkListViewHolder;
import com.example.dat.drinksever.Interface.IItemClickListener;
import com.example.dat.drinksever.Model.Drink;
import com.example.dat.drinksever.R;
import com.example.dat.drinksever.UpdateProductActivity;
import com.example.dat.drinksever.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrinkListAdapter extends RecyclerView.Adapter<DrinkListViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkListAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.drink_item_layout, viewGroup, false);
        return new DrinkListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkListViewHolder drinkListViewHolder, final int position) {

        Picasso.with(context).load(drinkList.get(position).getLink()).into(drinkListViewHolder.img_product);
        drinkListViewHolder.txt_price.setText(new StringBuilder("$").append(drinkList.get(position).getPrice()));
        drinkListViewHolder.txt_drink_name.setText(drinkList.get(position).getName());

        drinkListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currenDrink = drinkList.get(position);
                context.startActivity(new Intent(context, UpdateProductActivity.class));
            }
        });
        drinkListViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, boolean isLongClick) {
                //implement late

            }
        });
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}

