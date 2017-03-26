package com.candy.android.candyapp;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.candy.android.candyapp.login.LoginActivity;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.testUtils.MockCandyApplication;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;
import com.candy.android.candyapp.testUtils.graph.FakeUserManagerModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;

/**
 * @author Marcin
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setup() {
        UserManager manager = mock(UserManager.class);
        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakeUserManagerModule(new FakeUserManagerModule(manager))
                .fakePresenterModule(new FakePresenterModule())
                .build();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MockCandyApplication app = (MockCandyApplication) instrumentation.getTargetContext().getApplicationContext();
        app.setActivityComponent(component);

        activityRule.launchActivity(new Intent());
    }

    @Test
    public void viewsVisiblePortrait() {
        onView(withId(R.id.iconBig)).check(matches(isDisplayed()));
        onView(withText(R.string.login_welcome)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void viewsVisibleLandscape() {
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(withId(R.id.iconBig)).check(matches(isDisplayed()));
        onView(withText(R.string.login_welcome)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }
}