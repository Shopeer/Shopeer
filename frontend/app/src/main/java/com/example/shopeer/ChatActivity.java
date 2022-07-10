package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    EditText messageInput;
    ImageView sendMessageButton;
    CardView sendMessageCardView;
    androidx.appcompat.widget.Toolbar chatToolbar;
    ImageView roomPictureImageView;
    TextView roomNameTextView;
    private String enteredMessage;
    Intent intent;
    String senderEmail;
    int roomId;
    String roomName;

    RecyclerView recyclerView;
    ChatRecyclerAdapter chatRecyclerAdapter;
    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    ArrayList<ChatObject> messagesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageInput = findViewById(R.id.messageInput);
        sendMessageCardView = findViewById(R.id.sendMessageCardView);
        sendMessageButton = findViewById(R.id.sendMessageIcon);
        chatToolbar = findViewById(R.id.chatToolbar);
        roomNameTextView = findViewById(R.id.chatRoomName);
        roomPictureImageView = findViewById(R.id.RoomPeerPicture);
        intent = getIntent();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        // set senderEmail
        Bundle extras = intent.getExtras();
        roomId = extras.getInt("room_id");
        roomName = extras.getString("room_name");

        // populate the room name and picture
        roomNameTextView.setText(roomName);
        int imgUri = extras.getInt("room_pic");
        if (imgUri==0) {
            Toast.makeText(getApplicationContext(), "null is received", Toast.LENGTH_SHORT).show();
        } else {
            // for room picture
            roomPictureImageView.setImageResource(R.drawable.temp_profile);
//            Picasso.get().load(imgUri).into(roomPictureImageView);
        }

        // fetch messages from BE
        messagesList = new ArrayList<>();
        Date date = new Date();
        String time = simpleDateFormat.format(calendar.getTime());
        for (int i=0; i < 20; i++) {
            if (i%2==0) {
                messagesList.add(new ChatObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
                        "me", date.getTime(), time));
            } else {
                messagesList.add(new ChatObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
                        "other", date.getTime(), time));
            }
        }

        // initialize recycler view
        recyclerView = findViewById(R.id.chat_recyclerView);
        chatRecyclerAdapter = new ChatRecyclerAdapter(messagesList);
        recyclerView.setAdapter(chatRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        // set up "send message" button
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredMessage = messageInput.getText().toString();
                if(!enteredMessage.isEmpty()) {
                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());
                    ChatObject newMessage = new ChatObject(enteredMessage, "me", date.getTime(), currenttime);
                    messagesList.add(newMessage);
                    chatRecyclerAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messagesList.size()-1);
                    // send the message object to BE


                    messageInput.setText(null);
                }
            }
        });
    } // end of oncreate

<<<<<<< HEAD
=======
    private void postNewMessage(ChatObject newMessage, String room_id) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = postUrl + room_id;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("FCM_token", MyFirebaseMessagingService.getToken(this));
            jsonBody.put("email", newMessage.getSenderEmail());
            jsonBody.put("text", newMessage.getText());
            jsonBody.put("time", newMessage.getCurrenttime());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    fetchMessageHistory(room_id);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "post message error" + error.toString());
                }
            })
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // fetch from BE
    private void fetchMessageHistory(String room_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = roomUrl + room_id;
        Log.d(TAG, url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            if (response.length() == 0) {return;}
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Date date = new Date();
                                String email = obj.getString("email");
                                String text = obj.getString("text");
                                String time = obj.getString("time");
                                messagesList.add(new ChatObject(text, email, date.getTime(), time));
                            }
                            // notify change, scroll
                            chatRecyclerAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(messagesList.size()-1);

                            Log.d(TAG, "received message history");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "get message history: " + error.toString());
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

>>>>>>> be9-integratingchat
    @Override
    protected void onStart() {
        super.onStart();
        chatRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatRecyclerAdapter.notifyDataSetChanged();
    }
}