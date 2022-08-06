package com.example.shopeer;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable;
import static com.adevinta.android.barista.assertion.BaristaImageViewAssertions.assertHasNoDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

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
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModifyProfileTest {
    final String email = "test@email.com";
    final String TAG = "ModifyProfile Test";
    final String profileUrl = "http://20.230.148.126:8080/user/registration?email=";

    final Context testContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("email", "test@email.com");
        intent.putExtra("isMPTest", true);
    }

    private View decorView;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void A_isProfileFragment() {
        // clicks on profile fragment
        goToProfile();

        // checks components of profile fragment
        onView(withId(R.id.profileName_textView)).check(matches(isDisplayed()));
        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.profileBio_textView)).check(matches(isDisplayed()));
        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));
    }

    @Test
    public void B_declinePermission() {
        goToProfile();

        //check if camera icon exist
        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));

        assertHasNoDrawable(R.id.profilePic_imageView);
        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));

        //clicks camera icon
        onView(withId(R.id.camera_imageView)).perform(click());

        //Deny Permission
        onView(withId(R.id.mock_camera_permission_deny_button)).perform(click());

        //check toast message
        onView(withText("Enable permissions to set photo"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        // wait for toast message to clear
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void C_acceptPermission() {
        goToProfile();

        //check if camera icon exist
        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));

        assertHasNoDrawable(R.id.profilePic_imageView);
        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));

        //clicks camera icon
        onView(withId(R.id.camera_imageView)).perform(click());

        //Allow Permission
        onView(withId(R.id.mock_camera_permission_allow_button)).perform(click());

        // check profile picture set
        assertHasAnyDrawable(R.id.profilePic_imageView);
        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));
    }

    @Test
    public void D_invalidName() {
        goToProfile();
        onView(withId(R.id.edit_imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_imageView)).perform(click());

        onView(withText("UPDATE")).check(matches(isDisplayed()));

        // Edits text
        onView(withId(R.id.profileName_input)).perform(clearText());
        onView(withId(R.id.profileName_input)).perform(typeText("|!?()"));
        closeSoftKeyboard();
        onView(withId(R.id.profileBio_input)).perform(clearText());
        onView(withId(R.id.profileBio_input)).perform(typeText("edited with espresso"));
        closeSoftKeyboard();

        onView(withId(R.id.updateProfileButton)).perform(click());

        //check toast message
        onView(withText("Invalid Name"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        // wait for toast message to clear
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void E_validName() {
        int numClicks = 0;
        goToProfile();
        numClicks ++;
        onView(withId(R.id.edit_imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_imageView)).perform(click());
        numClicks ++;

        onView(withText("UPDATE")).check(matches(isDisplayed()));

        // Edits text
        onView(withId(R.id.profileName_input)).perform(clearText());
        onView(withId(R.id.profileName_input)).perform(typeText("espresso"));
        closeSoftKeyboard();
        onView(withId(R.id.profileBio_input)).perform(clearText());
        onView(withId(R.id.profileBio_input)).perform(typeText("edited with espresso"));
        closeSoftKeyboard();

        onView(withId(R.id.updateProfileButton)).perform(click());
        numClicks ++;

        onView(withId(R.id.edit_imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.profileName_textView)).check(matches(withText("espresso")));
        onView(withId(R.id.profileBio_textView)).check(matches(withText("edited with espresso")));

        assertTrue("number of clicks must be <=5", numClicks <=5);
    }

    /////////////////////////////////// test set up and clean up ///////////////////////////////////
    @Before
    public void testSetup() {
        Intents.init();

        //setup to test Toast message
        activityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });

        // setup new user
        String url = profileUrl + email + "&name=Modify Profile Test";
        Log.d(TAG, "POST_registration: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "GET_profile response: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse POST_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void testCleanup() {
        // revoke permissions
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("pm revoke ${getTargetContext().packageName} android.permission.WRITE_EXTERNAL_STORAGE");

        // delete user
        String url = profileUrl + email;
        Log.d(TAG, "DELETE_registration: " + url);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(testContext);
            StringRequest jsonStrReq = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "DELETE_registration response: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse DELETE_registration: " + error.toString());
                }
            });
            requestQueue.add(jsonStrReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intents.release();
    }

    //////////////////////////////////// helper functions //////////////////////////////////////////
    private void goToProfile() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.profileFragment), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
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
