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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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

        return fragment;
    }

    ///////////////////////////////my stuff/////////////////////////////
    final static String TAG = "MatchFragment";
    private static final String searchUrl = "http://20.230.148.126:8080/match/searches?email=";
    private static final String suggestionUrl = "http://20.230.148.126:8080/match/suggestions?email=";
    private static final String blockUrl = "http://20.230.148.126:8080/user/blocked?email=";
    private static final String invitationUrl = "http://20.230.148.126:8080/user/invitations?email="; // for invites I SEND
    private static final String receivedInvitationUrl = "http://20.230.148.126:8080/user/invitations/received?email="; // to get recieved invites
    private static final String peersUrl = "http://20.230.148.126:8080/user/peers?email=";
    private static final String roomUrl = "http://20.230.148.126:8080/chat/room";

    private static String email;

    RecyclerView rv;
    private LinearLayoutManager layoutManager;

    ImageButton addSearchButton;
    ImageButton editSearchButton;
    Spinner searchSpinner;

    SearchObject currentSearch = null;

    ArrayList<SearchObject> searches = new ArrayList<SearchObject>();
    ArrayList<ProfileObject> suggestions = new ArrayList<>();

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


        //ArrayList<SearchObject> s = getSearchList();

        searchSpinner = v.findViewById(R.id.search_spinner);

        // fetch data of searches
        //List<SearchObject> searchList = new ArrayList<SearchObject>();
        //for (int i=0; i < 4; i++) {
        //    ArrayList<String> activity = new ArrayList<String>();
        //    activity.add("entertainment");
        //    searches.add(new SearchObject("search " + i, "location" + i, i, i, i,i, activity));
        //}

        // convert hashset of searches to a list
        //ArrayList<SearchObject> searchList = new ArrayList<SearchObject>();
        //searchList.addAll(searches);
        //Collections.sort(searchList);

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

        /*
        // fetch data of peers and add to peersList
        for (int i=0; i < 3; i++) {
            String name = "Peer Number " + i;
            peersList.add(new ProfileObject(name));
        }

        ProfileCardRA ra = new ProfileCardRA(peersList);
        rv.setAdapter(ra);
        rv.setLayoutManager(layoutManager);
        */
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

    private void setSearchSpinner() {
        ArrayAdapter<SearchObject> adapter = new ArrayAdapter<SearchObject>(getActivity(), android.R.layout.simple_spinner_item, this.searches);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        searchSpinner.setAdapter(adapter);

        searchSpinner.setOnItemSelectedListener(this);
    }

    /**
     * later, this can be specific for each search, but for now, same backend call any search selected
     */
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
                    // extract the info
                    for (int i = 0; i < response.length(); i++) {
                        // creating a new json object and
                        // getting each object from our json array.
                        try {
                            // get each search object
                            JSONArray responseArr = response.getJSONArray(i);

                            String email = responseArr.getString(0);
                            ProfileObject peer = new ProfileObject(email);

                            suggestions.add(peer);

                            Log.d(TAG, "suggestion: " + peer.getEmail());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // TODO: set up recycler
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


    //OnItemSelectedListener interface for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        currentSearch = (SearchObject)parent.getItemAtPosition(pos);

        // set suggestion list
        // later, this can be specific for each search, but for now, same backend call any search selected
        getSuggestions();
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
            holder.peerName.setText(po.getEmail());

            //TODO: if user has received invite from this peer, .setVisibility(View.GONE) of friend_button, and View.VISIBLE of accept and decline, vice versa
            //TODO: deal with backend when buttons are clicked

            // block button
            holder.blockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blockPeer(po, holder);
                }
            });

            // friend button
            holder.friendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invitePeer(po, holder);
                }
            });

            // unfriend , ie. redact the invitiation
            holder.unfriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteInvite(po, holder);
                }
            });

            // accept
            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptInvite(po, holder);
                }
            });

            //decline
            holder.declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    declineInvite(po, holder);
                }
            });
                
            // check if already recived an invite - decided which buttons to show
            setButtonVisibility(po, holder);

        }

        //findRelativeAdapterPositionIn(Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder viewHolder, int localPosition)

        private void setButtonVisibility(ProfileObject peer, ProfileCardVH holder) {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.unfriendButton.setVisibility(View.GONE);
            checkIfInviteSent(peer, holder);
            checkIfInviteReceived(peer, holder);
        }

        /**
         * if I received an invite, then show accept/decline buttons
         * @param peer
         * @param holder
         */
        private void checkIfInviteReceived(ProfileObject peer, ProfileCardVH holder) {
            String url = receivedInvitationUrl + email;
            Log.d(TAG, "onClick GET_received_invitation: " + url);
            try {
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonArrayRequest Req = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "GET_received_invitation response: " + response);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                // get each search object
                                JSONObject responseObj = response.getJSONObject(i);

                                String sentEmail = responseObj.getString("email");

                                if (sentEmail.equals(peer.getEmail())) {
                                    // already received an invite
                                    // make friend/unfriend invisible
                                    holder.unfriendButton.setVisibility(View.GONE);
                                    holder.friendButton.setVisibility(View.GONE);
                                    holder.declineButton.setVisibility(View.VISIBLE);
                                    holder.acceptButton.setVisibility(View.VISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse GET_received_invitation: " + error.toString());
                        data.remove(holder.getBindingAdapterPosition()); // pass by ref, so will update this.suggestions also
                        notifyItemRemoved(holder.getBindingAdapterPosition());
                        notifyItemRangeChanged(holder.getBindingAdapterPosition(), data.size());
                    }
                });
                requestQueue.add(Req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * if I already sent them an invite, then only show unfriendButton
         * @param peer
         * @param holder
         */
        private void checkIfInviteSent(ProfileObject peer, ProfileCardVH holder) {
            String url = invitationUrl + email;
            Log.d(TAG, "onClick GET_invitation: " + url);
            try {
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonArrayRequest Req = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "GET_invitation response: " + response);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                // get each search object
                                JSONObject responseObj = response.getJSONObject(i);

                                String sentEmail = responseObj.getString("email");

                                if (sentEmail.equals(peer.getEmail())) {
                                    // already set them an invite
                                    // make accept/decline, friend invisible
                                    holder.acceptButton.setVisibility(View.GONE);
                                    holder.declineButton.setVisibility(View.GONE);
                                    holder.friendButton.setVisibility(View.GONE);
                                    holder.unfriendButton.setVisibility(View.VISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse DELETE_invitation: " + error.toString());
                        data.remove(holder.getBindingAdapterPosition()); // pass by ref, so will update this.suggestions also
                        notifyItemRemoved(holder.getBindingAdapterPosition());
                        notifyItemRangeChanged(holder.getBindingAdapterPosition(), data.size());
                    }
                });
                requestQueue.add(Req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void declineInvite(ProfileObject po, ProfileCardVH holder) {

        }

        private void acceptInvite(ProfileObject peer, ProfileCardVH holder) {
            String url = peersUrl + email + "&target_peer_email=" + peer.getEmail();
            Log.d(TAG, "onClick POST_peer: " + url);
            try {
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST_peer response: " + response);
                        Toast.makeText(getContext(), "accepted invite from " + peer.getEmail(), Toast.LENGTH_LONG).show();

                        // TODO: creates a new chat for them
                        createChatroom(email, peer.getEmail(), holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse POST_peer: " + error.toString());
                        Toast.makeText(getContext(), "error: could accept invite from " + peer.getEmail(), Toast.LENGTH_LONG).show();

                    }
                });
                requestQueue.add(jsonObjReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void createChatroom(String myEmail, String peerEmail, ProfileCardVH holder) {
            String url = roomUrl + email + "&target_peer_email=" + peerEmail;
            Log.d(TAG, "onClick POST_create_room: " + url);
            try {
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JSONObject body = new JSONObject();
                body.put("name", peerEmail);

                JSONArray peerslist = new JSONArray();
                peerslist.put(myEmail);
                peerslist.put(peerEmail);
                body.put("peerslist", peerslist);

                JSONArray chathistory = new JSONArray();
                body.put("chathistory", chathistory);

                final String reqBody = body.toString();

                Log.d(TAG, "POST_create_room request body: " + body);

                //JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,body, new Response.Listener<JSONObject>() {
                StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "POST_create_room response: " + response);
                        Toast.makeText(getContext(), "created new chatroom with " + peerEmail, Toast.LENGTH_LONG).show();

                        data.remove(holder.getBindingAdapterPosition()); // pass by ref, so will update this.suggestions also
                        notifyItemRemoved(holder.getBindingAdapterPosition());
                        notifyItemRangeChanged(holder.getBindingAdapterPosition(), data.size());
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
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DELETE_invitation response: " + response);
                        Toast.makeText(getContext(), "redacted invitation to " + peer.getEmail(), Toast.LENGTH_LONG).show();

                        // make unfriendButton visible
                        holder.unfriendButton.setVisibility(View.GONE);
                        holder.friendButton.setVisibility(View.VISIBLE);
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
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST_invitation response: " + response);
                        Toast.makeText(getContext(), "sent invitation to " + peer.getEmail(), Toast.LENGTH_LONG).show();

                        // make unfriendButton visible
                        holder.unfriendButton.setVisibility(View.VISIBLE);
                        holder.friendButton.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse POST_invitation: " + error.toString());
                        Toast.makeText(getContext(), "error: could not send invite to" + peer.getEmail(), Toast.LENGTH_LONG).show();

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
                //TODO: make sure it works
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST_block response: " + response);
                        Toast.makeText(getContext(), "blocked " + peer.getEmail(), Toast.LENGTH_LONG).show();
                        data.remove(holder.getBindingAdapterPosition()); // pass by ref, so will update this.suggestions also
                        notifyItemRemoved(holder.getBindingAdapterPosition());
                        notifyItemRangeChanged(holder.getBindingAdapterPosition(), data.size());
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
            Button acceptButton;
            Button declineButton;
            Button blockButton;

            public ProfileCardVH(@NonNull View itemView) {
                super(itemView);

                peerName = itemView.findViewById(R.id.peer_name_text);
                friendButton = itemView.findViewById(R.id.friend_button);
                unfriendButton = itemView.findViewById(R.id.unfriend_button);
                acceptButton = itemView.findViewById(R.id.accept_button);
                declineButton = itemView.findViewById(R.id.decline_button);
                blockButton = itemView.findViewById(R.id.block_button);
            }
        }
    }
//////////////////////////////////////////////////////////// end profile card stuff ///////////////////////////////////////////////////////////
}
