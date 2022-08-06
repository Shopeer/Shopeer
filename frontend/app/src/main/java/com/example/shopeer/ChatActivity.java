package com.example.shopeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    ArrayList<JSONObject> messagesList = new ArrayList<>();

    public RecyclerView recyclerView;
    public ChatRecyclerAdapter chatRecyclerAdapter;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    private WebSocket webSocket;
    private final String roomUrl = "http://20.230.148.126:8080/chat/room/history?room_id=";
//    private final String postUrl = "http://20.230.148.126:8080/chat/message?room_id=";
    private final String SERVER_PATH = "ws://20.230.148.126:8000";
//    private final String SERVER_PATH = "ws://192.168.1.179:8000";

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
//                Log.d(TAG, MainActivity.email + "  " + GoogleSignIn.getLastSignedInAccount(ChatActivity.this).getEmail());
                init();
            });
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(() -> {
                try{
                    JSONObject jsonObject = new JSONObject(text);
                    chatRecyclerAdapter.addItem(jsonObject, roomId);
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
        chatRecyclerAdapter = new ChatRecyclerAdapter(getLayoutInflater(), messagesList);
        recyclerView.setAdapter(chatRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setRoomInfo();
        setSendMessageButton();
        setBackButton();
        fetchMessageHistory(roomId);
        Log.d(TAG, "init done");
    }


    private void setRoomInfo() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        roomId = extras.getString("room_id");
        roomName = extras.getString("room_name");
        roomNameTextView.setText(roomName);
        String encodedImage = extras.getString("room_pic");
//        Log.d(TAG, "setRoomInfo: " + encodedImage);
        if (encodedImage == "") {
            Toast.makeText(getApplicationContext(), "Peer has no profile picture", Toast.LENGTH_SHORT).show();
        } else {
            // for room picture
            Bitmap image = ProfileFragment.newInstance().decodeImage(encodedImage);
            roomPictureImageView.setImageBitmap(image);
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
                        jsonObject.put("email", GoogleSignIn.getLastSignedInAccount(ChatActivity.this).getEmail());
                        jsonObject.put("text", enteredMessage);
                        jsonObject.put("time", currenttime);
                        jsonObject.put("room_id", roomId);

                        Log.d(TAG, "sending: " + jsonObject);
                        webSocket.send(jsonObject.toString());
                        chatRecyclerAdapter.addItem(jsonObject, roomId);
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
                                        obj.put("room_id", roomId);
                                        messagesList.add(obj);
                                    }
                                    // notify change, scroll
                                    chatRecyclerAdapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(chatRecyclerAdapter.getItemCount()-1);

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

}