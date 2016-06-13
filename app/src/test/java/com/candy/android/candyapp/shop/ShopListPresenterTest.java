package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopTest;
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
public class ShopListPresenterTest {

    private ShopManager manager;
    private ShopListPresenter presenter;
    private ShopListFragment fragment;

    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        fragment = mock(ShopListFragment.class);
        manager = mock(ShopManager.class);

        presenter = new ShopListPresenter(manager);
    }

    @Test
    public void shouldCallGetShopsOnFreshStart() {
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.just(new ArrayList<>()));

        presenter.setParent(fragment);

        verify(fragment).showListLoading(true);
        verify(manager).getShopLists(true);
    }

    @Test
    public void shouldResubscribeToPendingRequestAfterRotation() {
        PublishSubject<List<ModelShop>> shopsSubject = PublishSubject.create();
        when(manager.getShopLists(anyBoolean())).thenReturn(shopsSubject);
        PublishSubject<ModelShop> createSubject = PublishSubject.create();
        when(manager.createShopList(anyString())).thenReturn(createSubject);

        presenter.setParent(fragment);
        presenter.createShopList("test");
        presenter.removeParent();
        presenter.setParent(fragment);
        shopsSubject.onNext(new ArrayList<>());
        createSubject.onNext(ModelShopTest.getModelShop());

        verify(fragment, times(2)).showListLoading(true);
        verify(fragment, times(2)).showLoadingDialog(R.string.shop_dialog_creating);
        verify(manager).getShopLists(anyBoolean());
        verify(manager).createShopList(anyString());
        verify(fragment).hideLoadingDialog();
        verify(fragment).showListLoading(false);
    }

    @Test
    public void shouldNotResubscribeToFinishedRequests() {
        PublishSubject<List<ModelShop>> shopsSubject = PublishSubject.create();
        when(manager.getShopLists(anyBoolean())).thenReturn(shopsSubject);
        PublishSubject<ModelShop> createSubject = PublishSubject.create();
        when(manager.createShopList(anyString())).thenReturn(createSubject);

        presenter.setParent(fragment);
        presenter.createShopList("test");
        shopsSubject.onNext(new ArrayList<>());
        createSubject.onNext(ModelShopTest.getModelShop());
        presenter.removeParent();
        presenter.setParent(fragment);

        verify(fragment, times(1)).showListLoading(true);
        verify(fragment).showLoadingDialog(R.string.shop_dialog_creating);
        verify(manager, times(2)).getShopLists(anyBoolean());
        verify(manager).createShopList(anyString());
        verify(fragment).hideLoadingDialog();
        verify(fragment).showListLoading(false);
    }

    @Test
    public void shouldNotResubscribeToFinishedWithErrorRequests() {
        PublishSubject<List<ModelShop>> shopsSubject = PublishSubject.create();
        when(manager.getShopLists(anyBoolean())).thenReturn(shopsSubject);
        PublishSubject<ModelShop> createSubject = PublishSubject.create();
        when(manager.createShopList(anyString())).thenReturn(createSubject);

        presenter.setParent(fragment);
        presenter.createShopList("test");
        shopsSubject.onError(new Throwable(""));
        createSubject.onError(new Throwable(""));
        presenter.removeParent();
        presenter.setParent(fragment);

        verify(fragment, times(1)).showListLoading(true);
        verify(fragment).showLoadingDialog(R.string.shop_dialog_creating);
        verify(manager, times(2)).getShopLists(anyBoolean());
        verify(manager).createShopList(anyString());
        verify(fragment).hideLoadingDialog();
        verify(fragment, times(2)).showListLoading(false); // PublishSubject will throw error immediately after subscribe
    }

    @Test
    public void shouldNotResubscribeToCancelledCreateRequest() {
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.never());
        PublishSubject<ModelShop> createSubject = PublishSubject.create();
        when(manager.createShopList(anyString())).thenReturn(createSubject);

        presenter.setParent(fragment);
        presenter.createShopList("test");
        presenter.cancelShopCreating();
        presenter.removeParent();
        presenter.setParent(fragment);
        createSubject.onNext(ModelShopTest.getModelShop());

        verify(manager).createShopList(anyString());
        verify(fragment).showLoadingDialog(R.string.shop_dialog_creating);
    }

    @Test
    public void shouldAddShopsToFragmentOnSuccess() {
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.just(new ArrayList<>()));

        presenter.setParent(fragment);

        verify(fragment).showListLoading(false);
        verify(fragment).setData(any(ArrayList.class));
    }

    @Test
    public void shouldShowErrorOnShopsError() {
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.error(new Throwable()));

        presenter.setParent(fragment);

        verify(fragment).showListLoading(false);
        verify(fragment, never()).setData(any(ArrayList.class));
        verify(fragment).showError(anyInt());
    }

    @Test
    public void shouldAddShopToFragmentOnSuccess() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.never());
        when(manager.createShopList(anyString())).thenReturn(Observable.just(shop));
        presenter.setParent(fragment);

        presenter.createShopList("name");

        verify(fragment).hideLoadingDialog();
        verify(fragment).addData(shop);
    }

    @Test
    public void shouldShowErrorOnShopCreateError() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(manager.getShopLists(anyBoolean())).thenReturn(Observable.never());
        when(manager.createShopList(anyString())).thenReturn(Observable.error(new Throwable()));
        presenter.setParent(fragment);

        presenter.createShopList("name");

        verify(fragment).hideLoadingDialog();
        verify(fragment, never()).addData(shop);
        verify(fragment).showError(anyInt());
    }
}