package com.candy.android.candyapp.login;

import com.candy.android.candyapp.BuildConfig;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.*;

/**
 * Created by marcingawel on 25.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginFragmentTest {

    LoginFragment fragment;

    @Before
    public void setup() {
        fragment = new LoginFragment();
        SupportFragmentTestUtil.startFragment(fragment);
    }
}