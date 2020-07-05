package com.example.dat.drinksever.Adapter.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Interface.IItemClickListener;
import com.example.dat.drinksever.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

   public ImageView img_product;
    public TextView txt_menu_name;

     IItemClickListener iItemClickListener;

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        img_product = itemView.findViewById(R.id.img_product);
        txt_menu_name = itemView.findViewById(R.id.txt_menu_name);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {

         iItemClickListener.onClick(view, false);
    }

    @Override
    public boolean onLongClick(View view) {
         iItemClickListener.onClick(view, true);
        return false;
    }
}