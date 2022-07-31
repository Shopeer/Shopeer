package espressotests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable;
import static com.adevinta.android.barista.assertion.BaristaImageViewAssertions.assertHasNoDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.adevinta.android.barista.interaction.PermissionGranter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shopeer.MainActivity;
import com.example.shopeer.R;
import com.example.shopeer.RoomObject;

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

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModifyProfilePicture {
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

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

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

    private String getPackageName() {
        RoomObject x = new RoomObject("a","a", "a", "a", 1);
        return x.getClass().getPackage().getName();
    }

    @Test
    public void C_acceptPermission() {
        //check if camera icon exist
        goToProfile();
        onView(withId(R.id.camera_imageView)).check(matches(isDisplayed()));
        assertHasNoDrawable(R.id.profilePic_imageView);

        //stubs the image picker
        intending(not(isInternal())).respondWith(stubImagePicker());

        //clicks camera icon
        onView(withId(R.id.camera_imageView)).perform(click());

        //Allow Permission
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.READ_EXTERNAL_STORAGE);

        onView(withId(R.id.profilePic_imageView)).check(matches(isDisplayed()));
        assertHasAnyDrawable(R.id.profilePic_imageView);
//        assertHasDrawable(R.id.profilePic_imageView, R.drawable.temp_profile);
    }

    private Instrumentation.ActivityResult stubImagePicker() {
        // Build the result to return when the activity is launched.
        Intent resultData = new Intent();
        Bundle bundle = new Bundle();
        ArrayList<Parcelable> parcels = new ArrayList<>();
//        Uri uri1 = Uri.parse("android.resource://" + getPackageName() + "/drawable/temp_profile.jpeg");
        Uri uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.temp_profile);
        Log.d(TAG, "temp profile URI: " + uri1);

        Parcelable parcelable1 = (Parcelable) uri1;
        parcels.add(parcelable1);
        bundle.putParcelableArrayList(Intent.EXTRA_STREAM, parcels);
        // Create the Intent that will include the bundle.
        resultData.putExtras(bundle);

        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }

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
        Intents.release();
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
