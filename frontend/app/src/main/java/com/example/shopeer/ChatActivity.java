package com.example.shopeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    EditText messageInput;
    ImageView sendMessageButton;
    CardView sendMessageCardView;
    androidx.appcompat.widget.Toolbar chatToolbar;
    ImageView roomPictureImageView;
    TextView roomNameTextView;
    ImageView backButton;
    String roomId;
    String roomName;

    public RecyclerView recyclerView;
    public ChatRecyclerAdapter chatRecyclerAdapter;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    private WebSocket webSocket;
    private final String roomUrl = "http://20.230.148.126:8080/chat/room/history?room_id=";
    private final String postUrl = "http://20.230.148.126:8080/chat/message?room_id=";
//    private final String SERVER_PATH = "ws://20.230.148.126:8000";
    private final String SERVER_PATH = "ws://192.168.1.179:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initiateSocketConnection();
    }

    private void initiateSocketConnection() {
        Log.d(TAG, "initiateSocketConnection");
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(SERVER_PATH).build();
        webSocket = client.newWebSocket(request, new SocketListener());
    }

    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(() -> {
                Toast.makeText(ChatActivity.this, "Socket Connection Successful", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Socket Connection Successful");
                init();
            });
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(() -> {
                try{
                    JSONObject jsonObject = new JSONObject(text);
                    chatRecyclerAdapter.addItem(jsonObject);
                    recyclerView.smoothScrollToPosition(chatRecyclerAdapter.getItemCount()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void init() {
        messageInput = findViewById(R.id.messageInput);
        sendMessageCardView = findViewById(R.id.sendMessageCardView);
        sendMessageButton = findViewById(R.id.sendMessageIcon);
        backButton = findViewById(R.id.backButton);
        chatToolbar = findViewById(R.id.chatToolbar);
        roomNameTextView = findViewById(R.id.chatRoomName);
        roomPictureImageView = findViewById(R.id.RoomPeerPicture);
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        recyclerView = findViewById(R.id.chat_recyclerView);
        chatRecyclerAdapter = new ChatRecyclerAdapter(getLayoutInflater());
        recyclerView.setAdapter(chatRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setRoomInfo();
        setSendMessageButton();
        setBackButton();
    }


    private void setRoomInfo() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        roomId = extras.getString("room_id");
        roomName = extras.getString("room_name");
        roomNameTextView.setText(roomName);
        int imgUri = extras.getInt("room_pic");
        if (imgUri==0) {
            Toast.makeText(getApplicationContext(), "null is received", Toast.LENGTH_SHORT).show();
        } else {
            // for room picture
            roomPictureImageView.setImageResource(R.drawable.temp_profile);
//            Picasso.get().load(imgUri).into(roomPictureImageView);
        }
    }

    private void setSendMessageButton() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredMessage = messageInput.getText().toString();
                if(!enteredMessage.isEmpty()) {
                    String currenttime = simpleDateFormat.format(calendar.getTime());

                    Log.d(TAG, "sending message");
                    JSONObject jsonObject = new JSONObject();
                    try{
                        jsonObject.put("email", MainActivity.email);
                        jsonObject.put("text", enteredMessage);
                        jsonObject.put("time", currenttime);
                        jsonObject.put("room_id", roomId);

                        webSocket.send(jsonObject.toString());
                        chatRecyclerAdapter.addItem(jsonObject);
                        recyclerView.smoothScrollToPosition(chatRecyclerAdapter.getItemCount()-1);

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    messageInput.setText(null);
                }
            }
        });
    }

    private void setBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "going back to rooms");
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.putExtra("email", MainActivity.email);
                intent.putExtra("page", MainActivity.ROOM_ID);
                startActivity(intent);
            }
        });
    }
}