package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static String ROOM_ID = "1";
    public static String PROFILE_ID = "2";
    public static String email;

    public static NavController navController;

    public HashSet<SearchObject> searches = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the bottom nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        navController = Navigation.findNavController(this, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // get the user email
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
//        Log.d(TAG, email);

        String isRegister = intent.getStringExtra("page");
        if (PROFILE_ID.equals(isRegister))
            navController.navigate(R.id.profileFragment);
        else if (ROOM_ID.equals(isRegister))
            navController.navigate(R.id.roomsFragment);
    }



}
