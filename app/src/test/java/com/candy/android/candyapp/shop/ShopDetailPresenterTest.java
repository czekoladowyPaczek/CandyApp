package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.testUtils.RxSchedulersOverrideRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShopDetailPresenterTest {
    @Mock
    private ShopManager manager;
    @Mock
    private ShopDetailPresenter.ShopDetailFragmentContract fragmentContract;

    private ModelShop shop;
    private ShopDetailPresenter presenter;

    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Before
    public void setup() {
        shop = createShop();
        when(fragmentContract.getShopList()).thenReturn(shop);

        presenter = new ShopDetailPresenter(manager);
    }

    @Test
    public void setParent_shouldStartRequestOnFreshStart() throws Exception {
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(new ModelShopItem("", "", 1, ModelShopItem.PIECE, new Date()));
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(items));

        presenter.setParent(fragmentContract);

        InOrder fragmentOrder = inOrder(fragmentContract);

        fragmentOrder.verify(fragmentContract).showListLoading();
        verify(manager).getShopItems(shop.getId(), true);
        fragmentOrder.verify(fragmentContract).setShopItems(items);
        fragmentOrder.verify(fragmentContract).hideListLoading();
        verify(manager, never()).removeShopList(anyString());
    }

    @Test
    public void setParent_shouldSetItemsWithoutLoadingIfAlreadyFetched() {
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(new ModelShopItem("", "", 1, ModelShopItem.PIECE, new Date()));
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(items));

        presenter.setParent(fragmentContract);
        presenter.removeParent();
        presenter.setParent(fragmentContract);

        verify(manager, times(1)).getShopItems(shop.getId(), true);
        verify(fragmentContract, times(1)).showListLoading();
        verify(fragmentContract, times(2)).setShopItems(items);
    }

    @Test
    public void setParent_shouldNotSetItemsIfAlreadyFetchedAndRefreshInProgress() {
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(new ModelShopItem("", "", 1, ModelShopItem.PIECE, new Date()));
        when(manager.getShopItems(anyString(), anyBoolean()))
                .thenReturn(Observable.just(items))
                .thenReturn(Observable.never());

        presenter.setParent(fragmentContract);
        presenter.getShopListItems(true);
        presenter.removeParent();
        presenter.setParent(fragmentContract);

        verify(manager, times(2)).getShopItems(shop.getId(), true);
        verify(fragmentContract, times(2)).showListLoading();
        verify(fragmentContract).hideListLoading();
        verify(fragmentContract).setShopItems(any(items.getClass()));
    }

    @Test
    public void setParent_shouldNotSetItemsIfFirstFetchInProgress() {
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(new ModelShopItem("", "", 1, ModelShopItem.PIECE, new Date()));
        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.never());

        presenter.setParent(fragmentContract);
        presenter.removeParent();
        presenter.setParent(fragmentContract);

        verify(manager, times(1)).getShopItems(shop.getId(), true);
        verify(fragmentContract, times(2)).showListLoading();
        verify(fragmentContract, never()).hideListLoading();
        verify(fragmentContract, never()).setShopItems(any(items.getClass()));
    }

//    @Test
//    public void setParent_shouldResubscribeToPendingRequests() throws Exception {
//        PublishSubject<List<ModelShopItem>> sub = PublishSubject.create();
//        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(sub.asObservable());
//        PublishSubject<ModelResponseSimple> subRemove = PublishSubject.create();
//        when(manager.removeShopList(anyString())).thenReturn(subRemove.asObservable());
//
//        presenter.setParent(ID, fragment);
//        presenter.deleteList();
//        presenter.removeParent();
//        presenter.setParent(ID, fragment);
//        sub.onNext(new ArrayList<>());
//        subRemove.onNext(new ModelResponseSimple());
//
//        verify(fragment, times(2)).showListLoading(true);
//        verify(manager).getShopItems(ID, true);
//        verify(fragment).showListLoading(false);
//        verify(fragment).setData(any(ArrayList.class));
//        verify(manager).removeShopList(ID);
//        verify(fragment).showRemovingDialog();
//        verify(fragment).hideRemovingDialog();
//    }
//
//    @Test
//    public void setParent_shouldNotResubscribeToFinishedRequests() throws Exception {
//        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(new ArrayList<>()));
//        when(manager.removeShopList(anyString())).thenReturn(Observable.just(new ModelResponseSimple()));
//
//        presenter.setParent(ID, fragment);
//        presenter.deleteList();
//        presenter.removeParent();
//        presenter.setParent(ID, fragment);
//
//        verify(manager, times(2)).getShopItems(anyString(), anyBoolean());
//        verify(manager).removeShopList(anyString());
//    }
//
//    @Test
//    public void getShopListItems_shouldShowItemsOnSuccess() throws Exception {
//        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.just(new ArrayList<>()));
//        presenter.setParent(ID, fragment);
//
//        presenter.getShopListItems(true);
//
//        verify(fragment).showListLoading(true);
//        verify(manager, times(2)).getShopItems(ID, true);
//        verify(fragment, times(2)).showListLoading(false);
//        verify(fragment, times(2)).setData(any(ArrayList.class));
//    }
//
//    @Test
//    public void getShopListItems_shouldShowErrorOnError() throws Exception {
//        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.error(new Throwable()));
//        presenter.setParent(ID, fragment);
//
//        presenter.getShopListItems(true);
//
//        verify(fragment).showListLoading(true);
//        verify(manager, times(2)).getShopItems(ID, true);
//        verify(fragment, times(2)).showListLoading(false);
//        verify(fragment, never()).setData(any(ArrayList.class));
//        verify(fragment, times(2)).showError(anyInt());
//    }
//
//    @Test
//    public void deleteList_shouldCallParentOnSuccess() {
//        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.never());
//        when(manager.removeShopList(anyString())).thenReturn(Observable.just(new ModelResponseSimple()));
//        presenter.setParent(ID, fragment);
//
//        presenter.deleteList();
//
//        verify(manager).removeShopList(anyString());
//        verify(fragment).hideRemovingDialog();
//        verify(fragment).onListDeleted();
//    }
//
//    @Test
//    public void deleteList_shouldShowErrorOnError() {
//        when(manager.getShopItems(anyString(), anyBoolean())).thenReturn(Observable.never());
//        when(manager.removeShopList(anyString())).thenReturn(Observable.error(new Throwable()));
//        presenter.setParent(ID, fragment);
//
//        presenter.deleteList();
//
//        verify(manager).removeShopList(anyString());
//        verify(fragment).hideRemovingDialog();
//        verify(fragment, never()).onListDeleted();
//        verify(fragment).showError(anyInt());
//    }

    private static ModelShop createShop() {
        ModelShopUser owner = new ModelShopUser(1, "", "");
        List<ModelShopUser> users = new ArrayList<>(1);
        users.add(owner);
        return new ModelShop("test_id", owner, users, "test_name", new Date());
    }
}