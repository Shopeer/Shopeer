package com.example.shopeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise the bottom nav bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }



}
