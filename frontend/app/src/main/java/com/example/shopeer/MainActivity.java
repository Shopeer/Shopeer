package com.example.shopeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the bottom nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // get the user email
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        Log.d(TAG, email);

        String isRegister = intent.getStringExtra("register");
        if (isRegister != null) {
            // redirect to profile page
            navController.navigate(R.id.profileFragment);
        }
    }
}