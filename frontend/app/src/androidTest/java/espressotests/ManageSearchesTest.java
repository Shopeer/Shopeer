package espressotests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
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
import com.example.shopeer.MainActivity;
import com.example.shopeer.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
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

    private static View mainDecorView;

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("email", name + emailAddr);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    // cannot split each action of the test into separate test cases because app state is not
    // persistent across test cases (always a new activity starts on each test case)
    @Test // full test
    public void R_ManageSearchesTest_full(){
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

    /////////////////////////////////// "individual" step test case /////////////////////////////////
    // the lack of app state persistence means previous steps must be repeated, hence they are "accumulated"

    @Test // 1
    public void A_emptyMatchPage_accum() {
        A_emptyMatchPage();
    }

    @Test // 2
    public void B_createSearch_accum() {
        A_emptyMatchPage();
        B_createSearch();
    }

    @Test // 3
    public void C_invalidSaveNoActivitySet_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
    }

    @Test // 4
    public void D_validSave_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
    }

    @Test // 5
    public void E_createAnotherSearch_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
        E_createAnotherSearch();
    }

    @Test // 6
    public void F_invalidSaveDuplicateName_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
        E_createAnotherSearch();
        F_invalidSaveDuplicateName();
    }

    @Test // 7
    public void G_invalidSaveEmptyName_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
        E_createAnotherSearch();
        F_invalidSaveDuplicateName();
        G_invalidSaveEmptyName();
    }

    @Test // 8
    public void H_validSave_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
        E_createAnotherSearch();
        F_invalidSaveDuplicateName();
        G_invalidSaveEmptyName();
        H_validSave();
    }

    @Test // 9
    public void I_editSearch_accum() {
        A_emptyMatchPage();
        B_createSearch();
        C_invalidSaveNoActivitySet();
        D_validSave();
        E_createAnotherSearch();
        F_invalidSaveDuplicateName();
        G_invalidSaveEmptyName();
        H_validSave();
        I_editSearch();
    }

    @Test // 10
    public void J_editName_accum() {
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
    }

    @Test // 11
    public void K_editLocation_accum() {
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
    }

    @Test // 12
    public void L_invalidDistance_accum() {
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
    }

    @Test // 13
    public void M_invalidDistance_accum() {
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
    }

    @Test // 14
    public void N_invalidBudget_accum() {
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
    }

    @Test // 15
    public void O_validSave_accum() {
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
    }

    @Test // 16
    public void P_editSearch_accum() {
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
    }

    @Test // 17
    public void Q_deleteSearch_accum() {
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

    /////////////////////////////////// individual test step methods ///////////////////////////////

    //@Test // 1
    public void A_emptyMatchPage() {
        // edit search button disabled
        onView(withId(R.id.edit_search_button)).check(matches(not(isDisplayed())));

        // no profile cards showing
        onView(withId(R.id.profileCards)).check(doesNotExist());
    }

    //@Test // 2
    public void B_createSearch() {
        onView(withId(R.id.add_search_button)).perform(click());

        // default values for creating a new search
        onView(withId(R.id.search_name_text)).check(matches(withText("mySearch")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_entertainment_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_hiking_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_restaurants_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_fashion_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_books_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    //@Test // 3
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

    //@Test // 4
    public void D_validSave() {
        // select an activity and save
        onView(withId(R.id.activity_groceries_checkBox)).perform(click());
        onView(withId(R.id.save_search_button)).perform(click());

        // wait for page to load
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.edit_search_button)).check(matches(isDisplayed()));

        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("mySearch"))));

        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }

    //@Test // 5
    public void E_createAnotherSearch() {
        onView(withId(R.id.add_search_button)).perform(click());

        // default values for creating a new search
        onView(withId(R.id.search_name_text)).check(matches(withText("mySearch")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_entertainment_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_hiking_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_restaurants_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_fashion_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_books_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    //@Test // 6
    public void F_invalidSaveDuplicateName() {
        // select entertainment activity
        onView(withId(R.id.activity_entertainment_checkBox)).perform(click());

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

    //@Test // 7
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

    //@Test // 8
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

    //@Test // 9
    public void I_editSearch() {
        // select "mySearch"
        onView(withId(R.id.search_spinner)).perform(click());
        onData(hasToString("mySearch")).perform(click());
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("mySearch"))));

        // edit should show previous search values
        onView(withId(R.id.edit_search_button)).perform(click());

        onView(withId(R.id.search_name_text)).check(matches(withText("mySearch")));
        onView(withId(R.id.search_location_text)).check(matches(withText("North Pole")));
        onView(withId(R.id.distance_number)).check(matches(withText("10")));
        onView(withId(R.id.activity_bulkBuy_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_checkBox)).check(matches(isChecked()));
        onView(withId(R.id.activity_entertainment_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_hiking_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_restaurants_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_fashion_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_books_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    //@Test // 10
    public void J_editName() {
        onView(withId(R.id.search_name_text)).perform(replaceText("Costco"));
        onView(withId(R.id.search_name_text)).check(matches(withText("Costco")));
    }

    //@Test // 11
    public void K_editLocation() {
        onView(withId(R.id.autocomplete_fragment)).perform(click()) ;

        // Google Places autocomplete search bar
        final int PLACES_AUTOCOMPLETE_SEARCH_BAR_ID = 2131231133;
        final int PLACES_AUTOCOMPLETE_LIST_ID = 2131231126;

        onView(withId(PLACES_AUTOCOMPLETE_SEARCH_BAR_ID)).perform(typeTextIntoFocusedView("costco"), closeSoftKeyboard());
        onView(allOf(withId(PLACES_AUTOCOMPLETE_LIST_ID),
                childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        2))).perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.search_location_text)).check(matches(withText("Costco Wholesale")));
    }

    //@Test // 12
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

    //@Test // 13
    public void M_invalidDistance() {
        // negative distance range
        onView(withId(R.id.distance_number)).perform(replaceText(""));
        onView(withId(R.id.distance_number)).perform(typeText("-30"));

        // cannot input "-" into distance_number
        onView(withId(R.id.distance_number)).check(matches(withText("30")));
    }

    //@Test // 14
    public void N_invalidBudget() {
        onView(withId(R.id.budget_number)).perform(replaceText(""));
        onView(withId(R.id.budget_number)).perform(typeText("-80"), closeSoftKeyboard());

        // cannot input "-" into budget_number
        onView(withId(R.id.budget_number)).check(matches(withText("80")));
    }

    //@Test // 15
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
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("Costco"))));
        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }

    //@Test // 16
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
        onView(withId(R.id.activity_bulkBuy_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_groceries_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_entertainment_checkBox)).check(matches(isChecked()));
        onView(withId(R.id.activity_hiking_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_restaurants_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_fashion_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_books_checkBox)).check(matches(isNotChecked()));
        onView(withId(R.id.budget_number)).check(matches(withText("100")));
    }

    //@Test // 17
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

        // "Costco" is selected
        onView(withId(R.id.search_spinner)).check(matches(withSpinnerText(containsString("Costco"))));

        onView(withId(R.id.profileCards)).check(matches(isDisplayed()));
    }

    /////////////////////////////////// set up and clean up ////////////////////////////////////////
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

    @After
    public void testCleanup() {
        // delete user
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
