package com.example.shopeer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    public MatchFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchFragment newInstance(String param1, String param2) {
        MatchFragment fragment = new MatchFragment();
        /*
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        */

        return fragment;
    }

    ///////////////////////////////my stuff/////////////////////////////
    final static String TAG = "MatchFragment";
    private static final String searchUrl = "http://20.230.148.126:8080/match/searches?email=";
    private static String email;

    RecyclerView rv;
    private LinearLayoutManager layoutManager;

    ImageButton addSearchButton;
    ImageButton editSearchButton;
    Spinner searchSpinner;

    SearchObject currentSearch = null;

    ArrayList<SearchObject> searches = new ArrayList<SearchObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
         */

        this.email = getActivity().getIntent().getStringExtra("email");
        getSearchList();

    }

    // do graphical stuff here, always called after onCreate
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_match, container, false);

        HashSet<SearchObject> searches = ((MainActivity)getActivity()).searches;

        // add search button
        addSearchButton = v.findViewById(R.id.add_search_button);
        addSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // doing a pop up for edit search
                /*
                View search_edit_view = LayoutInflater.from(container.getContext()).inflate(R.layout.search_edit_layout,  container, false);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(search_edit_view, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                */

                // call edit search activity
                Intent editSearchIntent = new Intent(getActivity(), EditSearchActivity.class);
                editSearchIntent.putExtra("isNewSearch", true); // creating a search, no editing a existing one
                startActivity(editSearchIntent);

            }
        });

        // edit search button
        editSearchButton = v.findViewById(R.id.edit_search_button);
        editSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call edit search activity
                Intent editSearchIntent = new Intent(getActivity(), EditSearchActivity.class);
                editSearchIntent.putExtra("isNewSearch", false);
                editSearchIntent.putExtra("searchName", currentSearch.getSearchName());
                editSearchIntent.putExtra("locationName", currentSearch.getLocation());
                editSearchIntent.putExtra("lat", currentSearch.getLat());
                editSearchIntent.putExtra("lon", currentSearch.getLon());
                editSearchIntent.putExtra("range", currentSearch.getRange());
                editSearchIntent.putExtra("activities", currentSearch.getActivities());
                editSearchIntent.putExtra("budget", currentSearch.getBudget());
                startActivity(editSearchIntent);
            }
        });

        ///////////////////// get searches ////////////////////////

        ArrayList<SearchObject> s = getSearchList();

        searchSpinner = v.findViewById(R.id.search_spinner);

        // fetch data of searches
        //List<SearchObject> searchList = new ArrayList<SearchObject>();
        for (int i=0; i < 4; i++) {
            ArrayList<String> activity = new ArrayList<String>();
            activity.add("entertainment");
            searches.add(new SearchObject("search " + i, "location" + i, i, i, i,i, activity));
        }

        // convert hashset of searches to a list
        ArrayList<SearchObject> searchList = new ArrayList<SearchObject>();
        searchList.addAll(searches);
        Collections.sort(searchList);

        //ArrayAdapter<SearchObject> adapter = new ArrayAdapter<SearchObject>(getActivity(), android.R.layout.simple_spinner_item, searchList);
        //ArrayAdapter<SearchObject> adapter = new ArrayAdapter<SearchObject>(getActivity(), android.R.layout.simple_spinner_item, this.searches);
        //adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

       // searchSpinner.setAdapter(adapter);

       // searchSpinner.setOnItemSelectedListener(this);




        /////////////////////////////// profile card stuff ////////////////////////////////////////

        // layout manager
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        // initialize recycler view
        ArrayList<ProfileObject> peersList = new ArrayList<>(); // will be replaced with one in searchObject
        rv = v.findViewById(R.id.profile_cards_rv);

        // fetch data of peers and add to peersList
        for (int i=0; i < 3; i++) {
            String name = "Peer Number " + i;
            peersList.add(new ProfileObject(name));
        }

        ProfileCardRA ra = new ProfileCardRA(peersList);
        rv.setAdapter(ra);
        rv.setLayoutManager(layoutManager);

        return v;
    }

    private ArrayList<SearchObject> getSearchList() {
        String url = searchUrl + this.email;
        Log.d(TAG, "GET search: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "onResponse GET search: " + response);
                    //TODO: get this info extracted somehow...
                    searches.clear();

                    for (int i = 0; i < response.length(); i++) {
                        // creating a new json object and
                        // getting each object from our json array.
                        try {
                            // get each search object
                            JSONObject responseObj = response.getJSONObject(i);

                            String search_name = responseObj.getString("search_name");

                            JSONArray activity = responseObj.getJSONArray("activity");
                            ArrayList<String> activities = new ArrayList<>();
                            for (int j = 0; j < activity.length(); j++) {
                                activities.add(activity.getString(j));
                            }

                            JSONArray location = responseObj.getJSONArray("location");
                            double lon = location.getDouble(0);
                            double lat = location.getDouble(1);

                            int range = responseObj.getInt("max_range");

                            int budget = responseObj.getInt("max_budget");

                            SearchObject searchObject = new SearchObject(search_name, "", lat, lon, range, budget, activities);

                            searches.add(searchObject);

                            Log.d(TAG, "search: " + searchObject.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (SearchObject s : searches) {
                        Log.d(TAG, "search: " + s.toString());
                    }
                    setSearchSpinner();
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
        return null;
    }

    public void setSearchSpinner() {
        ArrayAdapter<SearchObject> adapter = new ArrayAdapter<SearchObject>(getActivity(), android.R.layout.simple_spinner_item, this.searches);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        searchSpinner.setAdapter(adapter);

        searchSpinner.setOnItemSelectedListener(this);
    }


    //OnItemSelectedListener interface
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        currentSearch = (SearchObject)parent.getItemAtPosition(pos);
    }

    // OnItemSelectedListener interface
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //////////////////////////////////////////////////////////////////// start profile cards stuff //////////////////////////////////////////////////////////////////////
    class ProfileCardRA extends RecyclerView.Adapter<ProfileCardRA.ProfileCardVH> {
        ArrayList<ProfileObject> data;

        public ProfileCardRA(ArrayList<ProfileObject> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ProfileCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_card_layout, parent, false);
            return new ProfileCardVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ProfileCardVH holder, int position) {
            ProfileObject po = data.get(position);
            holder.peerName.setText(po.getName());

            //TODO: if user has received invite from this peer, .setVisibility(View.GONE) of friend_button, and View.VISIBLE of accept and decline, vice versa
            //TODO: deal with backend when buttons are clicked
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        class ProfileCardVH extends RecyclerView.ViewHolder {
            TextView peerName;
            Button friendButton;
            Button acceptButton;
            Button declineButton;
            Button blockButton;

            public ProfileCardVH(@NonNull View itemView) {
                super(itemView);

                peerName = itemView.findViewById(R.id.peer_name_text);
                friendButton = itemView.findViewById(R.id.friend_button);
                acceptButton = itemView.findViewById(R.id.accept_button);
                declineButton = itemView.findViewById(R.id.decline_button);
                blockButton = itemView.findViewById(R.id.block_button);
            }
        }
    }
//////////////////////////////////////////////////////////// end profile card stuff ///////////////////////////////////////////////////////////
}
