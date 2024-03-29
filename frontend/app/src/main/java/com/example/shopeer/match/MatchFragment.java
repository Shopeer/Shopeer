package com.example.shopeer.match;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.example.shopeer.MainActivity.navController;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.shopeer.R;
import com.example.shopeer.rooms.chat.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class MatchFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    final static String TAG = "MatchFragment";

    private static final String searchUrl = "http://20.230.148.126:8080/match/searches?email=";
    private static final String suggestionUrl = "http://20.230.148.126:8080/match/suggestions?email=";
    private static final String blockUrl = "http://20.230.148.126:8080/user/blocked?email=";
    private static final String invitationUrl = "http://20.230.148.126:8080/user/invitations?email="; // for invites I SEND
    private static final String roomUrl = "http://20.230.148.126:8080/chat/room";
    private static final String profileUrl = "http://20.230.148.126:8080/user/profile?email=";

    private static String email;
    private static boolean isBrowseManagePeersTest;

    RecyclerView rv;
    private LinearLayoutManager layoutManager;

    ImageButton addSearchButton;
    ImageButton editSearchButton;
    Spinner searchSpinner;

    SearchObject currentSearch = null;

    ArrayList<SearchObject> searches = new ArrayList<SearchObject>();
    ArrayList<ProfileObject> suggestions = new ArrayList<>();

    // peer management lists
    HashSet<String> manageBlocked = new HashSet<>();
    HashSet<String> manageInvites = new HashSet<>();
    HashSet<String> managePeers = new HashSet<>();

    ProfileObject myProfile;

    public static MatchFragment newInstance() {
        MatchFragment fragment = new MatchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_match, container, false);

        this.email = getActivity().getIntent().getStringExtra("email");

        // Browse and Manage Peers testing
        isBrowseManagePeersTest = getActivity().getIntent().getBooleanExtra("isBMPTest", false);
        if (isBrowseManagePeersTest) {
            this.email = "BMPTest@test.com";
        }

        // setup search spinner
        searchSpinner = v.findViewById(R.id.search_spinner);

        // setup suggestion list profile cards
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv = v.findViewById(R.id.profile_cards_rv);

        getSearchList();

        // setup add search button
        addSearchButton = v.findViewById(R.id.add_search_button);
        addSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call edit search activity
                Intent editSearchIntent = new Intent(getActivity(), EditSearchActivity.class);
                editSearchIntent.putExtra("isNewSearch", true); // creating a search, not editing an existing one
                startActivity(editSearchIntent);
            }
        });

        // setup edit search button
        editSearchButton = v.findViewById(R.id.edit_search_button);
        editSearchButton.setVisibility(GONE);
        editSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call edit search activity with existing search info
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

        return v;
    }

    ////////////////////////////////////// handle searches /////////////////////////////////////////

    private void getSearchList() {
        if (isBrowseManagePeersTest) {
            // dummy search
            searches.clear();
            searches.add(new SearchObject("mySearch", null, 0, 0, 0, 0, null));
            setSearchSpinner();
            return;
        }

        String url = searchUrl + this.email;
        Log.d(TAG, "GET search: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "onResponse GET search: " + response);
                    searches.clear();

                    // extract search info from returned JSON Array of search objects
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            JSONObject responseObj = response.getJSONObject(i);

                            String search_name = responseObj.getString("search_name");

                            JSONArray activity = responseObj.getJSONArray("activity");
                            ArrayList<String> activities = new ArrayList<>();
                            for (int j = 0; j < activity.length(); j++) {
                                activities.add(activity.getString(j));
                            }

                            String location_name = responseObj.getString("location_name");

                            double lon  = responseObj.getDouble("location_long");
                            double lat = responseObj.getDouble("location_lati");

                            int range = responseObj.getInt("max_range");

                            int budget = responseObj.getInt("max_budget");

                            SearchObject searchObject = new SearchObject(search_name, location_name, lat, lon, range, budget, activities);
                            searches.add(searchObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (SearchObject s : searches) {
                        Log.d(TAG, "search: " + s.toString());
                    }

                    // if no searches exist, edit search button is disabled
                    if (!searches.isEmpty()) {
                        editSearchButton.setVisibility(VISIBLE);
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
    }

    private void setSearchSpinner() {
        ArrayAdapter<SearchObject> adapter = new ArrayAdapter<SearchObject>(getActivity(), android.R.layout.simple_spinner_item, this.searches);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        searchSpinner.setAdapter(adapter);
        searchSpinner.setOnItemSelectedListener(this);

        SearchObject prevSearch = SearchObject.getCurrentSearch();
        if (prevSearch != null) {
            int searchIndex = searches.indexOf(prevSearch);
            for (int i = 0; i < searches.size(); i++) {

                SearchObject s = searches.get(i);
                Log.e(TAG, "trying to find prev search index out of all searches: " + s.getSearchName());
                if (s.getSearchName().compareTo(prevSearch.getSearchName()) == 0) {
                    searchIndex = i;
                    break;
                }
            }
            Log.e(TAG, "prevSearch was: " + prevSearch.getSearchName() + " index: " + searchIndex);
            if (searchIndex >= 0) {
                searchSpinner.setSelection(searchIndex, true);
            }
        }
    }

    ////////////////////////////////////// handle suggestions //////////////////////////////////////

    private void getSuggestions() {
        String url = suggestionUrl + this.email;
        Log.d(TAG, "GET suggestions: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "GET suggestions response: " + response);
                    suggestions.clear();

                    // extract peer info from JSON Array of profile objects
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject peerObject = response.getJSONObject(i);

                            String email = peerObject.getString("email");

                            String name = peerObject.getString("name");
                            String description = peerObject.getString("description");
                            String photo = peerObject.getString("photo");

                            ProfileObject peer = new ProfileObject(email, name, description, photo);
                            suggestions.add(peer);

                            Log.d(TAG, "suggestion: " + peer.getEmail());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // populate recycler with profile cards
                    setProfileCardRecycler();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse GET suggestions: " + error.toString());
                    Toast.makeText(getContext(), "error loading suggestions", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "error loading suggestions", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProfileCardRecycler() {
        ProfileCardRA ra = new ProfileCardRA(this.suggestions);
        rv.setAdapter(ra);
        rv.setLayoutManager(layoutManager);
    }


    // OnItemSelectedListener interface
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        currentSearch = (SearchObject)parent.getItemAtPosition(pos);
        SearchObject.setCurrentSearch(currentSearch);
        Log.e(TAG, "currentSearch set as: " + currentSearch.getSearchName());

        if (isBrowseManagePeersTest){
            Log.d(TAG, "Browse Manage Peers Test");
            myProfile = new ProfileObject(email, "BMPTest", null, null);

            // set up dummy suggestions list
            this.suggestions = new ArrayList<ProfileObject>();

            // A is nobody
            createDummyProfileObj("A");

            // B sent a block
            createDummyProfileObj("B");

            // C sent invite
            createDummyProfileObj("C");

            this.manageBlocked = new HashSet<>();
            this.manageInvites = new HashSet<>();
            this.managePeers = new HashSet<>();

            setProfileCardRecycler();
            return;
        }

        setPeerManagementLists();
        // get and populate suggestion list for selected search
        getSuggestions();
    }

    // used for testing only
    private ProfileObject createDummyProfileObj(String name) {
        String email = name + "@test.com";
        String description = name + "'s description";

        ProfileObject profileObject = new ProfileObject(email, name, description, null);

        this.suggestions.add(profileObject);

        return profileObject;
    }

    private void setPeerManagementLists() {
        String url = profileUrl + email;
        Log.d(TAG, "onClick GET_profile: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "GET_profile response: " + response);

                    try {
                        JSONArray blockedList = response.getJSONArray("blocked");
                        manageBlocked = jsonArrayToHashSet(blockedList);

                        JSONArray sentInvitesList = response.getJSONArray("invites");
                        manageInvites = jsonArrayToHashSet(sentInvitesList);

                        JSONArray peersList = response.getJSONArray("peers");
                        managePeers = jsonArrayToHashSet(peersList);

                        // set my own profile info
                        String myName = response.getString("name");
                        String myEmail = response.getString("email");
                        myProfile = new ProfileObject(myEmail, myName, null, null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse GET_profile: " + error.toString());
                }
            });
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashSet<String> jsonArrayToHashSet(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return new HashSet<String>();
        }

        HashSet<String> result = new HashSet<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String email = jsonArray.getString(i);
            result.add(email);
        }

        return result;
    }

    // OnItemSelectedListener interface
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // required to implement OnItemSelectedListener interface
    }

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
            ProfileObject profileObject = data.get(position);

            // setup button onClick handlers
            holder.blockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blockPeer(profileObject, holder);
                }
            });

            holder.unblockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unblockPeer(profileObject, holder);
                }
            });

            holder.friendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invitePeer(profileObject, holder);
                }
            });

            holder.unfriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteInvite(profileObject, holder);
                }
            });
            
            // populate profilecard
            setProfileCardInfo(profileObject, holder);

            // set default button visibility
            setButtonVisibility(profileObject, holder);

        }

        private void setProfileCardInfo(ProfileObject peer, ProfileCardVH holder) {
            if (peer.getPhotoBitmap() == null) {
                holder.peerPhoto.setImageResource(R.drawable.temp_profile);
            }
            else {
                holder.peerPhoto.setImageBitmap(peer.getPhotoBitmap());
            }

            holder.peerName.setText(peer.getName());

            if (peer.getDescription() != null && peer.getDescription().compareToIgnoreCase("null") != 0) {
                holder.peerDescription.setText(peer.getDescription());
            }
        }

        private void setButtonVisibility(ProfileObject peer, ProfileCardVH holder) {
            holder.unfriendButton.setVisibility(GONE);
            holder.friendButton.setVisibility(VISIBLE);
            holder.unblockButton.setVisibility(GONE);
            holder.blockButton.setVisibility(VISIBLE);

            holder.peerPhoto.setVisibility(VISIBLE);
            holder.peerPhotoCard.setVisibility(VISIBLE);
            holder.peerPhotoCard.setVisibility(VISIBLE);
            holder.peerDescription.setVisibility(VISIBLE);

            // checked if blocked
            if (manageBlocked.contains(peer.getEmail())) {
                holder.blockButton.setVisibility(GONE);
                holder.unblockButton.setVisibility(VISIBLE);
                holder.friendButton.setVisibility(GONE);
                holder.unfriendButton.setVisibility(GONE);

                // profile info also set to invisible
                holder.peerPhoto.setVisibility(View.INVISIBLE);
                holder.peerPhotoCard.setVisibility(View.INVISIBLE);
                holder.peerPhotoCard.setVisibility(View.INVISIBLE);
                holder.peerDescription.setVisibility(View.INVISIBLE);
            }
            // check if sent invite
            else if (manageInvites.contains(peer.getEmail())) {
                holder.blockButton.setVisibility(GONE);
                holder.unblockButton.setVisibility(GONE);
                holder.friendButton.setVisibility(GONE);
                holder.unfriendButton.setVisibility(VISIBLE);
            }
        }

        private void createChatroom(String myEmail, ProfileObject peer, ProfileCardVH holder) {
            Log.d(TAG, "onClick POST_create_room: " + roomUrl);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                // create POST request body
                JSONObject body = new JSONObject();

                String roomName = peer.getName() + " & " + myProfile.getName();
                body.put("name", roomName);

                JSONArray peerslist = new JSONArray();
                peerslist.put(myEmail);
                peerslist.put(peer.getEmail());

                body.put("peerslist", peerslist);

                JSONArray chathistory = new JSONArray();
                body.put("chathistory", chathistory);

                final String reqBody = body.toString();

                Log.d(TAG, "POST_create_room request body: " + reqBody);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, roomUrl, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST_create_room response: " + response);

                        data.remove(holder.getBindingAdapterPosition()); // pass by ref, so will update this.suggestions also
                        notifyItemRemoved(holder.getBindingAdapterPosition());
                        notifyItemRangeChanged(holder.getBindingAdapterPosition(), data.size());

                        managePeers.add(peer.getEmail());

                        // going to Rooms fragment, to the auto created room
                        navController.navigate(R.id.roomsFragment);
                        // Redirect to new chat activity
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        try {
                            intent.putExtra("room_id", response.getString("insertedId"));
                            intent.putExtra("room_name", peer.getName());
                            intent.putExtra("room_pic", peer.getPhotoBitmap());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse POST_create_room: " + error.toString());
                        Toast.makeText(getContext(), "error creating chatroom with " + peer.getName(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), "error creating chatroom with " + peer.getName(), Toast.LENGTH_SHORT).show();

                    }
                });
                requestQueue.add(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void deleteInvite(ProfileObject peer, ProfileCardVH holder) {
            String url = invitationUrl + email + "&target_peer_email=" + peer.getEmail();
            Log.d(TAG, "onClick DELETE_invitation: " + url);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DELETE_invitation response: " + response);

                        manageInvites.remove(peer.getEmail());

                        // set button visibility
                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse DELETE_invitation: " + error.toString());
                        Toast.makeText(getContext(), "error removing invite to " + peer.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void invitePeer(ProfileObject peer, ProfileCardVH holder) {
            String url = invitationUrl + email + "&target_peer_email=" + peer.getEmail();
            Log.d(TAG, "onClick POST_invitation: " + url);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                StringRequest stringReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "POST_invitation response: " + response);

                        manageInvites.add(peer.getEmail());

                        if (response.compareToIgnoreCase("{\"response\":\"Success, both are now peers.\"}") == 0) {
                            Log.d(TAG, "POST_invitation all g" );
                            createChatroom(email, peer, holder);
                        }

                        // set button visibility
                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse POST_invitation: " + error.toString());
                        Toast.makeText(getContext(), "error sending invite to " + peer.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(stringReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void unblockPeer(ProfileObject peer, ProfileCardVH holder) {
            String url = blockUrl + email + "&target_peer_email=" + peer.getEmail();
            Log.d(TAG, "onClick DELETE_block: " + url);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DELETE_block response: " + response);

                        manageBlocked.remove(peer.getEmail());

                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse DELETE_block: " + error.toString());
                        Toast.makeText(getContext(), "error unblocking " + peer.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void blockPeer(ProfileObject peer, ProfileCardVH holder) {
            String url = blockUrl + email + "&target_peer_email=" + peer.getEmail();
            Log.d(TAG, "onClick POST_block: " + url);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST_block response: " + response);

                        manageBlocked.add(peer.getEmail());

                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse post_search: " + error.toString());
                        Toast.makeText(getContext(), "error blocking " + peer.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ProfileCardVH extends RecyclerView.ViewHolder {
            ImageView peerPhoto;
            CardView peerPhotoCard;
            TextView peerName;
            TextView peerDescription;
            Button friendButton;
            Button unfriendButton;
            Button blockButton;
            Button unblockButton;

            public ProfileCardVH(@NonNull View itemView) {
                super(itemView);

                peerPhoto = itemView.findViewById(R.id.peer_profile_photo);
                peerPhotoCard = itemView.findViewById(R.id.peer_profile_photo_cardView);
                peerName = itemView.findViewById(R.id.peer_name_text);
                peerDescription = itemView.findViewById(R.id.peer_description_text);
                friendButton = itemView.findViewById(R.id.friend_button);
                unfriendButton = itemView.findViewById(R.id.unfriend_button);
                blockButton = itemView.findViewById(R.id.block_button);
                unblockButton = itemView.findViewById(R.id.unblock_button);
            }
        }
    }
}
