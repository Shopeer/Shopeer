package com.example.shopeer;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.android.volley.toolbox.StringRequest;
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
    RoomRecyclerAdapter recyclerAdapter;
    private ArrayList<RoomObject> roomList;

    private View view;

    private final String roomsUrl = "http://20.230.148.126:8080/chat/room/all?email=" + MainActivity.email;
    final private String profileUrl = "http://20.230.148.126:8080/user/profile";
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
        initView(v);
        // fetch data of peers and add to peerList
        fetchAllRooms();
        Log.d(TAG, roomList.toString());

        view = v;
        return v;
    }

    private void initView(View v) {
        // initialize recycler view
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerAdapter = new RoomRecyclerAdapter(roomList, RoomsFragment.this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                                        String roomId = obj.getString("_id");
                                        String roomName = obj.getString("name");

                                        JSONArray chatHist = obj.getJSONArray("chathistory");
                                        String lastMessage = "Say Hi!";
                                        String timeLM = "";
                                        if (chatHist.length() >0) {
                                            JSONObject lastMessageObj = chatHist.getJSONObject(chatHist.length() -1);
                                            lastMessage = lastMessageObj.getString("text");
                                            timeLM = lastMessageObj.getString("time");
                                        }
                                        RoomObject roomObject = new RoomObject(roomId, roomName, lastMessage, timeLM, null);

                                        // get email of other person
                                        JSONArray members = obj.getJSONArray("peerslist");
                                        for(int j=0; j<members.length(); j++) {
                                            String email =  members.getString(j);
                                            if (!MainActivity.email.equals(email)) {
                                                Log.d(TAG, "other email: " + email);
                                                fillRoomInfo(email, roomObject);
                                            }
                                        }

                                    }
                                    Log.d(TAG, "received rooms" + roomList);

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

    private void fillRoomInfo(String email, RoomObject roomObject) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String url = profileUrl + "?email=" + email;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse fill room info: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String name = jsonResponse.getString("name");
                        roomObject.setRoomName(name);
                        Bitmap image = ProfileFragment.newInstance().decodeImage(jsonResponse.getString("photo"));
                        roomObject.setRoomProfilePic(image);
                        roomList.add(roomObject);
                        recyclerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse fill rooms: " + error.toString());
                }
            });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomClick(int position) {
        Log.d(TAG, "onRoomClick: clicked.");

        // Redirect to new chat activity
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("room_id", roomList.get(position).getRoomId());
        intent.putExtra("room_name", roomList.get(position).getRoomName());
        String encodedImage = ProfileFragment.newInstance().encodeImage(roomList.get(position).getRoomProfilePic());
        intent.putExtra("room_pic", encodedImage);
        startActivity(intent);
    }
}