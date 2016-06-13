package com.candy.android.candyapp;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.shop.ShopDetailPresenter;
import com.candy.android.candyapp.shop.ShopListPresenter;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private ShopManager manager;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setup() {
        manager = mock(ShopManager.class);
        ShopListPresenter presenter = new ShopListPresenter(manager);
        ShopDetailPresenter detailPresenter = new ShopDetailPresenter(manager);

        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(new ModelShop("1", new ModelShopUser(), new ArrayList<>(), "name", Calendar.getInstance().getTime()));
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.just(shops));
        List<ModelShopItem> items = new ArrayList<>(1);
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(items));
        when(manager.removeShopList(anyString())).thenReturn(Observable.just(new ModelResponseSimple()));

        ActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter, detailPresenter))
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

    @Test
    public void shouldRecreate() {
        activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Test
    public void shouldSwitchToDetailFragmentAndRecreate() {
        switchToDetailView();

        onView(withId(R.id.shop_detail_list)).check(matches(isDisplayed()));

        activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Test
    public void shouldSwitchToDetailFragmentAndRecreateAfterPressBack() {
        switchToDetailView();

        onView(withId(R.id.shop_detail_list)).check(matches(isDisplayed()));
        pressBack();

        activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Test
    public void shouldSwitchToDetailFragmentAndRecreateAfterListDeleted() {
        switchToDetailView();

        onView(withId(R.id.shop_detail_list)).check(matches(isDisplayed()));
        onView(withId(R.id.menu_delete)).perform(click());
        onView(withId(R.id.shop_list)).check(matches(isDisplayed()));

        activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
//
//    @Test
//    public void shouldAddShoppingItem() {
//        when(manager.addShoppingItem(any(ModelShopItem.class)))
//                .thenReturn(Observable.just(new ModelShopItem("id", "cool name", 0.5, ModelShopItem.LITER, Calendar.getInstance().getTime())));
//
//        switchToDetailView();
//        onView(withId(R.id.create_shop_accept)).perform(click());
//        onView(withId(R.id.item_name)).perform(typeText("name"));
//        onView(withId(R.id.item_count)).perform(typeText("0.5"));
//        onView(withText(R.string.item_save)).perform(click());
//
//        onView(withText("cool name")).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void shouldDoNothingWhenUserCancelsItemAdding() {
//        when(manager.addShoppingItem(any(ModelShopItem.class)))
//                .thenReturn(Observable.just(new ModelShopItem("id", "cool name", 0.5, ModelShopItem.LITER, Calendar.getInstance().getTime())));
//        switchToDetailView();
//        onView(withId(R.id.create_shop_accept)).perform(click());
//        onView(withId(R.id.item_cancel)).perform(click());
//
//        onView(withId(R.id.shop_detail_list)).check(matches(isDisplayed()));
//        onView(withText("cool name")).check(matches(not(isDisplayed())));
//    }

    private void switchToDetailView() {
        onView(withId(R.id.shop_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}