package com.candy.android.candyapp.shop;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.testUtils.MockCandyApplication;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;
import com.candy.android.candyapp.testUtils.graph.FakeUserManagerModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.subjects.PublishSubject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by marcingawel on 11.06.2016.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShopFriendActivityTest {
    private SubjectIdlingResource idlingResource;

    private ModelUser user;
    private ModelShop ownerList;
    private ModelShop notOwnerList;
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
        notOwnerList = new ModelShop("2", users.get(1), users, "name list", Calendar.getInstance().getTime());

        shopManager = Mockito.mock(ShopManager.class);
    }

    @Before
    public void setupUserManager() {
        user = new ModelUser(1, "user name", "", "user@user.com", new ArrayList<>());
        userManager = Mockito.mock(UserManager.class);
        when(userManager.getUser()).thenReturn(user);
    }

    @Before
    public void setActivityComponent() {
        presenter = new ShopFriendPresenter(shopManager, userManager);
        ActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter))
                .fakeUserManagerModule(new FakeUserManagerModule(Mockito.mock(UserManager.class)))
                .build();
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ((MockCandyApplication) instrumentation.getTargetContext().getApplicationContext()).setActivityComponent(component);
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
            idlingResource = null;
        }
    }

    private void startActivity(ModelShop shop) {
        Intent intent = new Intent();
        intent.putExtra(ShopFriendActivity.SHOP, shop);
        activityRule.launchActivity(intent);
    }

    @Test
    public void assertViewsForOwner() {
        startActivity(ownerList);

        onView(withText(R.string.users_title)).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withText(users.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void assertViewForUser() {
        startActivity(notOwnerList);

        onView(withText(R.string.users_title)).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).check(matches(not(isDisplayed())));
        onView(withText(users.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void shouldRefreshListAndKeepNewValuesAfterRecreate() {
        startActivity(notOwnerList);
        List<ModelShopUser> newUsers = new ArrayList<>(3);
        newUsers.addAll(notOwnerList.getUsers());
        newUsers.add(new ModelShopUser(4, "name 4", ""));
        ModelShop newShopList = new ModelShop(notOwnerList.getId(), newUsers.get(0), newUsers, "name new", Calendar.getInstance().getTime());
        when(shopManager.getShopList(notOwnerList.getId(), false)).thenReturn(Observable.just(newShopList));

        onView(withId(R.id.swipe_refresh)).perform(ViewActions.swipeDown());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> activityRule.getActivity().recreate());

        onView(withId(R.id.add_friend_button)).check(matches(not(isDisplayed())));
        onView(withText(newUsers.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(1).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(2).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void shouldResubscribeToPendingRefreshRequestAfterRecreate() {
        startActivity(notOwnerList);
        List<ModelShopUser> newUsers = new ArrayList<>(3);
        newUsers.addAll(notOwnerList.getUsers());
        newUsers.add(new ModelShopUser(4, "name 4", ""));
        ModelShop newShopList = new ModelShop(notOwnerList.getId(), newUsers.get(0), newUsers, "name new", Calendar.getInstance().getTime());
        PublishSubject<ModelShop> subject = PublishSubject.create();
        when(shopManager.getShopList(notOwnerList.getId(), false)).thenReturn(subject.asObservable());

        idlingResource = new SubjectIdlingResource(subject.asObservable());
        Espresso.registerIdlingResources(idlingResource);

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            presenter.refreshData();
            activityRule.getActivity().recreate();
        });

        Thread th = new Thread(() -> {
                try {
                    Thread.sleep(500); // let activity to be recreated before posting event
                } catch (InterruptedException ignored) {

                }
                subject.onNext(newShopList);
                subject.onCompleted();
        });
        th.run();

        onView(withText(newUsers.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(1).getName())).check(matches(isDisplayed()));
        onView(withText(newUsers.get(2).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowErrorMessageAfterOnErrorFromRefresh() {
        startActivity(ownerList);

        when(shopManager.getShopList(ownerList.getId(), false)).thenReturn(Observable.error(ModelError.generateError(ModelError.INTERNET_CONNECTION)));

        onView(withId(R.id.swipe_refresh)).perform(ViewActions.swipeDown());

        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withText(users.get(0).getName())).check(matches(isDisplayed()));
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
        onView(withText(R.string.error_connection)).check(matches(isDisplayed()));
    }

    @Test
    public void onListCLicked_shouldNotShowRemoveDialogAfterOnListClickIfUserIsNotListOwner() {
        startActivity(notOwnerList);

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText(getString(R.string.users_remove_title, notOwnerList.getUsers().get(0).getName()))).check(doesNotExist());
        onView(withText(R.string.users_error_remove_owner)).check(doesNotExist());
    }

    @Test
    public void onListClicked_shouldShowOwnerErrorOnOwnerClick() {
        startActivity(ownerList);

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText(R.string.users_error_remove_owner)).check(matches(isDisplayed()));
    }

    @Test
    public void onListClicked_shouldNotRemoveUserAfterDialogCancelled() {
        startActivity(ownerList);

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withText(getString(R.string.users_remove_title, ownerList.getUsers().get(1).getName()))).check(matches(isDisplayed()));
        onView(withText(android.R.string.cancel)).perform(click());
        onView(withText(getString(R.string.users_removing_user))).check(doesNotExist());
        onView(withText(users.get(1).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void onListClicked_shouldRemoveUserFromListOnSuccess() {
        startActivity(ownerList);
        when(shopManager.removeFromShop(anyString(), any(ModelShopUser.class))).thenReturn(Observable.just(ownerList.getUsers().get(1)));

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withText(getString(R.string.users_remove_title, ownerList.getUsers().get(1).getName()))).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());

        ArgumentCaptor<ModelShopUser> captor = ArgumentCaptor.forClass(ModelShopUser.class);
        verify(shopManager).removeFromShop(eq(ownerList.getId()), captor.capture());
        assertEquals(ownerList.getUsers().get(1).getId(), captor.getValue().getId());
        onView(withText(ownerList.getUsers().get(1).getName())).check(doesNotExist());
    }

    @Test
    public void onListClicked_shouldNotRemoveUserAndShowErrorOnError() {
        startActivity(ownerList);
        when(shopManager.removeFromShop(anyString(), any(ModelShopUser.class)))
                .thenReturn(Observable.error(ModelError.generateError(ModelError.INTERNET_CONNECTION)));

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withText(android.R.string.ok)).perform(click());

        ArgumentCaptor<ModelShopUser> captor = ArgumentCaptor.forClass(ModelShopUser.class);
        verify(shopManager).removeFromShop(eq(ownerList.getId()), captor.capture());
        assertEquals(ownerList.getUsers().get(1).getId(), captor.getValue().getId());
        onView(withText(ownerList.getUsers().get(1).getName())).check(matches(isDisplayed()));
    }

    @Test
    public void onListClicked_shouldKeepDialogAfterRecreate() {
        startActivity(ownerList);

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withText(getString(R.string.users_remove_title, ownerList.getUsers().get(1).getName()))).check(matches(isDisplayed()));

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> activityRule.getActivity().recreate());

        onView(withText(getString(R.string.users_remove_title, ownerList.getUsers().get(1).getName()))).check(matches(isDisplayed()));
    }

    @Test
    public void onListClicked_shouldResubscribeToPendingDeleteRequestAfterResubscribe() {
        startActivity(ownerList);
        PublishSubject<ModelShopUser> subject = PublishSubject.create();
        when(shopManager.removeFromShop(anyString(), any(ModelShopUser.class))).thenReturn(subject.asObservable());

        onView(withId(R.id.shop_users_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withText(android.R.string.ok)).perform(click());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> activityRule.getActivity().recreate());
        onView(withText(getString(R.string.users_removing_user))).check(matches(isDisplayed()));
        idlingResource = new SubjectIdlingResource(subject.asObservable());
        Espresso.registerIdlingResources(idlingResource);
        subject.onNext(ownerList.getUsers().get(1));

        onView(withText(ownerList.getUsers().get(1).getName())).check(doesNotExist());
    }

    private String getString(int resId, Object... objects) {
        return activityRule.getActivity().getString(resId, objects);
    }

    public static class SubjectIdlingResource implements IdlingResource {

        private AtomicInteger counter;
        private ResourceCallback callback;

        public SubjectIdlingResource(Observable obs) {
            counter = new AtomicInteger(0);
            counter.incrementAndGet();

            obs.subscribe(success -> {
                counter.decrementAndGet();
            }, error -> {
                counter.decrementAndGet();
            });
        }

        @Override
        public String getName() {
            return SubjectIdlingResource.class.getName();
        }

        @Override
        public boolean isIdleNow() {
            boolean idle = counter.get() == 0;
            if (idle && callback != null)
                callback.onTransitionToIdle();
            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
        }
    }
}