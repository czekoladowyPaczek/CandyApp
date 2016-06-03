package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShopManagerTest {

    private static final String TOKEN = "token";

    private CandyApi api;
    private UserManager userManager;
    private ShopManager manager;

    @Before
    public void setup() throws Exception {
        ShadowLog.stream = System.out;
        api = mock(CandyApi.class);
        userManager = mock(UserManager.class);
        when(userManager.getToken()).thenReturn(TOKEN);

        manager = new ShopManager(userManager, api);
    }

    @Test
    public void shouldCallGetShopsAndSaveShopListOnSuccess() {
        ModelShop shop = ModelShopTest.getModelShop();
        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(shop);
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(true).subscribe(sub);
        manager.getShopLists(true).subscribe(new TestSubscriber<>());

        sub.assertNoErrors();
        verify(api, times(1)).getShopLists(TOKEN);
    }

    @Test
    public void shouldCallGetShopsAndThrowErrorOnError() {
        ModelShop shop = ModelShopTest.getModelShop();
        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(shop);
        when(api.getShopLists(anyString())).thenReturn(Observable.error(new Throwable()));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(true).subscribe(sub);
        manager.getShopLists(true).subscribe(new TestSubscriber<>());

        sub.assertError(Throwable.class);
        verify(api, times(2)).getShopLists(TOKEN);
    }

    @Test
    public void shouldCallGetShopsWhenCacheIsNotRequested() {
        ModelShop shop = ModelShopTest.getModelShop();
        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(shop);
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(true).subscribe(sub);
        manager.getShopLists(false).subscribe(new TestSubscriber<>());

        sub.assertNoErrors();
        verify(api, times(2)).getShopLists(TOKEN);
    }
}