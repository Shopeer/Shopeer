package com.example.shopeer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NFRClickTest {
    final static String name = "NFRCTest";
    final static String TAG = "NFRC Test";
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

    @Test
    public void CreateSearchFromMatchPage() {
        int clicks = 0;

        // click on add search button
        onView(withId(R.id.add_search_button)).perform(click());
        clicks++;

        // set search name
        onView(withId(R.id.search_name_text)).perform(typeText("from match page"), closeSoftKeyboard());
        clicks++;

        // set activity
        onView(withId(R.id.activity_books_checkBox)).perform(click());
        clicks++;

        // save search
        onView(withId(R.id.save_search_button)).perform(click());
        clicks++;

        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("from match page"))));
        assert(clicks <= 5);
    }

    @Test
    public void CreateSearchFromProfilePage() {
        int clicks = 0;

        // navigate to position page
        onView(allOf(withId(R.id.profileFragment), withContentDescription("Profile"),
                childAtPosition(childAtPosition(withId(R.id.bottom_navigation_view), 0), 2),
                isDisplayed())).perform(click());

        // click to match page
        onView(allOf(withId(R.id.matchFragment), withContentDescription("Match"),
                childAtPosition(childAtPosition(withId(R.id.bottom_navigation_view), 0), 1),
                isDisplayed())).perform(click());
        clicks++;

        // click on add search button
        onView(withId(R.id.add_search_button)).perform(click());
        clicks++;

        // set search name
        onView(withId(R.id.search_name_text)).perform(typeText("from profile page"), closeSoftKeyboard());
        clicks++;

        // set activity
        onView(withId(R.id.activity_books_checkBox)).perform(click());
        clicks++;

        // save search
        onView(withId(R.id.save_search_button)).perform(click());
        clicks++;

        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("from profile page"))));
        assert(clicks <= 5);
    }

    @Test
    public void CreateSearchFromRoomsPage() {
        int clicks = 0;

        // navigate to position page
        onView(allOf(withId(R.id.roomsFragment), withContentDescription("Rooms"),
                childAtPosition(childAtPosition(withId(R.id.bottom_navigation_view), 0), 0),
                isDisplayed())).perform(click());

        // click to match page
        onView(allOf(withId(R.id.matchFragment), withContentDescription("Match"),
                childAtPosition(childAtPosition(withId(R.id.bottom_navigation_view), 0), 1),
                isDisplayed())).perform(click());

        // click on add search button
        onView(withId(R.id.add_search_button)).perform(click());
        clicks++;

        // set search name
        onView(withId(R.id.search_name_text)).perform(typeText("from rooms page"), closeSoftKeyboard());
        clicks++;

        // set activity
        onView(withId(R.id.activity_books_checkBox)).perform(click());
        clicks++;

        // save search
        onView(withId(R.id.save_search_button)).perform(click());
        clicks++;

        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("from rooms page"))));
        assert(clicks <= 5);
    }

    /////////////////////////////////// set up and clean up ////////////////////////////////////////
    @Before
    public void testSetup() {
        createUser(name);
    }

    @After
    public void testCleanup() {
        deleteUser(name);
    }

    ////////////////////////////////////////// helper functions /////////////////////////////////////
    private void createUser(String name) {
        // setup new user
        String url = profileUrl + name + emailAddr + "&name=" + name;
        Log.d(TAG, "POST_registration: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "GET_profile response: " + response);
                    //assertNotEquals(response.compareToIgnoreCase("user" + name + "already exists"), 0);
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
                    //assertEquals(response.compareToIgnoreCase("user deleted"), 0);
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
