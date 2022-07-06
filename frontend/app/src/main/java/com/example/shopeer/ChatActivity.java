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
                if(enteredMessage.isEmpty()) {
                 finish();
                }
                Date date = new Date();
                currenttime = simpleDateFormat.format(calendar.getTime());
                ChatObject message = new ChatObject(enteredMessage, "senderEmail", date.getTime(), currenttime);
                // send the message object to BE

                messageInput.setText(null);
            }
        });
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