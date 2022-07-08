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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    RecyclerView recyclerView;
    ChatRecyclerAdapter chatRecyclerAdapter;
    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    ArrayList<ChatObject> messagesList;

//    String url = "http://localhost:8081/";
    String url = "http://20.230.148.126:8080/";



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
        roomId = extras.getString("room_id");
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
        try {
            fetchMessageHistory(roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Date date = new Date();
//        String time = simpleDateFormat.format(calendar.getTime());
//        for (int i=0; i < 20; i++) {
//            if (i%2==0) {
//                messagesList.add(new ChatObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
//                        "me", date.getTime(), time));
//            } else {
//                messagesList.add(new ChatObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
//                        "other", date.getTime(), time));
//            }
//        }

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
                    // send message to backend, FCM sends back
                    messagesList.add(newMessage);
                    chatRecyclerAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messagesList.size()-1);
                    // send the message object to BE
                    //postNewMessage(newMessage);


                    messageInput.setText(null);
                }
            }
        });
    } // end of oncreate

//    private void postNewMessage(ChatObject newMessage, String room_id) throws JSONException {
//        JSONArray param= new JSONArray();
//        param.put(new JSONObject().put("room_id", room_id));
//        JSONArrayRequest jsonArrayRequest = new JsonArrayRequest
//                (Request.Method.POST,
//                        url + "chat/message",
//                        param,)
//    }

    // fetch from BE
    private void fetchMessageHistory(String room_id) throws JSONException {

        JSONArray param= new JSONArray();
        param.put(new JSONObject().put("room_id", room_id));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET,
                        url + "chat/room/history", param,
                        new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<ChatObject> chatArr = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                // String mssg_id
                                Date date = new Date();
                                String email = obj.getString("email");
                                String text = obj.getString("text");
                                String time = obj.getString("time");
                                chatArr.add(new ChatObject(text, email, date.getTime(), time));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        messagesList = chatArr;
                        // notify change, scroll
                        chatRecyclerAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(messagesList.size()-1);

                        Log.d(TAG, "received message history");

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        return;

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
}