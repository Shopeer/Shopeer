package com.example.shopeer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyLog;

import java.io.UnsupportedEncodingException;
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

    RecyclerView rv;
    private LinearLayoutManager layoutManager;

    ImageButton addSearchButton;
    ImageButton editSearchButton;
    Spinner searchSpinner;

    SearchObject currentSearch = null;

    ArrayList<SearchObject> searches = new ArrayList<SearchObject>();
    ArrayList<ProfileObject> suggestions = new ArrayList<>();

    // peer management lists
    HashSet<String> manageBlocked;
    HashSet<String> manageInvites;
    HashSet<String> managePeers;

    public static MatchFragment newInstance() {
        MatchFragment fragment = new MatchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.email = getActivity().getIntent().getStringExtra("email");
        getSearchList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_match, container, false);

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

        // setup search spinner
        searchSpinner = v.findViewById(R.id.search_spinner);

        // setup suggestion list profile cards
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv = v.findViewById(R.id.profile_cards_rv);

        return v;
    }

    ////////////////////////////////////// handle searches /////////////////////////////////////////

    private ArrayList<SearchObject> getSearchList() {
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

                            String location_name = "";
                            try { //TODO: get rid of this try catch once all searches in server have location_name
                                location_name = responseObj.getString("location_name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONArray location = responseObj.getJSONArray("location");
                            double lon = location.getDouble(0);
                            double lat = location.getDouble(1);

                            int range = responseObj.getInt("max_range");

                            int budget = responseObj.getInt("max_budget");

                            SearchObject searchObject = new SearchObject(search_name, location_name, lat, lon, range, budget, activities);
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

    private void setSearchSpinner() {
        ArrayAdapter<SearchObject> adapter = new ArrayAdapter<SearchObject>(getActivity(), android.R.layout.simple_spinner_item, this.searches);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        searchSpinner.setAdapter(adapter);
        searchSpinner.setOnItemSelectedListener(this);
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

                            if (managePeers.contains(email)) {
                                continue;
                            }


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

        setPeerManagementLists();

        // get and populate suggestion list for selected search
        getSuggestions();
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
            holder.peerName.setText(profileObject.getEmail());

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
            setProfileCardInfo();

            // set default button visibility
            setButtonVisibility(profileObject, holder);

        }

        private void setProfileCardInfo() {
        }

        private void setButtonVisibility(ProfileObject peer, ProfileCardVH holder) {
            holder.unfriendButton.setVisibility(View.GONE);
            holder.friendButton.setVisibility(View.VISIBLE);
            holder.unblockButton.setVisibility(View.GONE);
            holder.blockButton.setVisibility(View.VISIBLE);

            // checked if blocked
            if (manageBlocked.contains(peer.getEmail())) {
                holder.blockButton.setVisibility(View.GONE);
                holder.unblockButton.setVisibility(View.VISIBLE);
                holder.friendButton.setVisibility(View.GONE);
                holder.unfriendButton.setVisibility(View.GONE);
            }
            // check if sent invite
            else if (manageInvites.contains(peer.getEmail())) {
                holder.blockButton.setVisibility(View.GONE);
                holder.unblockButton.setVisibility(View.GONE);
                holder.friendButton.setVisibility(View.GONE);
                holder.unfriendButton.setVisibility(View.VISIBLE);
            }
        }

        private void createChatroom(String myEmail, String peerEmail, ProfileCardVH holder) {
            Log.d(TAG, "onClick POST_create_room: " + roomUrl);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                // create POST request body
                JSONObject body = new JSONObject();
                body.put("name", peerEmail.split("@")[0]);

                JSONArray peerslist = new JSONArray();
                peerslist.put(myEmail);
                peerslist.put(peerEmail);

                body.put("peerslist", peerslist);

                JSONArray chathistory = new JSONArray();
                body.put("chathistory", chathistory);

                final String reqBody = body.toString();

                Log.d(TAG, "POST_create_room request body: " + reqBody);

                StringRequest jsonObjReq = new StringRequest(Request.Method.POST, roomUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "POST_create_room response: " + response);
                        Toast.makeText(getContext(), "created new chatroom with " + peerEmail, Toast.LENGTH_LONG).show();

                        data.remove(holder.getBindingAdapterPosition()); // pass by ref, so will update this.suggestions also
                        notifyItemRemoved(holder.getBindingAdapterPosition());
                        notifyItemRangeChanged(holder.getBindingAdapterPosition(), data.size());

                        managePeers.add(peerEmail);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse POST_create_room: " + error.toString());

                    }
                })
                {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return reqBody == null ? null : reqBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", reqBody, "utf-8");
                            return null;
                        }
                    }
                };
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
                        Toast.makeText(getContext(), "redacted invitation to " + peer.getEmail(), Toast.LENGTH_LONG).show();

                        manageInvites.remove(peer.getEmail());

                        // set button visibility
                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse DELETE_invitation: " + error.toString());
                        Toast.makeText(getContext(), "error: could not redact invite to" + peer.getEmail(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(), "sent invitation to " + peer.getEmail(), Toast.LENGTH_SHORT).show();

                        manageInvites.add(peer.getEmail());

                        if (response.compareToIgnoreCase("success, both are now peers") == 0) {
                            createChatroom(email, peer.getEmail(), holder);
                        }

                        // set button visibility
                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse POST_invitation: " + error.toString());
                        Toast.makeText(getContext(), "error: could not send invite to" + peer.getEmail(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(), "ublocked " + peer.getEmail(), Toast.LENGTH_LONG).show();

                        manageBlocked.remove(peer.getEmail());

                        setButtonVisibility(peer, holder);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse DELETE_block: " + error.toString());
                        Toast.makeText(getContext(), "error: could not ublock " + peer.getEmail(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(), "blocked " + peer.getEmail(), Toast.LENGTH_LONG).show();

                        manageBlocked.add(peer.getEmail());

                        setButtonVisibility(peer, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse post_search: " + error.toString());
                        Toast.makeText(getContext(), "error: could block " + peer.getEmail(), Toast.LENGTH_LONG).show();
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
            TextView peerName;
            Button friendButton;
            Button unfriendButton;
            Button blockButton;
            Button unblockButton;

            public ProfileCardVH(@NonNull View itemView) {
                super(itemView);

                peerName = itemView.findViewById(R.id.peer_name_text);
                friendButton = itemView.findViewById(R.id.friend_button);
                unfriendButton = itemView.findViewById(R.id.unfriend_button);
                blockButton = itemView.findViewById(R.id.block_button);
                unblockButton = itemView.findViewById(R.id.unblock_button);
            }
        }
    }
}
