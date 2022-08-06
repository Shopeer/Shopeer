package com.example.shopeer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<RoomObject> roomList;

    public RecyclerAdapter(ArrayList<RoomObject> peerList) {
        this.roomList = peerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.room_view_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomObject roomObject = roomList.get(position);

        holder.peerName.setText(roomObject.getRoomName());
        holder.peerProfilePic.setImageBitmap(roomObject.getRoomProfilePic());
        holder.lastMessage.setText(roomObject.getLastMessage());
        holder.timeofLM.setText(roomObject.getTimeofLM());

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView peerProfilePic;
        TextView peerName;
        TextView lastMessage;
        TextView timeofLM;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            peerName = itemView.findViewById(R.id.peerName);
            peerProfilePic = itemView.findViewById(R.id.peerImage);
            lastMessage = itemView.findViewById(R.id.peerLastMessage);
            timeofLM = itemView.findViewById(R.id.peerLastMessageTime);
        }
    }
}
