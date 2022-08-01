package espressotests;

import com.android.volley.toolbox.JsonArrayRequest;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import static java.lang.Thread.sleep;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shopeer.ChatActivity;
import com.example.shopeer.MainActivity;
import com.example.shopeer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BrowseManagePeersTest {
    final static String name = "BMPTest";
    final static String emailAddr = "@test.com";
    final static String TAG = "BrowseManagePeers Test";

    final static String profileUrl = "http://20.230.148.126:8080/user/registration?email=";
    private static final String blockUrl = "http://20.230.148.126:8080/user/blocked?email=";
    private static final String invitationUrl = "http://20.230.148.126:8080/user/invitations?email=";
    private static final String roomsUrl = "http://20.230.148.126:8080/chat/room";

    final espressotests.RecyclerViewUtil.RecyclerViewMatcher profileCards = espressotests.RecyclerViewUtil.withRecyclerView(R.id.profile_cards_rv);
    private static int swipe; // +1 right, -1 left

    final Context testContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("email", name + emailAddr);
        intent.putExtra("isBMPTest", true);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    // cannot split each action of the test into separate test cases because app state is not
    // persistent across test cases (always a new activity starts on each test case)
    @Test // full test
    public void L_BrowseManagePeerTest_full() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
        G_unblockA();
        H_swipeRightBProfileCardShowing();
        I_removeInviteToB();
        J_swipeRightCProfileCardShowing();
        K_matchWithC();
    }

    /////////////////////////////////// "individual" step test case /////////////////////////////////
    // the lack of app state persistence means previous steps must be repeated, hence they are "accumulated"

    @Test // 1
    public void A_registeredUserWithActiveSearch_accum() {
        A_registeredUserWithActiveSearch();
    }

    @Test // 2
    public void B_AProfileCardShowing_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
    }

    @Test // 3
    public void C_blockA_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
    }

    @Test // 4
    public void D_swipeRightBProfileCardShowing_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
    }

    @Test // 5
    public void E_sendInviteToB_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
    }

    @Test // 6
    public void F_swipeLeftAProfileCardShowing_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
    }

    @Test // 7
    public void G_unblockA_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
        G_unblockA();
    }

    @Test // 8
    public void H_swipeRightBProfileCardShowing_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
        G_unblockA();
        H_swipeRightBProfileCardShowing();
    }

    @Test // 9
    public void I_removeInviteToB_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
        G_unblockA();
        H_swipeRightBProfileCardShowing();
        I_removeInviteToB();
    }

    @Test // 10
    public void J_swipeRightCProfileCardShowing_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
        G_unblockA();
        H_swipeRightBProfileCardShowing();
        I_removeInviteToB();
        J_swipeRightCProfileCardShowing();
    }

    @Test // 11
    public void K_matchWithC_accum() {
        A_registeredUserWithActiveSearch();
        B_AProfileCardShowing();
        C_blockA();
        D_swipeRightBProfileCardShowing();
        E_sendInviteToB();
        F_swipeLeftAProfileCardShowing();
        G_unblockA();
        H_swipeRightBProfileCardShowing();
        I_removeInviteToB();
        J_swipeRightCProfileCardShowing();
        K_matchWithC();
    }

    /////////////////////////////////// individual test step methods ///////////////////////////////
    //@Test // 1
    public void A_registeredUserWithActiveSearch() {
        // spinner has a search
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("mySearch"))));
    }

    //@Test // 2
    public void B_AProfileCardShowing() {
        // A's pc is showing, friend and block enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("A")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("A's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 3
    public void C_blockA() {
        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).perform(click());

        // loading
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // A's pc is not showing since blocked, unblocked enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(not((isDisplayed()))));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("A")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(not((isDisplayed()))));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 4
    public void D_swipeRightBProfileCardShowing() {
        // swipe right
        swipe++;
        onView(withId(R.id.profile_cards_rv)).perform(scrollToPosition(swipe));

        // B's pc is showing, friend and block enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("B")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("B's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 5
    public void E_sendInviteToB() {
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).perform(click());

        // loading
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("B")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("B's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(isDisplayed()));
    }

    //@Test // 6
    public void F_swipeLeftAProfileCardShowing() {
        // swipe left
        swipe--;
        onView(withId(R.id.profile_cards_rv)).perform(scrollToPosition(swipe));

        // A's pc is not showing since blocked, unblocked enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("A")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(not(isDisplayed())));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 7
    public void G_unblockA() {
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).perform(click());

        // loading
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // A's pc is showing, friend and block enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("A")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("A's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 8
    public void H_swipeRightBProfileCardShowing() {
        // swipe right
        swipe++;
        onView(withId(R.id.profile_cards_rv)).perform(scrollToPosition(swipe));

        // B's pc is showing, only unfriend enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("B")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("B's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(isDisplayed()));
    }

    //@Test // 9
    public void I_removeInviteToB() {
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).perform(click());

        // loading
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // B's pc is showing, friend and block enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("B")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("B's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 10
    public void J_swipeRightCProfileCardShowing() {
        //swipe right
        swipe++;
        onView(withId(R.id.profile_cards_rv)).perform(scrollToPosition(swipe));

        // C's pc is showing, friend and block enabled
        onView(profileCards.atPositionOnView(swipe, R.id.peer_profile_photo)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_name_text)).check(matches(withText("C")));
        onView(profileCards.atPositionOnView(swipe, R.id.peer_description_text)).check(matches(withText("C's description")));

        onView(profileCards.atPositionOnView(swipe, R.id.block_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unblock_button)).check(matches(not(isDisplayed())));
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).check(matches(isDisplayed()));
        onView(profileCards.atPositionOnView(swipe, R.id.unfriend_button)).check(matches(not(isDisplayed())));
    }

    //@Test // 11
    public void K_matchWithC() {
        Intents.init();
        onView(profileCards.atPositionOnView(swipe, R.id.friend_button)).perform(click());

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(hasComponent(new ComponentName(getTargetContext(), ChatActivity.class)));
        Intents.release();
    }

 ////////////////////////////////// set up and clean up ///////////////////////////////////////////

    @Before
    public void testSetup() {
        createUser(name);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        createUser("A");

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createUser("B");

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createUser("C");
        swipe = 0;
    }

    @After
    public void testCleanup() {
        deleteUser(name);
        deleteUser("A");
        deleteUser("B");
        deleteUser("C");

        // possibly redundant
        deleteAllRooms("C");
    }

    ////////////////////////////////////// helper functions /////////////////////////////////////////

    private void invitePeer(String name, String targetName) {
        String url = invitationUrl + name + emailAddr + "&target_peer_email=" + targetName + emailAddr;
        Log.d(TAG, "onClick POST_invitation: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest stringReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // good job
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse POST_invitation: " + error.toString());
                    Log.e(TAG, name + " could not sent invite to " + targetName + " during setup: \n" + "onErrorResponse POST_block: " + error.toString());

                }
            });
            requestQueue.add(stringReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void blockPeer(String name, String targetName) {
        String url = blockUrl + name + emailAddr + "&target_peer_email=" + targetName + emailAddr;
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // good job
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, name + "could not block " + targetName + "during setup: \n" + "onErrorResponse POST_block: " + error.toString());
                }
            });
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUser(String user) {
        //assertEquals("com.example.volleysampleforgithub", appContext.getPackageName());
        // setup new user
        String url = profileUrl + user + emailAddr + "&name=" + user;
        Log.e(TAG, "POST_registration: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "GET_profile response: " + response);
                    assertNotEquals(response.compareToIgnoreCase("user" + user + "already exists"), 0);

                    if (user.compareToIgnoreCase("B") == 0) {
                        // B has blocked user
                        blockPeer("B", name);
                    }
                    else if (user.compareToIgnoreCase("C") == 0) {
                        // C has sent invite to user
                        invitePeer("C", name);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse POST_registration: " + error.toString());
                    Log.e(TAG, "Could not create new user during setup: \n" + "onErrorResponse POST_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAllRooms(String user) {
        ArrayList<String> ids = getRoomIds(user);
        for (String id : ids) {
            Log.e(TAG, "call to delete room: " + id);
            deleteRoom(id);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteRoom(String roomId) {
        if (roomId.isEmpty()){
            return;
        }
        String url = roomsUrl + "?room_id=" + roomId;
        Log.d(TAG, "DELETE_room: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "DELETE_room response: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse DELETE_room: " + error.toString());
                    Log.e(TAG, "Could not delete room " +  "during cleanup: \n" + "onErrorResponse DELETE_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getRoomIds(String user) {
        ArrayList<String> roomIds = new ArrayList<String>();

        String url = roomsUrl + "/all?email=" + user + emailAddr;
        Log.d(TAG, "GET all rooms: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "onResponse GET all rooms: " + response);
                    // extract search info from returned JSON Array of search objects
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject responseObj = response.getJSONObject(i);
                            String id = responseObj.getString("_id");
                            Log.d(TAG, "got room id: " + id);
                            roomIds.add(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse login: " + error.toString());
                }
            });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomIds;
    }

    private void deleteUser(String name) {
        // delete user
        String url = profileUrl + name + emailAddr;
        Log.d(TAG, "DELETE_registration: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "DELETE_registration response: " + response);
                    assertEquals(response.compareToIgnoreCase("user deleted"), 0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse DELETE_registration: " + error.toString());
                    Log.e(TAG, "Could not delete user " + name + "during cleanup: \n" + "onErrorResponse DELETE_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
