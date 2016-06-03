package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.managers.ShopManager;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class ShopListPresenterTest {

    private ShopManager manager;
    private ShopListPresenter presenter;
    private ShopListFragment fragment;

    @Before
    public void setUp() throws Exception {
        fragment = mock(ShopListFragment.class);
        manager = mock(ShopManager.class);

        presenter = new ShopListPresenter(manager);
    }

    @Test
    public void testLifecycleWithPendingApiRequests() {
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(fragment);
        presenter.removeParent();
        presenter.setParent(fragment);

        verify(fragment, times(2)).showListLoading();
        verify(manager, times(1)).getShopLists(anyBoolean());
    }

    @Test
    public void testLifecycleWithoutPendingApiRequests() {
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(fragment);
        presenter.removeParent();

        verify(manager).getShopLists(true);
        verify(fragment).showListLoading();
    }

}