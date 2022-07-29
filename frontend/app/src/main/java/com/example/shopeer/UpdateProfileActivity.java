package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    private EditText nameInput;
    private EditText bioInput;
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
                try{
                    String url = profileUrl + GoogleSignIn.getLastSignedInAccount(UpdateProfileActivity.this).getEmail();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", nameInput.getText().toString());
                    jsonObject.put("description", bioInput.getText().toString());
                    final String requestBody = jsonObject.toString();

                    Log.d(TAG, "onClick: " + url);
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(UpdateProfileActivity.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "update profile " + response);
                                Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                                intent.putExtra("email", MainActivity.email);
                                intent.putExtra("page", MainActivity.PROFILE_ID);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse login: " + error.toString());
                            }
                        }) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
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