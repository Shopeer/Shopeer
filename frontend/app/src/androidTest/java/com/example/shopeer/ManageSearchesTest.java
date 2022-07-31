package com.example.shopeer;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManageSearchesTest {
    final static String name = "SMTest";
    final static String TAG = "ManageSearches Test";
    final static String profileUrl = "http://20.230.148.126:8080/user/registration?email=";

    final static String emailAddr = "@test.com";

    final static Context testContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("email", name + emailAddr);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    @Before
    public void testSetup() {
        createUser(name);
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

    @Test // 1
    public void A_emptyMatchPage() {


    }

    @Test // 2
    public void B_createSearch() {
        Intents.init();
        onView(withId(R.id.add_search_button)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), EditSearchActivity.class)));
        Intents.release();

        onView(withId(R.id.search_name_text)).check(matches(withText("my search")));


    }

    @Test // 3
    public void C_invalidSaveNoActivitySet() {}

    @Test // 4
    public void D_validSave() {}

    @Test // 5
    public void E_createAnotherSearch() {}

    @Test // 6
    public void F_invalidSaveDuplicateName() {}

    @Test // 7
    public void G_invalidSaveEmptyName() {}

    @Test // 8
    public void H_validSave() {}

    @Test // 9
    public void I_editSearch() {}

    @Test // 10
    public void J_editNameAndLocation() {
        onView(withId(R.id.autocomplete_fragment)).perform(click()) ;

        // Google Places autocomplete search bar
        final int PLACES_AUTOCOMPLETE_SEARCH_BAR_ID = 2131231128;
        final int PLACES_AUTOCOMPLETE_LIST_ID = 2131231121;

        onView(withId(PLACES_AUTOCOMPLETE_SEARCH_BAR_ID)).perform(typeTextIntoFocusedView("costco"), closeSoftKeyboard());
        //onView(withId(2131231128)).perform(pressImeActionButton());
        //onView(withId(2131231128)).perform(pressImeActionButton());
        onView(allOf(withId(PLACES_AUTOCOMPLETE_LIST_ID),
                childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        2))).perform(actionOnItemAtPosition(0, click()));


        onView(withId(R.id.search_location_text)).check(matches(withText("Costco Wholesale")));
    }

    @Test // 11
    public void K_invalidDistance() {
        //3000, then -30
    }

    @Test // 12
    public void L_invalidDistance() {
        //30
    }

    @Test // 13
    public void M_invalidBudget() {}

    @Test // 14
    public void N_validBudget() {}

    @Test // 15
    public void O_editSearch() {}

    @Test // 16
    public void P_deleteSearch() {}




    //test spinner is on the right search
    /*
    https://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
     */


    @After
    public void testCleanup() {
        // delete user
        deleteUser(name);
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

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
