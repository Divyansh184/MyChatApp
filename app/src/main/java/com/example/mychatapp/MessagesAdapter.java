package com.example.mychatapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter  extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIEVE=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return new RecieverViewHolder(view) {
            };
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        Messages messages=messagesArrayList.get(position);
        if(holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder =(SenderViewHolder)holder;
            viewHolder.textviewmwessage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());

        }else {
            RecieverViewHolder viewHolder =(RecieverViewHolder)holder;
            viewHolder.textviewmwessage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSendid())){

            return  ITEM_SEND;
        }else {
            return ITEM_RECIEVE;
        }
    }

    class  SenderViewHolder extends RecyclerView.ViewHolder{


        TextView textviewmwessage;
        TextView timeofmessage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            textviewmwessage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);

        }
    }

    class  RecieverViewHolder extends RecyclerView.ViewHolder{


        TextView textviewmwessage;
        TextView timeofmessage;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);

            textviewmwessage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);

        }
    }
}
