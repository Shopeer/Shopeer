package com.example.shopeer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment implements RoomRecyclerAdapter.OnRoomListener{
    final static String TAG = "RoomsFragment";
    RecyclerView recyclerView;
    private ArrayList<RoomObject> roomList;


    public RoomsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PeersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomsFragment newInstance() {
        RoomsFragment fragment = new RoomsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_rooms, container, false);

        // fetch data of peers and add to peerList
        roomList = new ArrayList<>();
        for (int i=0; i < 20; i++) {
            String name = "Peer Number " + i;
            roomList.add(new RoomObject(i, name,
                    "Last Message sent by this peer",
                    "00:00",
                    R.drawable.temp_profile));
        }

        // initialize recycler view
        recyclerView = v.findViewById(R.id.recyclerView);
        RoomRecyclerAdapter recyclerAdapter = new RoomRecyclerAdapter(roomList, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onRoomClick(int position) {
        Log.d(TAG, "onRoomClick: clicked.");

        // Redirect to new chat activity
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("room_id", roomList.get(position).getRoomId());
        intent.putExtra("room_name", roomList.get(position).getRoomName());
        intent.putExtra("room_pic", roomList.get(position).getRoomProfilePic());
        startActivity(intent);
    }
}