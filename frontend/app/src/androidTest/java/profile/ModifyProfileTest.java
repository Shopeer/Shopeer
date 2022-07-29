package profile;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import android.Manifest;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.adevinta.android.barista.interaction.PermissionGranter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shopeer.EditSearchActivity;
import com.example.shopeer.MainActivity;
import com.example.shopeer.R;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.material.navigation.NavigationView;

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
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("email", "test@email.com");
    }

    final String email = "test@gmail.com";
    final String TAG = "ModifyProfile Test";
    final String profileUrl = "http://20.230.148.126:8080/user/registration?email=";

    final Context testContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private View decorView;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    @Before
    public void testSetup() {
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
                    assertNotEquals(response.compareToIgnoreCase("user already exists"), 0);
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

//    @Test
//    public void A_isProfileFragment() {
//        // clicks on profile fragment
//        goToProfile();
//
//        // checks components of profile fragment
//        onView(withId(R.id.profileName_textView)).check(matches(isDisplayed()));
//        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));
//        onView(withId(R.id.profileBio_textView)).check(matches(isDisplayed()));
//        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void B_declinePermission() {
//        //check if camera icon exist
//        goToProfile();
//
//        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));
//        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));
//        //clicks camera icon
//        onView(withId(R.id.camera_imageView)).perform(click());
//        //Deny Permission
//        PermissionGranter.denyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE);
//        //check toast message
//        onView(withText("Enable permissions to set photo"))
//                .inRoot(withDecorView(not(decorView)))
//                .check(matches(isDisplayed()));
//    }

//    @Test
//    public void C_acceptPermission() {
//        //check if camera icon exist
//        goToProfile();
//
//        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));
//        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));
//        //clicks camera icon
//        onView(withId(R.id.camera_imageView)).perform(click());
//        //Deny Permission
//        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.READ_EXTERNAL_STORAGE);
//    }

    @Test
    public void D_editProfile() {
        goToProfile();
        onView(withId(R.id.edit_imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_imageView)).perform(click());

        onView(withText("UPDATE")).check(matches(isDisplayed()));

        // Edits text
        onView(withId(R.id.profileName_input)).perform(clearText());
        onView(withId(R.id.profileName_input)).perform(typeText("|!?()"));
        onView(withId(R.id.profileBio_input)).perform(clearText());
        onView(withId(R.id.profileBio_input)).perform(typeText("edited with espresso"));

        onView(withId(R.id.updateProfileButton)).perform(click());
        //check toast message
        onView(withText("Invalid Name"))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void E_invalidName() {
        // Edits text
        onView(withId(R.id.profileName_input)).perform(clearText());
        onView(withId(R.id.profileName_input)).perform(typeText("espresso"));
        onView(withId(R.id.profileBio_input)).perform(clearText());
        onView(withId(R.id.profileBio_input)).perform(typeText("edited with espresso"));

        onView(withId(R.id.updateProfileButton)).perform(click());
        onView(withId(R.id.edit_imageView)).check(matches(isDisplayed()));
    }
//
//    @Test
//    public void F_successfulChange() {
//
//    }

    @After
    public void testCleanup() {
        // delete user
        String url = profileUrl + email;
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
                    fail("Could not delete user during setup: \n" + "onErrorResponse DELETE_registration: " + error.toString());
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
