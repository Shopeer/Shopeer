package com.example.shopeer.rooms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopeer.R;

import java.util.ArrayList;

public class RoomRecyclerAdapter extends RecyclerView.Adapter<RoomRecyclerAdapter.ViewHolder> {
    private ArrayList<RoomObject> roomList;
    private OnRoomListener mOnRoomListener;

    public RoomRecyclerAdapter(ArrayList<RoomObject> peerList, OnRoomListener onRoomListener) {
        this.roomList = peerList;
        this.mOnRoomListener = onRoomListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.room_view_layout, parent, false);
        return new ViewHolder(v, mOnRoomListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomObject roomObject = roomList.get(position);

//        holder.roomId = roomObject.getRoomId();
        holder.roomName.setText(roomObject.getRoomName());
        holder.roomProfilePic.setImageBitmap(roomObject.getRoomProfilePic());
        holder.lastMessage.setText(roomObject.getLastMessage());
        holder.timeofLM.setText(roomObject.getTimeofLM());

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        int roomId;
        ImageView roomProfilePic;
        TextView roomName;
        TextView lastMessage;
        TextView timeofLM;
        OnRoomListener onRoomListener;

        public ViewHolder(@NonNull View itemView, OnRoomListener onRoomListener) {
            super(itemView);
            this.onRoomListener = onRoomListener;
            roomName = itemView.findViewById(R.id.peerName);
            roomProfilePic = itemView.findViewById(R.id.peerImage);
            lastMessage = itemView.findViewById(R.id.peerLastMessage);
            timeofLM = itemView.findViewById(R.id.peerLastMessageTime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRoomListener.onRoomClick(getAdapterPosition());
        }
    }

    public interface OnRoomListener{
        void onRoomClick(int position);
    }
}
>>>>>>> main:frontend/app/src/main/java/com/example/shopeer/RoomRecyclerAdapter.java
