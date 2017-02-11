package com.candy.android.candyapp.shop;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by marcingawel on 26.07.2016.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShopItemActivityTest {

    @Rule
    public ActivityTestRule<ShopItemActivity> activityTestRule = new ActivityTestRule<>(ShopItemActivity.class);

    @Before
    public void setUp() throws Exception {

    }

}