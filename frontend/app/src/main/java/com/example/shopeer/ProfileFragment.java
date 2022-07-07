package com.example.shopeer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    final static String TAG = "ProfileFragment"; // good practice for debugging
    private TextView profileName, profileBio;
    private CardView profilePicCard;
    private ImageView profilePic, cameraButton, editButton;
    private String email, password;
    private Uri imagepath;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get user info as token when calling backend API

//        profileName = getView().findViewById(R.id.profileName_textView);
//        profileBio = getView().findViewById(R.id.profileBio_textView);
//        profilePic = getView().findViewById(R.id.profilePic_imageView);
//        profilePicCard = getView().findViewById(R.id.cardView);
//        cameraButton = getView().findViewById(R.id.camera_imageView);
//        editButton = getView().findViewById(R.id.edit_imageView);
//
        // make database instance
        // access the user info from database
        // use addValueEventListener, ref: https://www.youtube.com/watch?v=GuMwCuvGWx4

        //initialize buttons
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Editing profile pic");
//            }
//        });
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Editing profile info");
//            }
//        });
//
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}