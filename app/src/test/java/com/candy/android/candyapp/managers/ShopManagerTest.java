package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopItemTest;
import com.candy.android.candyapp.model.ModelShopTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
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

    @Test
    public void shouldClearCacheAfterLogout() {
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.getShopLists(anyString())).thenReturn(Observable.just(new ArrayList<>()));
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(new ArrayList<>()));

        manager.getShopLists(true);
        manager.getShopItems("123", true);

        manager.logout();
        manager.getShopLists(true);
        manager.getShopItems("123", true);

        verify(api, times(2)).getShopLists("Bearer " + TOKEN);
        verify(api, times(2)).getItems(eq("Bearer " + TOKEN), anyString());
    }

    @Test
    public void getShopItems_shouldReturnCachedItemsWhenAvailableAndCacheRequested() {
        List<ModelShopItem> items = new ArrayList<>();
        items.add(ModelShopItemTest.getModelShopItem());
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));

        TestSubscriber<List<ModelShopItem>> subOne = new TestSubscriber<>();
        TestSubscriber<List<ModelShopItem>> subTwo = new TestSubscriber<>();
        manager.getShopItems("123", true).subscribe(subOne);
        manager.getShopItems("123", true).subscribe(subTwo);

        subOne.assertNoErrors();
        subTwo.assertNoErrors();
        verify(api).getItems("Bearer " + TOKEN, "123");
    }

    @Test
    public void getShopItems_shouldReturnNotCachedItemsWhenAvailableButCacheNotRequested() {
        List<ModelShopItem> items = new ArrayList<>();
        items.add(ModelShopItemTest.getModelShopItem());
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));

        TestSubscriber<List<ModelShopItem>> subOne = new TestSubscriber<>();
        TestSubscriber<List<ModelShopItem>> subTwo = new TestSubscriber<>();
        manager.getShopItems("123", true).subscribe(subOne);
        manager.getShopItems("123", false).subscribe(subTwo);

        subOne.assertNoErrors();
        subTwo.assertNoErrors();
        verify(api, times(2)).getItems("Bearer " + TOKEN, "123");
    }

    @Test
    public void getShopItems_shouldRemoveShoppingListFromCacheIfItDoesNotExistOnServer() {
        List<ModelShop> shops = new ArrayList<>();
        shops.add(ModelShopTest.getModelShop());
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.error(getRetrofitException(32)));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(true).subscribe(new TestSubscriber<>());
        manager.getShopItems(ModelShopTest.getModelShop().getId(), true).subscribe(new TestSubscriber<>());
        manager.getShopLists(true).subscribe(sub);

        sub.assertNoErrors();
        assertEquals(0, sub.getOnNextEvents().get(0).size());
    }

    @Test
    public void removeShopList_shouldCallApiAndRemoveFromCacheIfAvailable() {
        List<ModelShop> shops = new ArrayList<>();
        shops.add(ModelShopTest.getModelShop());
        shops.add(new ModelShop("123", null, null, "name", null));
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(ModelShopItemTest.getModelShopItem());
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        when(api.deleteShopList(anyString(), anyString())).thenReturn(Observable.just(new ModelResponseSimple()));
        TestSubscriber<ModelResponseSimple> sub = new TestSubscriber<>();
        TestSubscriber<List<ModelShop>> shopSub = new TestSubscriber<>();
        TestSubscriber<List<ModelShopItem>> itemSub = new TestSubscriber<>();

        manager.getShopItems("123", false).subscribe(new TestSubscriber<>());
        manager.getShopLists(false).subscribe(new TestSubscriber<>());
        manager.removeShopList("123").subscribe(sub);
        manager.getShopItems("123", true).subscribe(itemSub);
        manager.getShopLists(true).subscribe(shopSub);


        verify(api).deleteShopList("Bearer " + TOKEN, "123");
        sub.assertNoErrors();
        verify(api, times(2)).getItems(anyString(), eq("123"));
        verify(api, times(1)).getShopLists(anyString());
        assertEquals(1, shopSub.getOnNextEvents().get(0).size());
    }

    @Test
    public void removeShopList_shouldCallApiAndRemoveFromCacheIfAvailableOnNoListError() {
        List<ModelShop> shops = new ArrayList<>();
        shops.add(ModelShopTest.getModelShop());
        shops.add(new ModelShop("123", null, null, "name", null));
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(ModelShopItemTest.getModelShopItem());
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        HttpException e = getRetrofitException(32);
        when(api.deleteShopList(anyString(), anyString())).thenReturn(Observable.error(e));
        TestSubscriber<ModelResponseSimple> sub = new TestSubscriber<>();
        TestSubscriber<List<ModelShop>> shopSub = new TestSubscriber<>();
        TestSubscriber<List<ModelShopItem>> itemSub = new TestSubscriber<>();

        manager.getShopItems("123", false).subscribe(new TestSubscriber<>());
        manager.getShopLists(false).subscribe(new TestSubscriber<>());
        manager.removeShopList("123").subscribe(sub);
        manager.getShopItems("123", true).subscribe(itemSub);
        manager.getShopLists(true).subscribe(shopSub);


        verify(api).deleteShopList("Bearer " + TOKEN, "123");
        sub.assertError(e);
        verify(api, times(2)).getItems(anyString(), eq("123"));
        verify(api, times(1)).getShopLists(anyString());
        assertEquals(1, shopSub.getOnNextEvents().get(0).size());
    }

    @Test
    public void removeShopList_shouldCallApiAndNotRemoveFromCacheIfDifferentErrorOccurred() {
        List<ModelShop> shops = new ArrayList<>();
        shops.add(ModelShopTest.getModelShop());
        shops.add(new ModelShop("123", null, null, "name", null));
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(ModelShopItemTest.getModelShopItem());
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        HttpException e = getRetrofitException(33);
        when(api.deleteShopList(anyString(), anyString())).thenReturn(Observable.error(e));
        TestSubscriber<ModelResponseSimple> sub = new TestSubscriber<>();
        TestSubscriber<List<ModelShop>> shopSub = new TestSubscriber<>();
        TestSubscriber<List<ModelShopItem>> itemSub = new TestSubscriber<>();

        manager.getShopItems("123", false).subscribe(new TestSubscriber<>());
        manager.getShopLists(false).subscribe(new TestSubscriber<>());
        manager.removeShopList("123").subscribe(sub);
        manager.getShopItems("123", true).subscribe(itemSub);
        manager.getShopLists(true).subscribe(shopSub);


        verify(api).deleteShopList("Bearer " + TOKEN, "123");
        sub.assertError(e);
        verify(api, times(1)).getItems(anyString(), eq("123"));
        verify(api, times(1)).getShopLists(anyString());
        assertEquals(2, shopSub.getOnNextEvents().get(0).size());
    }

    private HttpException getRetrofitException(int code) {
        return new HttpException(Response.error(500, ResponseBody.create(null, "{\"code\": " + code + ", \"message\":\"Error\"}")));
    }
}