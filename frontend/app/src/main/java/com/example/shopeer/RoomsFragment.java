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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private final String roomsUrl = "http://20.230.148.126:8080/chat/room/all?email=" + MainActivity.email;


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
        roomList = new ArrayList<>();
        // fetch data of peers and add to peerList
        fetchAllRooms();
        Log.d(TAG, roomList.toString());
        return v;
    }

    private void fetchAllRooms() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET,
                        roomsUrl, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try{
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject obj = response.getJSONObject(i);
                                        String name = obj.getString("name");
                                        String roomId = obj.getString("_id");

                                        JSONArray chatHist = obj.getJSONArray("chathistory");
                                        String lastMessage = "Say Hi!";
                                        String timeLM = "";
                                        if (chatHist.length() >0) {
                                            JSONObject lastMessageObj = chatHist.getJSONObject(chatHist.length() -1);
                                            lastMessage = lastMessageObj.getString("text");
                                            timeLM = lastMessageObj.getString("time");
                                        }
                                        roomList.add(new RoomObject(roomId, name, lastMessage, timeLM, R.drawable.temp_profile));
                                    }
                                    Log.d(TAG, "received rooms");
                                    // initialize recycler view
                                    recyclerView = getView().findViewById(R.id.recyclerView);
                                    RoomRecyclerAdapter recyclerAdapter = new RoomRecyclerAdapter(roomList, RoomsFragment.this);
                                    recyclerView.setAdapter(recyclerAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "get rooms: " + error.toString());
                    }
                });
        requestQueue.add(jsonArrayRequest);
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