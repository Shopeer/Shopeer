package com.example.shopeer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
    private ImageView cameraButton;
    private ImageView editButton;

    final private String profileUrl = "http://20.230.148.126:8080/user/profile?email=";

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try{
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        profilePic.setImageBitmap(bitmap);
                        String encodedImage = encodeImage(bitmap);
                        // send encoded image to backend as put
                        updateProfileInBackend(encodedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

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
        cameraButton = v.findViewById(R.id.camera_imageView);
        editButton = v.findViewById(R.id.edit_imageView);
    }

    private void getProfileInfo() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//            String url = profileUrl + GoogleSignIn.getLastSignedInAccount(getContext()).getEmail();
            String url = profileUrl + MainActivity.email;
            Log.d(TAG, "trying to get profile info " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    Log.d(TAG, "get profile " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            profileName.setText(jsonResponse.getString("name"));
                            profileBio.setText(jsonResponse.getString("description"));
                            Bitmap profilePhoto = decodeImage(jsonResponse.getString("photo"));
                            profilePic.setImageBitmap(profilePhoto);
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
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Checking permissions to access photos");
                // ask for permission
                try {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Editing profile pic");
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickImage.launch(intent);
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(getContext(), "Enable permissions to set photo", Toast.LENGTH_LONG).show();
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Editing profile info");
                Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted
                    Log.d(TAG, "Editing profile pic");
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImage.launch(intent);
                } else {
                    Toast.makeText(getContext(), "Enable permissions to set photo", Toast.LENGTH_LONG).show();
                }
            });

    private void updateProfileInBackend(String encodedImage) {
        try{
            String url = profileUrl + GoogleSignIn.getLastSignedInAccount(getContext()).getEmail();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("photo", encodedImage);
            final String requestBody = jsonObject.toString();

            Log.d(TAG, "onClick: " + url);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "profile photo updated");
                        Toast.makeText(getContext(), "Photo updated", Toast.LENGTH_SHORT).show();
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


    public String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public Bitmap decodeImage(String encodedImage) {
        try{
//            Log.d(TAG, "decodeImage: " + encodedImage);
            byte [] encodeByte = Base64.decode(encodedImage,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            Log.d(TAG, "decodeImage: " + bitmap);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}