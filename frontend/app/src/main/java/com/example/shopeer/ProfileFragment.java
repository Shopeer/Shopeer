package com.example.shopeer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    final static String TAG = "ProfileFragment"; // good practice for debugging
    private TextView profileName;
    private TextView profileBio;
    private ImageView profilePic;
//    private ImageView cameraButton;
    private ImageView editButton;

    final private String profileUrl = "http://20.230.148.126:8080/user/profile?email=";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        getProfileInfo();
        setUpdateProfile();
        return v;
    }

    // Helper functions
    private void init(View v) {
        profileName = v.findViewById(R.id.profileName_textView);
        profileBio = v.findViewById(R.id.profileBio_textView);
        profilePic = v.findViewById(R.id.profilePic_imageView);
//        cameraButton = v.findViewById(R.id.camera_imageView);
        editButton = v.findViewById(R.id.edit_imageView);
    }

    private void getProfileInfo() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            String url = profileUrl + MainActivity.email;
            Log.d(TAG, "trying to get profile info " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "get profile " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            profileName.setText(jsonResponse.getString("name"));
                            profileBio.setText(jsonResponse.getString("description"));
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

    private void setUpdateProfile() {
        //initialize buttons
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Editing profile pic");
//            }
//        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Editing profile info");
                Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}