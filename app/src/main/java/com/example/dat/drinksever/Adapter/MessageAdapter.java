package com.example.dat.drinksever.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.bl.BubbleLayout;
import com.example.dat.drinksever.Model.ChatMessage;
import com.example.dat.drinksever.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Viewholder>{

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    public static  final int MSG_TYPE_RIGHT_IMAGE = 2;
    private Context mContext;
    private String imageurl;
    FirebaseUser fuser;
    List<ChatMessage> chatMessages;

    public MessageAdapter(Context mContext,List<ChatMessage> chatMessageList){
        this.chatMessages = chatMessageList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = viewType == MSG_TYPE_LEFT ?
                LayoutInflater.from(mContext).inflate(R.layout.item_chat_left,parent,false) :
                LayoutInflater.from(mContext).inflate(R.layout.item_chat_right,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.Viewholder holder, int position) {


        if(chatMessages.get(position).isPicture()){
            holder.imImage.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
            holder.layout.setVisibility(View.GONE);
            Picasso.with(mContext).load(chatMessages.get(position).getPictureLink()).into(holder.imImage);
        }else {
            holder.imImage.setVisibility(View.GONE);
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(chatMessages.get(position).getContent());
        }

        holder.tvTime.setText(DateUtils.getRelativeTimeSpanString(chatMessages.get(position).getTimeStamp(),
                Calendar.getInstance().getTimeInMillis(),0).toString());


    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if( chatMessages.get(position).getSender().equals("3NP8PNkVqYOmQhqKUOyB9b30sJv2")){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }

    }

    public class Viewholder extends RecyclerView.ViewHolder{
        public TextView tvTime;
        public BubbleLayout layout;
        public TextView tvMessage;
        public ImageView imImage;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            layout = itemView.findViewById(R.id.layout);
            tvMessage = itemView.findViewById(R.id.tvChatMessage);
            imImage = itemView.findViewById(R.id.imImage);

        }
    }
}

