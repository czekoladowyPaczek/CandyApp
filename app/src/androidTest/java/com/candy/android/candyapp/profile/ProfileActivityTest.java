package com.candy.android.candyapp.profile;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;
import com.candy.android.candyapp.testUtils.graph.FakeUserManagerModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

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
public class ProfileActivityTest {

    private UserManager manager;

    @Rule
    public ActivityTestRule<ProfileActivity> activityRule = new ActivityTestRule<>(ProfileActivity.class, true, false);

    @Before
    public void setup() {
        manager = mock(UserManager.class);
        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakeUserManagerModule(new FakeUserManagerModule(manager))
                .fakePresenterModule(new FakePresenterModule())
                .build();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        CandyApplication app = (CandyApplication) instrumentation.getTargetContext().getApplicationContext();
        app.setActivityComponent(component);
    }

    @Test
    public void assertVisibleViews() {
        List<ModelFriend> friends = new ArrayList<>(10);
        for(int i = 0; i < friends.size(); i++) {
            friends.add(new ModelFriend(i , "Name " + i, "", ModelFriend.STATUS_ACCEPTED));
        }
        ModelUser user = new ModelUser(1, "name", "", "email", friends);

        activityRule.launchActivity(new Intent());
        activityRule.getActivity().runOnUiThread(() -> activityRule.getActivity().setUserData(user));

        onView(withText(user.getName())).check(matches(isDisplayed()));
        onView(withText(user.getEmail())).check(matches(isDisplayed()));
        onView(withId(R.id.user_profile_image)).check(matches(isDisplayed()));
        onView(withId(R.id.menu_logout)).check(matches(isDisplayed()));
    }
}