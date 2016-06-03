package com.candy.android.candyapp;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.shop.ShopListPresenter;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Marcin
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setup() {
        ActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(mock(ShopListPresenter.class)))
                .build();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        CandyApplication app = (CandyApplication) instrumentation.getTargetContext().getApplicationContext();
        app.setActivityComponent(component);

        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void shouldCreateShopListFragmentOnStart() {
        assertNotNull(activityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_SHOP_LIST));
    }
}