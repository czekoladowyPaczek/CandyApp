package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.testUtils.RxSchedulersOverrideRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class ShopDetailPresenterTest {
    private static final String ID = "asd";
    private ShopManager manager;
    private ShopDetailFragment fragment;
    private ShopDetailPresenter presenter;

    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Before
    public void setup() {
        manager = mock(ShopManager.class);
        fragment = mock(ShopDetailFragment.class);

        presenter = new ShopDetailPresenter(manager);
    }

    @Test
    public void setParent_shouldStartRequestOnFreshStart() throws Exception {
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(new ArrayList<>()));
        presenter.setParent(ID, fragment);

        verify(fragment).showListLoading(true);
        verify(manager).getShopItems(ID, true);
        verify(fragment).showListLoading(false);
        verify(fragment).setData(any(ArrayList.class));
    }

    @Test
    public void setParent_shouldResubscribeToPendingRequest() throws Exception {
        PublishSubject<List<ModelShopItem>> sub = PublishSubject.create();
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(sub.asObservable());

        presenter.setParent(ID, fragment);
        presenter.removeParent();
        presenter.setParent(ID, fragment);
        sub.onNext(new ArrayList<>());

        verify(fragment, times(2)).showListLoading(true);
        verify(manager).getShopItems(ID, true);
        verify(fragment).showListLoading(false);
        verify(fragment).setData(any(ArrayList.class));
    }

    @Test
    public void getShopListItems_shouldShowItemsOnSuccess() throws Exception {
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(new ArrayList<>()));
        presenter.setParent(ID, fragment);

        presenter.getShopListItems(true);

        verify(fragment).showListLoading(true);
        verify(manager, times(2)).getShopItems(ID, true);
        verify(fragment, times(2)).showListLoading(false);
        verify(fragment, times(2)).setData(any(ArrayList.class));
    }

    @Test
    public void getShopListItems_shouldShowErrorOnError() throws Exception {
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.error(new Throwable()));
        presenter.setParent(ID, fragment);

        presenter.getShopListItems(true);

        verify(fragment).showListLoading(true);
        verify(manager, times(2)).getShopItems(ID, true);
        verify(fragment, times(2)).showListLoading(false);
        verify(fragment, never()).setData(any(ArrayList.class));
        verify(fragment, times(2)).showError(anyInt());
    }
}