package com.example.shopeer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatRecyclerAdapter  extends RecyclerView.Adapter {
    private static final int ITEM_SEND = 1;
    private static final int ITEM_RECEIVE = 2;

    private LayoutInflater inflater;
    private ArrayList<JSONObject> messagesList = new ArrayList<>();

    public ChatRecyclerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType==ITEM_SEND) {
            v = inflater.inflate(R.layout.senderchat_layout, parent, false);
            return new SenderViewHolder(v);
        } else {
            v = inflater.inflate(R.layout.receivedchat_layout, parent, false);
            return new ReceivedViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject message = messagesList.get(position);
        try {
            if(holder.getClass() == SenderViewHolder.class) {
                SenderViewHolder viewHolder = (SenderViewHolder) holder;
                viewHolder.textViewMessage.setText(message.getString("text"));
                viewHolder.messageTime.setText(message.getString("time"));
            } else {
                ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
                viewHolder.senderName.setText(message.getString("email"));
                viewHolder.textViewMessage.setText(message.getString("text"));
                viewHolder.messageTime.setText(message.getString("time"));
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        // if the message was sent by current user-> return ITEM_SEND, else return ITEM_RECEIVE
        JSONObject message = messagesList.get(position);
        try {
            if (MainActivity.email.equals(message.getString("email"))) {
                return ITEM_SEND;
            } else {
                return ITEM_RECEIVE;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void addItem (JSONObject jsonObject) {
        messagesList.add(jsonObject);
        notifyDataSetChanged();
    }

    // Helper classes
    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView messageTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.senderMessage);
            messageTime = itemView.findViewById(R.id.messageTimeS);
        }
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView senderName;
        TextView textViewMessage;
        TextView messageTime;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.receivedName);
            textViewMessage = itemView.findViewById(R.id.receivedMessage);
            messageTime = itemView.findViewById(R.id.messageTimeR);
        }
    }
}
