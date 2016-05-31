package com.candy.android.candyapp.profile;

import android.content.Intent;
import android.os.Bundle;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.graph.FakeActivityComponent;
import com.candy.android.candyapp.graph.FakePresenterModule;
import com.candy.android.candyapp.graph.FakeUserManagerModule;
import com.candy.android.candyapp.login.LoginActivity;
import com.candy.android.candyapp.managers.UserManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by marcingawel on 31.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProfileActivityTest {

    private ProfilePresenter presenter;
    private ActivityController<ProfileActivity> activityController;
    private ProfileActivity activity;

    @Before
    public void setup() {
        presenter = mock(ProfilePresenter.class);
        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakeUserManagerModule(new FakeUserManagerModule(mock(UserManager.class)))
                .fakePresenterModule(new FakePresenterModule(presenter))
                .build();
        ((CandyApplication) RuntimeEnvironment.application).setActivityComponent(component);
        activityController = Robolectric.buildActivity(ProfileActivity.class).create().start().resume().visible();
        activity = activityController.get();
    }

    @Test
    public void onSaveInstanceState() throws Exception {
        Bundle bundle = mock(Bundle.class);
        activity.onSaveInstanceState(bundle);

        verify(presenter).onSaveInstanceState(bundle);
    }

    @Test
    public void onLogoutClicked() throws Exception {
        ShadowActivity shadow = shadowOf(activity);

        RoboMenuItem item = new RoboMenuItem(R.id.menu_logout);
        activity.onOptionsItemSelected(item);

        Intent nextActivity = shadow.getNextStartedActivity();
        assertThat(nextActivity.getComponent().getClassName(), equalTo(LoginActivity.class.getName()));
        verify(presenter).logout();
    }

    @Test
    public void testLifecycle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        activityController.pause().stop().saveInstanceState(bundle).destroy();

        verify(presenter).setParent(eq(activity), any(Bundle.class));
        verify(presenter).onSaveInstanceState(bundle);
        verify(presenter).removeParent();
    }
}