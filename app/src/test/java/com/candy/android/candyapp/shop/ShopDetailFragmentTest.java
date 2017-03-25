package com.candy.android.candyapp.shop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.graph.FakeActivityComponent;
import com.candy.android.candyapp.graph.FakeManagerModule;
import com.candy.android.candyapp.graph.FakePresenterModule;
import com.candy.android.candyapp.graph.module.UtilModule;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopItemTest;
import com.candy.android.candyapp.model.ModelShopTest;
import com.candy.android.candyapp.testUtils.DummyActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowProgressDialog;
import org.robolectric.shadows.support.v4.ShadowSwipeRefreshLayout;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Marcin
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShopDetailFragmentTest {
    private static final String ID = "123";
    private ShopDetailPresenter presenter;
    private ShopDetailFragment fragment;

    @Before
    public void setup() {
        presenter = mock(ShopDetailPresenter.class);

        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter))
                .fakeManagerModule(new FakeManagerModule(mock(ShopManager.class)))
                .utilModule(new UtilModule(RuntimeEnvironment.application))
                .build();
        ((CandyApplication) RuntimeEnvironment.application).setActivityComponent(component);

        fragment = new ShopDetailFragment();
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(ShopDetailFragment.LIST_ID, ModelShopTest.getModelShop());
        fragment.setArguments(bundle);
        SupportFragmentTestUtil.startVisibleFragment(fragment, DummyActivity.class, R.id.container);
    }

    @Test
    public void testLifecycle() {
        fragment.onDestroyView();

        assertTrue(fragment.getRetainInstance());
        verify(presenter).setParent(ModelShopTest.getModelShop().getId(), fragment);
        verify(presenter).removeParent();
    }

    @Test
    public void setData_shouldSetShops() {
        List<ModelShopItem> shops = new ArrayList<>(2);
        shops.add(ModelShopItemTest.getModelShopItem());
        shops.add(ModelShopItemTest.getModelShopItem());

        fragment.setData(shops);

        RecyclerView list = (RecyclerView) fragment.getView().findViewById(R.id.shop_detail_list);
        assertEquals(View.GONE, fragment.getView().findViewById(R.id.empty_layout).getVisibility());
        assertEquals(2, list.getAdapter().getItemCount());
    }

    @Test
    public void setData_showEmptyLayout() {
        List<ModelShopItem> shops = new ArrayList<>(2);
        shops.add(ModelShopItemTest.getModelShopItem());
        shops.add(ModelShopItemTest.getModelShopItem());
        fragment.setData(shops);
        fragment.setData(new ArrayList<>());

        RecyclerView list = (RecyclerView) fragment.getView().findViewById(R.id.shop_detail_list);
        assertEquals(View.VISIBLE, fragment.getView().findViewById(R.id.empty_layout).getVisibility());
        assertEquals(0, list.getAdapter().getItemCount());
    }

    @Test
    public void shouldCallPresenterOnSwipeRefresh() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) fragment.getView().findViewById(R.id.refreshLayout);
        ShadowSwipeRefreshLayout shadow = (ShadowSwipeRefreshLayout) shadowOf(refreshLayout);

        shadow.getOnRefreshListener().onRefresh();

        verify(presenter).getShopListItems(false);
    }

    @Test
    public void testShowListLoading() {
        fragment.showListLoading(true);
        SwipeRefreshLayout layout = (SwipeRefreshLayout) fragment.getView().findViewById(R.id.refreshLayout);
        assertTrue(layout.isRefreshing());
        fragment.showListLoading(false);
        assertFalse(layout.isRefreshing());
    }

    @Test
    public void shouldShowDialogAndRemoveList() {
        fragment.getView().findViewById(R.id.menu_delete).performClick();

        AlertDialog dialog = ShadowProgressDialog.getLatestAlertDialog();
        ShadowAlertDialog shadow = shadowOf(dialog);
        assertEquals(RuntimeEnvironment.application.getString(R.string.detail_deleting_list), shadow.getMessage());
        verify(presenter).deleteList();
    }
}