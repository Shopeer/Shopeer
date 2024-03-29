package com.example.shopeer.match;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.example.shopeer.BuildConfig;
import com.example.shopeer.MainActivity;
import com.example.shopeer.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class EditSearchActivity extends AppCompatActivity {
    private static final String TAG = "editSearchActivity";
    private static final String searchUrl = "http://20.230.148.126:8080/match/searches?email=";
    int SERVER_TIMEOUT_MS = 1000; // num ms wait for server
    private static final String API_KEY = BuildConfig.GPLACES_API_KEY;

    private boolean isNewSearch;

    private EditText searchName;
    private TextView searchLocation;
    private EditText distanceNumber;
    private CheckBox activityGroceriesCheckBox;
    private CheckBox activityEntertainmentCheckBox;
    private CheckBox activityBulkBuyCheckBox;
    private CheckBox activityHikingCheckBox;
    private CheckBox activityRestaurantsCheckBox;
    private CheckBox activityFashionCheckBox;
    private CheckBox activityBooksCheckBox;
    private EditText budgetNumber;
    private Button deleteButton;
    private Button saveButton;

    private String oldSearchName;
    private double locationLat;
    private double locationLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_search);

        Log.d(TAG, "starting editSearchActivity");

        init();
        initPlacesSearch();
        setDeleteButton();
        setSaveButton();

    }

    private void initPlacesSearch() {
        // init the SDK
        Places.initialize(getApplicationContext(), API_KEY);

        Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // biasing to improve predictions
        autocompleteFragment.setCountry("CA"); // Canada

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                searchLocation.setText(place.getName());
                locationLat = place.getLatLng().latitude;
                locationLon = place.getLatLng().longitude;
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void init() {
        // set up resources
        searchName = findViewById(R.id.search_name_text);
        searchLocation = findViewById(R.id.search_location_text);
        distanceNumber = findViewById(R.id.distance_number);
        budgetNumber = findViewById(R.id.budget_number);

        activityGroceriesCheckBox = findViewById(R.id.activity_groceries_checkBox);
        activityEntertainmentCheckBox = findViewById(R.id.activity_entertainment_checkBox);
        activityBulkBuyCheckBox = findViewById(R.id.activity_bulkBuy_checkBox);
        activityHikingCheckBox = findViewById(R.id.activity_hiking_checkBox);
        activityRestaurantsCheckBox = findViewById(R.id.activity_restaurants_checkBox);
        activityFashionCheckBox = findViewById(R.id.activity_fashion_checkBox);
        activityBooksCheckBox = findViewById(R.id.activity_books_checkBox);

        deleteButton = findViewById(R.id.delete_search_button);
        saveButton = findViewById(R.id.save_search_button);

        TextView pageTitle = findViewById(R.id.edit_search_toolbar_text);

        // check if creating a new search or not
        Intent intent = getIntent();
        this.isNewSearch = intent.getBooleanExtra("isNewSearch", true);

        // is an existing search open for editing
        if (!isNewSearch) {
            pageTitle.setText("Edit Search");

            //set inputs as existing values from the intent data
            oldSearchName = intent.getStringExtra("searchName");
            searchName.setText(oldSearchName);

            this.locationLat = intent.getDoubleExtra("lat", -1);
            this.locationLon = intent.getDoubleExtra("lon", -1);

            if (intent.getStringExtra("locationName").equals("") || intent.getStringExtra("locationName") == null) {
                // set to lat lon if no location name set
                searchLocation.setText("lat: " + this.locationLat + "\nlon: " + this.locationLon);
            }
            else {
                searchLocation.setText(intent.getStringExtra("locationName"));
            }

            distanceNumber.setText(Integer.toString(intent.getIntExtra("range", 0)));
            budgetNumber.setText(Integer.toString(intent.getIntExtra("budget", 0)));

            // show the activities
            ArrayList<String> acts = intent.getStringArrayListExtra("activities");
            for (String a : acts) {
                if (a.equals("entertainment")) {
                    activityEntertainmentCheckBox.setChecked(true);
                }
                else if (a.compareToIgnoreCase("bulkbuy") == 0) {
                    activityBulkBuyCheckBox.setChecked(true);
                }
                else if (a.compareToIgnoreCase("groceries") == 0) {
                    activityGroceriesCheckBox.setChecked(true);
                }
                else if (a.compareToIgnoreCase("hiking") == 0) {
                    activityHikingCheckBox.setChecked(true);
                }
                else if (a.compareToIgnoreCase("restaurants") == 0) {
                    activityRestaurantsCheckBox.setChecked(true);
                }
                else if (a.compareToIgnoreCase("fashion") == 0) {
                    activityFashionCheckBox.setChecked(true);
                }
                else if (a.compareToIgnoreCase("books") == 0) {
                    activityBooksCheckBox.setChecked(true);
                }
            }

            Log.d(TAG, "opened " + oldSearchName + " for edit");
        }
        else {
            Log.d(TAG, "creating a new search");
            pageTitle.setText("Create Search");
            pageTitle.setText("Create Search");
            setDefaultLocation();
        }

    }

    private void setDefaultLocation(){
        // defaults to north pole in beginning
        searchLocation.setText("North Pole");
        locationLat = 90;
        locationLon = 135;
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
                    String url = searchUrl + MainActivity.email + "&search_name=" + oldSearchName;
                    Log.d(TAG, "onClick: " + url);
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(EditSearchActivity.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "delete_search response: " + response);
                                Intent intent = new Intent(EditSearchActivity.this, MainActivity.class);
                                intent.putExtra("email", MainActivity.email);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse DELETE_search: " + error.toString());
                                Toast.makeText(EditSearchActivity.this, "error deleting search", Toast.LENGTH_SHORT).show();
                                Toast.makeText(EditSearchActivity.this, "error deleting search", Toast.LENGTH_SHORT).show();
                            }
                        });

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
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

                // alpha and numbers only, done by textedit
                // duplicate name check by server on request
                String nameInput = searchName.getText().toString();
                if (nameInput.compareTo("") == 0) {
                    Toast.makeText(EditSearchActivity.this, "please set search name", Toast.LENGTH_SHORT).show();
                    canSave = false;
                }

                // by Google Places
                String locationInput = searchLocation.getText().toString();

                // by Google Places
                double latInput = locationLat;
                double lonInput = locationLon;

                // pos int only, by textedit
                String rangeInputText = distanceNumber.getText().toString();
                int rangeInput;
                if (rangeInputText.compareTo("") == 0) {
                    rangeInput = 0;
                }
                else {
                    rangeInput = Integer.parseInt(rangeInputText);
                    if (rangeInput >= 100) {
                        Toast.makeText(EditSearchActivity.this, "set distance range below 100 km", Toast.LENGTH_SHORT).show();
                        canSave = false;
                    }
                }

                // at least one activity chosen
                ArrayList<String> activitiesInput = getActivitySelection();
                if (activitiesInput.isEmpty()) {
                    Toast.makeText(EditSearchActivity.this, "choose at least one activity", Toast.LENGTH_SHORT).show();
                    canSave = false;
                }

                // pos int only, by textedit
                String budgetInputText = budgetNumber.getText().toString();
                int budgetInput;
                if (budgetInputText.compareTo("") == 0) {
                    budgetInput = 0;
                }
                else {
                    budgetInput = Integer.parseInt(budgetNumber.getText().toString());
                }


                // send request
                if (!canSave) {
                    Log.d(TAG, "bad input, cannot save yet");
                }
                else {
                    if (!isNewSearch) {
                        updateSearch(nameInput, locationInput, latInput, lonInput, rangeInput, activitiesInput, budgetInput);
                    }
                    else {
                        createSearch(nameInput, locationInput, latInput, lonInput, rangeInput, activitiesInput, budgetInput);
                    }

                }

            }
        });
    }

    // create search is POST
    private void createSearch(String nameInput, String locationInput, double latInput, double lonInput, int rangeInput, ArrayList<String> activitiesInput, int budgetInput) {
        String url = searchUrl + MainActivity.email;
        Log.d(TAG, "create POST_search: " + url);
        try {
            //TODO: change to be for POST to search
            RequestQueue requestQueue = Volley.newRequestQueue(EditSearchActivity.this);

            // create json
            JSONObject search = new JSONObject();
            search.put("search_name", nameInput);

            JSONArray activity = new JSONArray(activitiesInput);
            search.put("activity",activity);

            search.put("location_name", locationInput);

            search.put("location_long", lonInput);
            search.put("location_lati", latInput);

            search.put("max_range", rangeInput);

            search.put("max_budget", budgetInput);

            Log.d(TAG, "POST_search request body: " + search);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, search, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "post_search response: " + response);
                    //Toast.makeText(EditSearchActivity.this, "saved " + nameInput + " search", Toast.LENGTH_SHORT).show();

                    SearchObject.setCurrentSearch(new SearchObject(nameInput, locationInput, latInput, lonInput, rangeInput, budgetInput, activitiesInput));

                    Intent intent = new Intent(EditSearchActivity.this, MainActivity.class);
                    intent.putExtra("email", MainActivity.email);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int statusCode = Integer.valueOf(String.valueOf(error.networkResponse.statusCode));

                    if (statusCode == 409) {
                        // search with same name already exists
                        Log.e(TAG, "onErrorResponse post_search: " + error.toString() + "\nerr code: " + statusCode);
                        Toast.makeText(EditSearchActivity.this, "search with same name already exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.e(TAG, "onErrorResponse post_search: " + error.toString() + "\nerr code: " + statusCode);
                        Toast.makeText(EditSearchActivity.this, "error: could not save search", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // updating existing search is PUT request
    private void updateSearch(String nameInput, String locationInput, double latInput, double lonInput, int rangeInput, ArrayList<String> activitiesInput, int budgetInput) {
        String url = searchUrl + MainActivity.email + "&search_name_name=" + oldSearchName;

        Log.d(TAG, "update existing PUT_search: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(EditSearchActivity.this);

            // create json
            JSONObject search = new JSONObject();
            search.put("search_name", nameInput);

            JSONArray activity = new JSONArray(activitiesInput);
            search.put("activity",activity);

            search.put("location_name", locationInput);

            search.put("location_long", lonInput);
            search.put("location_lati", latInput);

            search.put("max_range", rangeInput);

            search.put("max_budget", budgetInput);

            Log.d(TAG, "PUT_search request body: " + search);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, search, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "post_search response: " + response);
                    //Toast.makeText(EditSearchActivity.this, "saved " + nameInput + " search", Toast.LENGTH_SHORT).show();

                    SearchObject.setCurrentSearch(new SearchObject(nameInput, locationInput, latInput, lonInput, rangeInput, budgetInput, activitiesInput));

                    Intent intent = new Intent(EditSearchActivity.this, MainActivity.class);
                    intent.putExtra("email", MainActivity.email);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int statusCode = Integer.valueOf(String.valueOf(error.networkResponse.statusCode));

                    if (statusCode == 409) {
                        // search with same name already exists
                        Log.e(TAG, "onErrorResponse PUT_search: " + error.toString() + "\nerr code: " + statusCode);
                        Toast.makeText(EditSearchActivity.this, "search with same name already exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.e(TAG, "onErrorResponse post_search: " + error.toString() + "\nerr code: " + statusCode);
                        Toast.makeText(EditSearchActivity.this, "error: could not save search", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * checks which activities were selected (no ranking yet) adn returns String array of them
     */
    private ArrayList<String> getActivitySelection() {
        ArrayList<String> activities = new ArrayList<String>();

        if (activityGroceriesCheckBox.isChecked()) {
            activities.add("groceries");
        }
        if (activityEntertainmentCheckBox.isChecked()) {
            activities.add("entertainment");
        }
        if (activityBulkBuyCheckBox.isChecked()) {
            activities.add("bulkbuy");
        }
        if (activityHikingCheckBox.isChecked()) {
            activities.add("hiking");
        }
        if (activityRestaurantsCheckBox.isChecked()) {
            activities.add("restaurants");
        }
        if (activityFashionCheckBox.isChecked()) {
            activities.add("fashion");
        }
        if (activityBooksCheckBox.isChecked()) {
            activities.add("books");
        }

        return activities;
    }

}