package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class updateProfileActivity extends AppCompatActivity {
    private static final String TAG = "updateProfileActivity";
    private EditText nameInput, bioInput;
    private ImageView profilePic;
    private Button updateButton;

    final private String profileUrl = "http://20.230.148.126:8080/user/profile?email=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        init();
        getProfileInfo();
        setUpdateButton();
    }

    private void init() {
        nameInput = findViewById(R.id.profileName_input);
        bioInput = findViewById(R.id.profileBio_input);
        updateButton = findViewById(R.id.updateProfileButton);
        profilePic = findViewById(R.id.profilePic_imageView);
    }

    private void setUpdateButton() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredName = nameInput.getText().toString();
                String enteredBio = bioInput.getText().toString();
                String url = profileUrl + MainActivity.email +
                        "&name=" + enteredName +
                        "&description=" + enteredBio;
                Log.d(TAG, "onClick: " + url);
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(updateProfileActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "update profile " + response);
                            Intent intent = new Intent(updateProfileActivity.this, MainActivity.class);
                            intent.putExtra("email", MainActivity.email);
                            intent.putExtra("register", "yes");
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse login: " + error.toString());
                        }
                    });
                    requestQueue.add(stringRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getProfileInfo() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = profileUrl + MainActivity.email;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "get profile " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        nameInput.setText(jsonResponse.getString("name"));
                        bioInput.setText(jsonResponse.getString("description"));
                        profilePic.setImageResource(R.drawable.temp_profile);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse login: " + error.toString());
                }
            });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}