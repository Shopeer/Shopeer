package com.example.shopeer;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import static java.lang.Thread.sleep;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BrowseManagePeersTest {

    final static String name = "BMPTest";
    final static String TAG = "BrowseManagePeers Test";
    final static String profileUrl = "http://20.230.148.126:8080/user/registration?email=";
    private static final String blockUrl = "http://20.230.148.126:8080/user/blocked?email=";
    private static final String invitationUrl = "http://20.230.148.126:8080/user/invitations?email=";

    final static String emailAddr = "@test.com";

    final static Context testContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("email", name + emailAddr);
        intent.putExtra("isBMPTest", true);
    }

    @BeforeClass
    public static void testSetup() {
        createUser(name);
        createUser("A");
        createUser("B");
        createUser("C");

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // B has blocked user
        blockPeer("B", name);

        // C has sent invite to user
        invitePeer("C", name);
    }

    private static void invitePeer(String name, String targetName) {
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
                    fail(name + " could not sent invite to " + targetName + " during setup: \n" + "onErrorResponse POST_block: " + error.toString());

                }
            });
            requestQueue.add(stringReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void blockPeer(String name, String targetName) {
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
                    fail(name + "could not block " + targetName + "during setup: \n" + "onErrorResponse POST_block: " + error.toString());
                }
            });
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createUser(String name) {
        //assertEquals("com.example.volleysampleforgithub", appContext.getPackageName());
        // setup new user
        String url = profileUrl + name + emailAddr + "&name=" + name;
        Log.d(TAG, "POST_registration: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "GET_profile response: " + response);
                    assertNotEquals(response.compareToIgnoreCase("user" + name + "already exists"), 0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse POST_registration: " + error.toString());
                    fail("Could not create new user during setup: \n" + "onErrorResponse POST_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    @Test // action 1
    public void A_addSearchButton_launchNewSearch() {
        Intents.init();
        onView(withId(R.id.add_search_button)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), EditSearchActivity.class)));
        Intents.release();

        onView(withId(R.id.search_name_text)).check(matches(withText("my search")));
    }

    @Test
    public void B_saveSearchButton_invalidActivity() {}


    //test spinner is on the right search
    /*
    https://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
     */


    @AfterClass
    public static void testCleanup() {
        deleteUser(name);
        deleteUser("A");
        deleteUser("B");
        deleteUser("C");

        // TODO: delete room created with C
    }

    private static void deleteUser(String name) {
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
                    fail("Could not delete user " + name + "during cleanup: \n" + "onErrorResponse DELETE_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
