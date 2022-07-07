package com.example.shopeer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFragment extends Fragment {

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
    RecyclerView rv;
    private ArrayList<SearchObject> searches; // list of active searches
    private LinearLayoutManager layoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
         */

        //



    }

    // do graphical stuff here, always called after onCreate
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_match, container, false);

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

//////////////////////////////////////////////////////////////////// start search stuff /////////////////////////////////////////////////////////////////////////


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