package com.example.dat.drinksever.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dat.drinksever.ChatActivity;
import com.example.dat.drinksever.Interface.IItemClickListener;
import com.example.dat.drinksever.Model.ChatList;
import com.example.dat.drinksever.Model.ChatMessage;
import com.example.dat.drinksever.Model.User;
import com.example.dat.drinksever.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterListChat extends RecyclerView.Adapter<AdapterListChat.ViewHolder>{

    Context context;
    List<User> chatLists;
    String theLastMessage;
    public AdapterListChat(Context context,List<User> chatLists){
        this.context = context;
        this.chatLists = chatLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvUsername.setText(chatLists.get(position).getUsername());
        lastMessage(chatLists.get(position).getId(), holder.tvLastmessage);
        holder.setClick(new IItemClickListener() {
            @Override
            public void onClick(View view, boolean isLongClick) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user",chatLists.get(position).getId());
                intent.putExtra("name",chatLists.get(position).getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUsername,tvLastmessage;
        IItemClickListener iItemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.username);
            tvLastmessage = itemView.findViewById(R.id.last_msg);
            itemView.setOnClickListener(this);
        }
        public void setClick(IItemClickListener iItemClickListener){
            this.iItemClickListener = iItemClickListener;
        }

        @Override
        public void onClick(View v) {
            iItemClickListener.onClick(v,false);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatMessage").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    if(chatMessage != null){
                        if (chatMessage.getSender().equals(userid)) {
                            theLastMessage = chatMessage.getContent();
                        }

                    }

                }

                last_msg.setText(theLastMessage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
