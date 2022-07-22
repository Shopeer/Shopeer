package com.example.shopeer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRecyclerAdapter  extends RecyclerView.Adapter {
    private static final String TAG = "ChatRecyclerAdapter";

    private ArrayList<ChatObject> messagesList;

    private int ITEM_SEND = 1;
    private int ITEM_RECEIVE = 2;

    TextView textViewMessage;
    TextView messageTime;

    public ChatRecyclerAdapter(ArrayList<ChatObject> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType==ITEM_SEND) {
            View v = inflater.inflate(R.layout.senderchat_layout, parent, false);
            return new SenderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.receivedchat_layout, parent, false);
            return new ReceivedViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatObject message = messagesList.get(position);
        if(holder.getClass() == SenderViewHolder.class) {
            textViewMessage.setText(message.getText());
            messageTime.setText(message.getCurrenttime());
        } else {
            textViewMessage.setText(message.getText());
            messageTime.setText(message.getCurrenttime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        // if the message was sent by current user-> return ITEM_SEND, else return ITEM_RECEIVE
        ChatObject message = messagesList.get(position);
        if (MainActivity.email.equals(message.getSenderEmail())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


    // Helper class
    public class SenderViewHolder extends RecyclerView.ViewHolder {


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.senderMessage);
            messageTime = itemView.findViewById(R.id.messageTimeS);
        }
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder {

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.receivedMessage);
            messageTime = itemView.findViewById(R.id.messageTimeR);
        }
    }
}
