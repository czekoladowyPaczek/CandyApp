package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class ShopManagerTest {

    private static final String TOKEN = "token";

    private CandyApi api;
    private UserManager userManager;
    private ShopManager manager;

    @Before
    public void setup() throws Exception {
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
        verify(api, times(1)).getShopLists("Bearer " + TOKEN);
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
        verify(api, times(2)).getShopLists("Bearer " + TOKEN);
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
        verify(api, times(2)).getShopLists("Bearer " + TOKEN);
    }

    @Test
    public void shouldCreateNewShopListAndSaveOnSuccessWhenNoRequestWasMadeBefore() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.createShopList(anyString(), any(RequestCreateShopList.class))).thenReturn(Observable.just(shop));
        ArgumentCaptor<RequestCreateShopList> captor = ArgumentCaptor.forClass(RequestCreateShopList.class);
        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        TestSubscriber<List<ModelShop>> shopsSub = new TestSubscriber<>();

        manager.createShopList("shop name").subscribe(sub);
        manager.getShopLists(true).subscribe(shopsSub);

        sub.assertNoErrors();
        verify(api).createShopList(eq("Bearer " + TOKEN), captor.capture());
        assertEquals("shop name", captor.getValue().getShopName());
        assertEquals(1, shopsSub.getOnNextEvents().get(0).size());
        assertEquals(shop.getName(), shopsSub.getOnNextEvents().get(0).get(0).getName());
    }

    @Test
    public void shouldCreateNewShopListAndSaveOnSuccessWhenRequestWasMadeBefore() {
        ModelShop shop = ModelShopTest.getModelShop();
        ArrayList<ModelShop> shops = new ArrayList<>(2);
        shops.add(new ModelShop());
        shops.add(new ModelShop());
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.createShopList(anyString(), any(RequestCreateShopList.class))).thenReturn(Observable.just(shop));
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        ArgumentCaptor<RequestCreateShopList> captor = ArgumentCaptor.forClass(RequestCreateShopList.class);
        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        TestSubscriber<List<ModelShop>> shopsSub = new TestSubscriber<>();

        manager.getShopLists(false).subscribe(new TestSubscriber<>());
        manager.createShopList("shop name").subscribe(sub);
        manager.getShopLists(true).subscribe(shopsSub);

        sub.assertNoErrors();
        verify(api).createShopList(eq("Bearer " + TOKEN), captor.capture());
        assertEquals("shop name", captor.getValue().getShopName());
        assertEquals(3, shopsSub.getOnNextEvents().get(0).size());
        assertEquals(shop.getName(), shopsSub.getOnNextEvents().get(0).get(2).getName());
    }

    @Test
    public void shouldCreateNewShopListAndNotSaveOnError() {
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.getShopLists(anyString())).thenReturn(Observable.just(new ArrayList<>()));
        when(api.createShopList(anyString(), any(RequestCreateShopList.class))).thenReturn(Observable.error(new Throwable("")));
        ArgumentCaptor<RequestCreateShopList> captor = ArgumentCaptor.forClass(RequestCreateShopList.class);
        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        TestSubscriber<List<ModelShop>> shopsSub = new TestSubscriber<>();

        manager.createShopList("shop name").subscribe(sub);
        manager.getShopLists(true).subscribe(shopsSub);

        sub.assertError(Throwable.class);
        verify(api).createShopList(eq("Bearer " + TOKEN), captor.capture());
        assertEquals("shop name", captor.getValue().getShopName());
        verify(api).getShopLists("Bearer " + TOKEN);
    }
}