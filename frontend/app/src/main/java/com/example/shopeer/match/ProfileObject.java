package com.example.shopeer.match;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class ProfileObject {
    private static final String TAG = "Profile Obj";

    private String email;
    private String name;
    private String description;
    private String photo;

    public ProfileObject(String email, String name, String description, String photo) {
        this.email = email;
        this.name = name;
        this.description = description;
        this.photo = photo;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Bitmap getPhotoBitmap() {
        if (photo != null) {
            return decodeImage(photo);
        }
        return null;
    }

    private Bitmap decodeImage(String encodedImage) {
        try{
            Log.d(TAG, "decodeImage: " + encodedImage);
            byte [] encodeByte = Base64.decode(encodedImage,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.d(TAG, "decodeImage: " + bitmap);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
