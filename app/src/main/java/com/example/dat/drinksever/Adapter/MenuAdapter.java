package com.example.dat.drinksever.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Adapter.ViewHolder.MenuViewHolder;
import com.example.dat.drinksever.DrinkListActivity;
import com.example.dat.drinksever.Interface.IItemClickListener;
import com.example.dat.drinksever.Model.Category;
import com.example.dat.drinksever.R;
import com.example.dat.drinksever.UpdateCategory;
import com.example.dat.drinksever.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    Context context;
    List<Category> categoryList;
    IItemClickListener iItemClickListener;
    public MenuAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_layout, viewGroup, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, final int position) {
        Picasso.with(context).load(categoryList.get(position).Link).into(menuViewHolder.img_product);
        menuViewHolder.txt_menu_name.setText(categoryList.get(position).Name);

        menuViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Common.category = categoryList.get(position);
                context.startActivity(new Intent(context,UpdateCategory.class ));
                return true;
            }
        });

        menuViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.category = categoryList.get(position);

                context.startActivity(new Intent(context, DrinkListActivity.class));
            }
        });

        menuViewHolder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, boolean isLongClick) {

                if (isLongClick) {


                } else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}
