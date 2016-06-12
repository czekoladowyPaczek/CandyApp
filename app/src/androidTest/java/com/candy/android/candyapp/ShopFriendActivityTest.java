package com.candy.android.candyapp;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.shop.ShopFriendActivity;
import com.candy.android.candyapp.shop.ShopFriendPresenter;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;
import com.candy.android.candyapp.testUtils.graph.FakeUserManagerModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by marcingawel on 11.06.2016.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShopFriendActivityTest {

    private ModelUser user;
    private ModelShop ownerList;
    private ModelShop userList;
    private List<ModelShopUser> users;

    private ShopManager shopManager;
    private UserManager userManager;

    private ShopFriendPresenter presenter;

    @Rule
    public ActivityTestRule<ShopFriendActivity> activityRule = new ActivityTestRule<>(ShopFriendActivity.class, true, false);

    @Before
    public void setupShopManager() {
        users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "name 1", ""));
        users.add(new ModelShopUser(2, "name 2", ""));
        ownerList = new ModelShop("1", users.get(0), users, "name list", Calendar.getInstance().getTime());
        userList = new ModelShop("2", users.get(1), users, "name list", Calendar.getInstance().getTime());

        shopManager = mock(ShopManager.class);
    }

    @Before
    public void setupUserManager() {
        user = new ModelUser(1, "user name", "", "user@user.com", new ArrayList<>());
        userManager = mock(UserManager.class);
        when(userManager.getUser()).thenReturn(user);
    }

    @Before
    public void setActivityComponent() {
        presenter = new ShopFriendPresenter(shopManager, userManager);
        ActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter))
                .fakeUserManagerModule(new FakeUserManagerModule(mock(UserManager.class)))
                .build();
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ((CandyApplication) instrumentation.getTargetContext().getApplicationContext()).setActivityComponent(component);
    }

    private void startActivity(ModelShop shop) {
        Intent intent = new Intent();
        intent.putExtra(ShopFriendActivity.SHOP, shop);
        activityRule.launchActivity(intent);
    }

    @Test
    public void assertViewsForOwner() {
        startActivity(ownerList);

        onView(withText(ownerList.getName())).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withText(users.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void assertViewForUser() {
        startActivity(userList);

        onView(withText(ownerList.getName())).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(not(isDisplayed())));
        onView(withText(users.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void shouldRefreshListAndKeepNewValuesAfterRecreate() {
        startActivity(userList);
        List<ModelShopUser> newUsers = new ArrayList<>(3);
        newUsers.addAll(userList.getUsers());
        newUsers.add(new ModelShopUser(4, "name 4", ""));
        ModelShop newShopList = new ModelShop(userList.getId(), newUsers.get(0), newUsers, "name new", Calendar.getInstance().getTime());
        when(shopManager.getShopList(userList.getId(), false)).thenReturn(Observable.just(newShopList));

        onView(withId(R.id.swipe_refresh)).perform(swipeDown());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> activityRule.getActivity().recreate());


        onView(withText(userList.getName())).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(not(isDisplayed())));
        onView(withText(newUsers.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(1).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(2).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void shouldResubscribeToPendingRefreshRequestAfterRecreate() {
        startActivity(userList);
        List<ModelShopUser> newUsers = new ArrayList<>(3);
        newUsers.addAll(userList.getUsers());
        newUsers.add(new ModelShopUser(4, "name 4", ""));
        ModelShop newShopList = new ModelShop(userList.getId(), newUsers.get(0), newUsers, "name new", Calendar.getInstance().getTime());
        PublishSubject<ModelShop> subject = PublishSubject.create();
        when(shopManager.getShopList(userList.getId(), false)).thenReturn(subject.asObservable());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            presenter.refreshData();
            activityRule.getActivity().recreate();
        });

        SystemClock.sleep(100);
        subject.onNext(newShopList);
        subject.onCompleted();

        onView(withText(userList.getName())).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(not(isDisplayed())));
        onView(withText(newUsers.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(1).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(2).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowErrorMessageAfterOnErrorFromRefresh() {
        startActivity(ownerList);

        when(shopManager.getShopList(ownerList.getId(), false)).thenReturn(Observable.error(ModelError.generateError(ModelError.INTERNET_CONNECTION)));

        onView(withId(R.id.swipe_refresh)).perform(swipeDown());

        onView(withText(ownerList.getName())).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withText(users.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
        onView(withText(R.string.error_connection)).check(matches(isDisplayed()));
    }
}