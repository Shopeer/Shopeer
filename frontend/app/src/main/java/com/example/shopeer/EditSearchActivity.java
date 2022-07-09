package com.example.shopeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EditSearchActivity extends AppCompatActivity {
    private static final String TAG = "editSearchActivity";
    private static final String searchUrl = "http://20.230.148.126:8080/match/searches?email=";
    int SERVER_TIMEOUT_MS = 1000; // num ms wait for server

    private boolean isNewSearch;

    private EditText searchName;
    private TextView searchLocation;
    private Button changeLocationButton;
    private EditText distanceNumber;
    private RadioButton activityGroceriesButton;
    private RadioButton activityEntertainmentButton;
    private  RadioButton activityBulkBuyButton;
    private EditText budgetNumber;
    private Button deleteButton;
    private Button saveButton;

    private String oldSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_search);

        Log.d(TAG, "starting editSearchActivity");

        init();

        //TODO: request to update/create the new search in backend

        setDeleteButton();
        setSaveButton();

    }

    private void init() {
        // set up resources
        searchName = findViewById(R.id.search_name_text);
        searchLocation = findViewById(R.id.search_location_text);
        changeLocationButton = findViewById(R.id.change_location_button);
        distanceNumber = findViewById(R.id.distance_number);
        budgetNumber = findViewById(R.id.budget_number);

        activityGroceriesButton = findViewById(R.id.activity_groceries_radioButton);
        activityEntertainmentButton = findViewById(R.id.activity_entertainment_radioButton);
        activityBulkBuyButton = findViewById(R.id.activity_bulkBuy_radioButton);

        deleteButton = findViewById(R.id.delete_search_button);
        saveButton = findViewById(R.id.save_search_button);

        // check if creating a new search or not
        Intent intent = getIntent();
        this.isNewSearch = intent.getBooleanExtra("isNewSearch", true);

        // is an existing search open for editing
        if (!isNewSearch) {
            //TODO: set inputs as existing values from the intent data
            oldSearchName = intent.getStringExtra("searchName");
            searchName.setText(oldSearchName);

            searchLocation.setText(intent.getStringExtra("locationName"));
            distanceNumber.setText(Integer.toString(intent.getIntExtra("range", 0)));
            budgetNumber.setText(Integer.toString(intent.getIntExtra("budget", 0)));

            // show the activities
            ArrayList<String> acts = intent.getStringArrayListExtra("activities");
            for (String a : acts) {
                if (a.equals("entertainment")) {
                    activityEntertainmentButton.setChecked(true);
                }
                else if (a.equals("bulk buy")) {
                    activityBulkBuyButton.setChecked(true);
                }
                else if (a.equals("groceries")) {
                    activityGroceriesButton.setChecked(true);
                }
            }

            Log.d(TAG, "opened " + oldSearchName + " for edit");
        }
        else {
            Log.d(TAG, "creating a new search");
        }

    }

    private void setDeleteButton() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete search from backend only if it is an existing search
                if (isNewSearch) {
                    Log.d(TAG, "deleting a new search, nothing was saved");
                    Intent intent = new Intent(EditSearchActivity.this, MainActivity.class);
                    intent.putExtra("email", MainActivity.email);
                    startActivity(intent);
                }
                else {
                    Log.d(TAG, "deleting a existing search " + oldSearchName);
                    String url = searchUrl + MainActivity.email;
                    Log.d(TAG, "onClick: " + url);
                    try {
                        //TODO: make sure this works
                        RequestQueue requestQueue = Volley.newRequestQueue(EditSearchActivity.this);

                        // deleting a search needs the search name to be passed via body
                        JSONObject search_name = new JSONObject();
                        search_name.put("search_name", oldSearchName);
                        JSONObject search = new JSONObject();
                        search.put("search", search_name);
                        Log.d(TAG, "delete_search request body: " + search);

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, url, search, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "delete_search response: " + response);
                                Intent intent = new Intent(EditSearchActivity.this, MainActivity.class);
                                intent.putExtra("email", MainActivity.email);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse delete_search: " + error.toString());
                                Toast.makeText(EditSearchActivity.this, "error: could not delete", Toast.LENGTH_LONG).show();
                            }
                        });
                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(jsonObjReq);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void setSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get inputs and do error checking
                boolean canSave = true;

                String nameInput = searchName.getText().toString();

                //TODO: update this location stuff
                String locationInput = searchLocation.getText().toString();
                double latInput = 49.3847923562497;
                double lonInput = 49.3542784803249;

                int rangeInput = Integer.parseInt(distanceNumber.getText().toString());

                ArrayList<String> activitiesInput = getActivitySelection();
                if (activitiesInput.isEmpty()) {
                    Toast.makeText(EditSearchActivity.this, "choose at least one activity", Toast.LENGTH_LONG).show();
                    canSave = false;
                }

                int budgetInput = Integer.parseInt(budgetNumber.getText().toString());

                // send request
                if (!canSave) {
                    Log.d(TAG, "bad input, cannot save yet");
                }
                else {
                    String url = searchUrl + MainActivity.email + "&search=";
                    if (!isNewSearch) {
                        url += oldSearchName;
                    }
                    Log.d(TAG, "onClick: " + url);
                    try {
                        //TODO: change to be for POST to search
                        RequestQueue requestQueue = Volley.newRequestQueue(EditSearchActivity.this);

                        // create json
                        JSONObject search = new JSONObject();
                        search.put("search_name", nameInput);

                        JSONArray activity = new JSONArray(activitiesInput);
                        search.put("activity",activity);

                        JSONArray location = new JSONArray();
                        location.put(latInput);
                        location.put(lonInput);

                        search.put("location", location);

                        search.put("max_range", rangeInput);

                        search.put("max_budget", budgetInput);

                        JSONObject body = new JSONObject();
                        body.put("search", search);
                        Log.d(TAG, "post_search request body: " + search);

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "post_search response: " + response);
                                Toast.makeText(EditSearchActivity.this, "saved " + nameInput + " search", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditSearchActivity.this, MainActivity.class);
                                intent.putExtra("email", MainActivity.email);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse post_search: " + error.toString());
                                Toast.makeText(EditSearchActivity.this, "error: could not save search", Toast.LENGTH_LONG).show();
                            }
                        });

                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(jsonObjReq);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });



    }

    /**
     * checks which activities were selected (no ranking yet) adn returns String array of them
     */
    private ArrayList<String> getActivitySelection() {
        ArrayList<String> activities = new ArrayList<String>();

        if (activityGroceriesButton.isChecked()) {
            activities.add("groceries");
        }
        if (activityEntertainmentButton.isChecked()) {
            activities.add("entertainment");
        }
        if (activityBulkBuyButton.isChecked()) {
            activities.add("bulkbuy");
        }

        return activities;
    }

}