package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    String roomId;
    String roomName;

    public RecyclerView recyclerView;
    public ChatRecyclerAdapter chatRecyclerAdapter;
    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    ArrayList<ChatObject> messagesList;
    private final String roomUrl = "http://20.230.148.126:8080/chat/room/history?room_id=";
    private final String postUrl = "http://20.230.148.126:8080/chat/message?room_id=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        setRoomInfo();
        setMessagesView();
        // fetch messages from BE when there are any changes in BE
        fetchMessageHistory(roomId);
        setSendMessageButton();

    }

    private void init() {
        messageInput = findViewById(R.id.messageInput);
        sendMessageCardView = findViewById(R.id.sendMessageCardView);
        sendMessageButton = findViewById(R.id.sendMessageIcon);
        chatToolbar = findViewById(R.id.chatToolbar);
        roomNameTextView = findViewById(R.id.chatRoomName);
        roomPictureImageView = findViewById(R.id.RoomPeerPicture);
        intent = getIntent();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
    }

    private void setRoomInfo() {
        roomNameTextView.setText(roomName);
        // set senderEmail
        Bundle extras = intent.getExtras();
        roomId = extras.getString("room_id");
        roomName = extras.getString("room_name");
        int imgUri = extras.getInt("room_pic");
        if (imgUri==0) {
            Toast.makeText(getApplicationContext(), "null is received", Toast.LENGTH_SHORT).show();
        } else {
            // for room picture
            roomPictureImageView.setImageResource(R.drawable.temp_profile);
//            Picasso.get().load(imgUri).into(roomPictureImageView);
        }
    }

    private void setMessagesView() {
        messagesList = new ArrayList<>();
        // initialize recycler view
        recyclerView = findViewById(R.id.chat_recyclerView);
        chatRecyclerAdapter = new ChatRecyclerAdapter(messagesList);
        recyclerView.setAdapter(chatRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setSendMessageButton() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredMessage = messageInput.getText().toString();
                if(!enteredMessage.isEmpty()) {
                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());
                    ChatObject newMessage = new ChatObject(enteredMessage, MainActivity.email, date.getTime(), currenttime);
                    Log.d(TAG, "sending message");
                    // send the message object to BE
                    postNewMessage(newMessage, roomId);
                    messageInput.setText(null);
                }
            }
        });
    }

    private void postNewMessage(ChatObject newMessage, String room_id) {
        try {
            Log.d(TAG, "postNewMessage");
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = postUrl + room_id;
            JSONObject jsonBody = new JSONObject();
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
                            messagesList.clear();
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

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter(TAG));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

           // refresh chatview
            chatRecyclerAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messagesList.size()-1);

        }
    };
}