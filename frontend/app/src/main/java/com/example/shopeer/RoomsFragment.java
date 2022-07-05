package com.example.shopeer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Time;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment {
    final static String TAG = "RoomsFragment";
    RecyclerView recyclerView;
    private ArrayList<PeerRoomObject> peerList;


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

        // initialize recycler view
        peerList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);

        // fetch data of peers and add to peerList
        for (int i=0; i < 20; i++) {
            String name = "Peer Number " + i;
            peerList.add(new PeerRoomObject(name,
                    "Last Message sent by this peer",
                    "00:00",
                    R.drawable.temp_profile));
        }

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(peerList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }
}