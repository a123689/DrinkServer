package com.example.dat.drinksever.Adapter.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.Interface.IItemClickListener;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView shipper_name,shipper_phone;
    public Button btn_edit, btn_remove;
    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    private IItemClickListener itemClickListener;

    @Override
    public void onClick(View v) {

    }
}
