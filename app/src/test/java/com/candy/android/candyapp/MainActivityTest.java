package com.candy.android.candyapp;

import android.content.Intent;

import com.candy.android.candyapp.profile.ProfileActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Marcin
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private MainActivity activity;

    @Test
    public void shouldStartProfileActivity() {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        ShadowActivity shadow = shadowOf(activity);

        RoboMenuItem item = new RoboMenuItem(R.id.menu_profile);
        activity.onOptionsItemSelected(item);

        Intent nextActivity = shadow.getNextStartedActivity();
        assertThat(nextActivity.getComponent().getClassName(), equalTo(ProfileActivity.class.getName()));
    }
}