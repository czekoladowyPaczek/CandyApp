package com.candy.android.candyapp.shop;

import android.app.AlertDialog;
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
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopTest;
import com.candy.android.candyapp.testUtils.DummyActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by marcingawel on 03.06.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShopListFragmentTest {

    private ShopListPresenter presenter;
    private ShopListFragment fragment;

    @Before
    public void setup() {
        presenter = mock(ShopListPresenter.class);

        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter))
                .fakeManagerModule(new FakeManagerModule(mock(ShopManager.class)))
                .build();
        ((CandyApplication) RuntimeEnvironment.application).setActivityComponent(component);

        fragment = new ShopListFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment, DummyActivity.class, R.id.container);
    }

    @Test
    public void testRecreate() {
        fragment.getActivity().recreate();

        verify(presenter, times(2)).setParent(fragment);
        verify(presenter).removeParent();
        assertEquals(View.GONE, fragment.getView().findViewById(R.id.create_shop_list_layout).getVisibility());
    }

    @Test
    public void testRecreateWithShopCreatingDialog() {
        fragment.showLoadingDialog(R.string.shop_dialog_creating);

        fragment.getActivity().recreate();

        verify(presenter, never()).cancelShopCreating();
    }

    @Test
    public void testRecreateWithAddShopViewVisible() {
        fragment.getView().findViewById(R.id.createShopButton).performClick();
        fragment.getActivity().recreate();

        verify(presenter, times(2)).setParent(fragment);
        verify(presenter).removeParent();
        assertEquals(View.VISIBLE, fragment.getView().findViewById(R.id.create_shop_list_layout).getVisibility());
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
    public void testShowLoadingDialog() {
        fragment.showLoadingDialog(R.string.shop_dialog_creating);

        AlertDialog dialog = ShadowProgressDialog.getLatestAlertDialog();
        ShadowAlertDialog d = shadowOf(dialog);
        assertEquals(RuntimeEnvironment.application.getString(R.string.shop_dialog_creating), d.getMessage());

        dialog.cancel();
        verify(presenter).cancelShopCreating();
    }

    @Test
    public void shouldCallPresenterOnSwipeRefresh() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) fragment.getView().findViewById(R.id.refreshLayout);
        ShadowSwipeRefreshLayout shadow = (ShadowSwipeRefreshLayout) shadowOf(refreshLayout);

        shadow.getOnRefreshListener().onRefresh();

        verify(presenter).getShopLists(false);
    }

    @Test
    public void setData_shouldSetShops() {
        List<ModelShop> shops = new ArrayList<>(2);
        shops.add(ModelShopTest.getModelShop());
        shops.add(ModelShopTest.getModelShop());

        fragment.setData(shops);

        RecyclerView list = (RecyclerView) fragment.getView().findViewById(R.id.shop_list);
        assertEquals(View.GONE, fragment.getView().findViewById(R.id.empty_layout).getVisibility());
        assertEquals(2, list.getAdapter().getItemCount());
    }

    @Test
    public void setData_showEmptyLayout() {
        List<ModelShop> shops = new ArrayList<>(2);
        shops.add(ModelShopTest.getModelShop());
        shops.add(ModelShopTest.getModelShop());
        fragment.setData(shops);
        fragment.setData(new ArrayList<>());

        RecyclerView list = (RecyclerView) fragment.getView().findViewById(R.id.shop_list);
        assertEquals(View.VISIBLE, fragment.getView().findViewById(R.id.empty_layout).getVisibility());
        assertEquals(0, list.getAdapter().getItemCount());
    }

    @Test
    public void addData_shouldAddModelToNotEmptyList() {
        List<ModelShop> shops = new ArrayList<>(2);
        shops.add(ModelShopTest.getModelShop());
        shops.add(ModelShopTest.getModelShop());
        fragment.setData(shops);
        fragment.addData(ModelShopTest.getModelShop());

        RecyclerView list = (RecyclerView) fragment.getView().findViewById(R.id.shop_list);
        assertEquals(View.GONE, fragment.getView().findViewById(R.id.empty_layout).getVisibility());
        assertEquals(3, list.getAdapter().getItemCount());
    }

    @Test
    public void addData_shouldAddModelToEmptyList() {
        fragment.setData(new ArrayList<>());
        fragment.addData(ModelShopTest.getModelShop());

        RecyclerView list = (RecyclerView) fragment.getView().findViewById(R.id.shop_list);
        assertEquals(View.GONE, fragment.getView().findViewById(R.id.empty_layout).getVisibility());
        assertEquals(1, list.getAdapter().getItemCount());
    }
}