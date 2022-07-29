package com.example.shopeer;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ModifyProfileTest {

    @Rule
    public ActivityScenarioRule<SplashScreen> mActivityScenarioRule =
            new ActivityScenarioRule<>(SplashScreen.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE");

    @Test
    public void modifyProfileTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.login_button), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.profileFragment), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation_view),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction imageView = onView(
                allOf(withId(R.id.camera_imageView),
                        withParent(withParent(withId(R.id.nav_fragment))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.camera_imageView),
                        withParent(withParent(withId(R.id.nav_fragment))),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.edit_imageView),
                        withParent(allOf(withId(R.id.constraintLayout2),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction imageView4 = onView(
                allOf(withId(R.id.edit_imageView),
                        withParent(allOf(withId(R.id.constraintLayout2),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView4.check(matches(isDisplayed()));

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.camera_imageView),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.camera_imageView),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction imageView5 = onView(
                allOf(withId(R.id.profilePic_imageView),
                        withParent(allOf(withId(R.id.cardView),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView5.check(matches(isDisplayed()));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.edit_imageView),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.profileName_input), withText("Dorithy\n"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.profileName_input), withText("Dorithy\n"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("||&^??\n"));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.profileName_input), withText("||&^??\n"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.profileBio_input), withText("test Android studio"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("new bio"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.profileBio_input), withText("new bio"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.profileName_input), withText("||&^??\n"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("||&^??"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.profileName_input), withText("||&^??"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.updateProfileButton), withText("Update"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.edit_imageView),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        appCompatImageView4.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.profileName_input), withText("||&^??"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText("new name"));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.profileName_input), withText("new name"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText9.perform(closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.updateProfileButton), withText("Update"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout2),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        materialButton3.perform(click());
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
