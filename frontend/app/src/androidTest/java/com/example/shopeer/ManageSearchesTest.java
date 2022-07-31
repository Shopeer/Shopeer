package com.example.shopeer;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import static java.lang.Thread.sleep;

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
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
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

    private static View mainDecorView;


    @Before
    public void testSetup() {
        createUser(name);

        //setup to test Toast message
        activityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                mainDecorView = activity.getWindow().getDecorView();
            }
        });
    }

    private void createUser(String name) {
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

    @Test
    public void ManageSearchesTest(){
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
        E_createAnotherSearch();
        F_invalidSaveDuplicateName();
        G_invalidSaveEmptyName();
        H_validSave();
        I_editSearch();
        J_editName();
        K_editLocation();
        L_invalidDistance();
        M_invalidDistance();
        N_invalidBudget();
        O_validSave();
        P_editSearch();
        Q_deleteSearch();
    }

    @Test // 1
    public void A_emptyMatchPage() {
        // edit search button disabled
        onView(withId(R.id.edit_search_button)).check(matches(not(isDisplayed())));

        // no profile cards showing
        onView(withId(R.id.profileCards)).check(doesNotExist());

        // TODO: some more testing?

    }

    @Test // 2
    public void B_createSearch() {
        onView(withId(R.id.add_search_button)).perform(click());

        // default values for creating a new search
        onView(withId(R.id.search_name_text)).check(matches(withText("my search")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_entertainment_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    @Test // 3
    public void C_invalidSaveNoActivitySet() {
        onView(withId(R.id.save_search_button)).perform(click());

        //check toast message
        onView(withText("choose at least one activity"))
                .inRoot(withDecorView(Matchers.not(mainDecorView)))
                .check(matches(isDisplayed()));

        // need to wait for toast message to clear for next test
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test // 4
    public void D_validSave() {
        // select an activity and save
        onView(withId(R.id.activity_groceries_radioButton)).perform(click());
        onView(withId(R.id.save_search_button)).perform(click());

        // wait for page to load
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.edit_search_button)).check(matches(isDisplayed()));

        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("my search"))));

        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }

    @Test // 5
    public void E_createAnotherSearch() {
        onView(withId(R.id.add_search_button)).perform(click());

        // default values for creating a new search
        onView(withId(R.id.search_name_text)).check(matches(withText("my search")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_entertainment_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    @Test // 6
    public void F_invalidSaveDuplicateName() {
        // select entertainment activity
        onView(withId(R.id.activity_entertainment_radioButton)).perform(click());

        onView(withId(R.id.save_search_button)).perform(click());

        //check toast message
        onView(withText("search with same name already exists"))
                .inRoot(withDecorView(Matchers.not(mainDecorView)))
                .check(matches(isDisplayed()));

        // need to wait for toast message to clear for next test
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test // 7
    public void G_invalidSaveEmptyName() {

        onView(withId(R.id.search_name_text)).perform(replaceText(""));
        onView(withId(R.id.save_search_button)).perform(click());

        //check toast message
        onView(withText("please set search name"))
                .inRoot(withDecorView(Matchers.not(mainDecorView)))
                .check(matches(isDisplayed()));

        // need to wait for toast message to clear for next test
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test // 8
    public void H_validSave() {
        onView(withId(R.id.search_name_text)).perform(replaceText("movies"));
        onView(withId(R.id.save_search_button)).perform(click());

        // wait for page to load
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // back on match page
        onView(withId(R.id.edit_search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("movies"))));
        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }

    @Test // 9
    public void I_editSearch() {
        // select "my search"
        onView(withId(R.id.search_spinner)).perform(click());
        onData(hasToString("my search")).perform(click());
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("my search"))));

        // edit should show previous search values
        onView(withId(R.id.edit_search_button)).perform(click());

        onView(withId(R.id.search_name_text)).check(matches(withText("my search")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_radioButton)).check(matches(isChecked()));
        onView(withId(R.id.activity_entertainment_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    @Test // 10
    public void J_editName() {
        onView(withId(R.id.search_name_text)).perform(replaceText("Costco fruits"));
        onView(withId(R.id.search_name_text)).check(matches(withText("Costco fruits")));
    }

    @Test // 11
    public void K_editLocation() {
        onView(withId(R.id.autocomplete_fragment)).perform(click()) ;

        // Google Places autocomplete search bar
        final int PLACES_AUTOCOMPLETE_SEARCH_BAR_ID = 2131231128;
        final int PLACES_AUTOCOMPLETE_LIST_ID = 2131231121;

        onView(withId(PLACES_AUTOCOMPLETE_SEARCH_BAR_ID)).perform(typeTextIntoFocusedView("costco"), closeSoftKeyboard());
        onView(allOf(withId(PLACES_AUTOCOMPLETE_LIST_ID),
                childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        2))).perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.search_location_text)).check(matches(withText("Costco Wholesale")));
    }

    @Test // 12
    public void L_invalidDistance() {
        // over max distance range
        onView(withId(R.id.distance_number)).perform(replaceText("300"));
        onView(withId(R.id.distance_number)).check(matches(withText("300")));

        onView(withId(R.id.save_search_button)).perform(click());

        //check toast message
        onView(withText("set distance range below 100 km"))
                .inRoot(withDecorView(Matchers.not(mainDecorView)))
                .check(matches(isDisplayed()));

        // need to wait for toast message to clear for next test
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test // 13
    public void M_invalidDistance() {
        // negative distance range
        onView(withId(R.id.distance_number)).perform(replaceText(""));
        onView(withId(R.id.distance_number)).perform(typeText("-30"));

        // cannot input "-" into distance_number
        onView(withId(R.id.distance_number)).check(matches(withText("30")));
    }

    @Test // 14
    public void N_invalidBudget() {
        onView(withId(R.id.budget_number)).perform(replaceText(""));
        onView(withId(R.id.budget_number)).perform(typeText("-80"));

        // cannot input "-" into budget_number
        onView(withId(R.id.budget_number)).check(matches(withText("80")));
    }

    @Test // 15
    public void O_validSave() {
        onView(withId(R.id.save_search_button)).perform(click());

        // wait for page to load
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // back on match page
        onView(withId(R.id.edit_search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("Costco fruits"))));
        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }

    @Test // 16
    public void P_editSearch() {
        // select "movies"
        onView(withId(R.id.search_spinner)).perform(click());
        onData(hasToString("movies")).perform(click());
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("movies"))));

        onView(withId(R.id.edit_search_button)).perform(click());

        // edit should show previous search values
        onView(withId(R.id.search_name_text)).check(matches(withText("movies")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_radioButton)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_entertainment_radioButton)).check(matches(isChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    @Test // 17
    public void Q_deleteSearch() {
        onView(withId(R.id.delete_search_button)).perform(click());

        // wait for page to load
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // back on match page
        onView(withId(R.id.edit_search_button)).check(matches(isDisplayed()));

        // "Costco fruits" is selected
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("Costco fruits"))));

        // "movies" search does not exist
        onData(hasToString("movies")).check(doesNotExist());

        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }




    //test spinner is on the right search
    /*
    https://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
     */


    @After
    public void testCleanup() {
        // delete user
        deleteUser(name);
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
