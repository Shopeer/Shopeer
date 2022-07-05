package com.example.shopeer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<PeerRoomObject> peerList;

    public RecyclerAdapter(ArrayList<PeerRoomObject> peerList) {
        this.peerList = peerList;
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
        PeerRoomObject peerRoomObject = peerList.get(position);

        holder.peerName.setText(peerRoomObject.getPeerName());
        holder.peerProfilePic.setImageResource(peerRoomObject.getPeerProfilePic());
        holder.lastMessage.setText(peerRoomObject.getLastMessage());
        holder.timeofLM.setText(peerRoomObject.getTimeofLM());

    }

    @Override
    public int getItemCount() {
        return peerList.size();
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
