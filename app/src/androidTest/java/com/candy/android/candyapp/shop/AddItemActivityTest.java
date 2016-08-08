package com.candy.android.candyapp.shop;

import android.support.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import org.junit.Rule;

/**
 * @author Marcin
 */
public class AddItemActivityTest extends TestCase {

    @Rule
    public ActivityTestRule<AddItemActivity> activityRule = new ActivityTestRule<>(AddItemActivity.class, false, true);

}