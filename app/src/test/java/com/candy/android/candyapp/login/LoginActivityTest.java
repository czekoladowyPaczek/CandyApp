package com.candy.android.candyapp.login;

import android.content.Intent;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by marcingawel on 29.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginActivityTest {

    LoginActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LoginActivity.class).create().get();
    }

    @Test
    public void shouldStartMainActivityOnLoginSuccess() throws Exception {
        ShadowActivity shadow = shadowOf(activity);

        activity.onLoginSuccess();

        Intent nextActivity = shadow.getNextStartedActivity();
        assertThat(nextActivity.getComponent().getClassName(), equalTo(MainActivity.class.getName()));
    }

}