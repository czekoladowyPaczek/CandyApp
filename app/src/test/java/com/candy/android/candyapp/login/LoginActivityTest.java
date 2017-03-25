package com.candy.android.candyapp.login;

import android.content.Intent;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.MainActivity;
import com.candy.android.candyapp.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.graph.FakeActivityComponent;
import com.candy.android.candyapp.graph.FakeManagerModule;
import com.candy.android.candyapp.graph.FakePresenterModule;
import com.candy.android.candyapp.graph.module.UtilModule;
import com.candy.android.candyapp.managers.UserManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginActivityTest {

    private LoginActivity activity;
    private UserManager userManager;

    @Before
    public void setup() {
        userManager = mock(UserManager.class);

        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakeManagerModule(new FakeManagerModule(userManager))
                .fakePresenterModule(new FakePresenterModule(mock(LoginPresenter.class)))
                .utilModule(new UtilModule(activity))
                .build();
        ((CandyApplication) RuntimeEnvironment.application).setActivityComponent(component);
    }

    @Test
    public void shouldStartMainActivityOnLoginSuccess() throws Exception {
        activity = Robolectric.buildActivity(LoginActivity.class).create().get();
        ShadowActivity shadow = shadowOf(activity);

        activity.onLoginSuccess();

        Intent nextActivity = shadow.getNextStartedActivity();
        assertThat(nextActivity.getComponent().getClassName(), equalTo(MainActivity.class.getName()));
    }

    @Test
    public void shouldStartMainActivityIfUserIsLoggedIn() {
        when(userManager.isLoggedIn()).thenReturn(true);
        activity = Robolectric.buildActivity(LoginActivity.class).create().get();
        ShadowActivity shadow = shadowOf(activity);

        Intent nextActivity = shadow.getNextStartedActivity();
        assertThat(nextActivity.getComponent().getClassName(), equalTo(MainActivity.class.getName()));
    }
}