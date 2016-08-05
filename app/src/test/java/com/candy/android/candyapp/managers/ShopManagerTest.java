package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.api.request.RequestShopUser;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopItemTest;
import com.candy.android.candyapp.model.ModelShopTest;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.storage.ShopMemoryStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private ShopMemoryStorage memoryStorage;

    @Before
    public void setup() throws Exception {
        ShadowLog.stream = System.out;
        api = mock(CandyApi.class);
        userManager = mock(UserManager.class);
        when(userManager.getToken()).thenReturn(TOKEN);

//        memoryStorage = new ShopMemoryStorage();
        memoryStorage = mock(ShopMemoryStorage.class);

        manager = new ShopManager(userManager, api, memoryStorage);
    }

    @Test
    public void shouldClearCacheAfterLogout() {
        manager.logout();

        verify(memoryStorage).clearData();
    }

    @Test
    public void getShopLists_shouldCallApiAndSaveToCacheOnSuccessWhenCacheNotAvailable() {
        when(memoryStorage.getShops()).thenReturn(Observable.empty());
        ModelShop shop = ModelShopTest.getModelShop();
        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(shop);
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(true).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopLists("Bearer " + TOKEN);
        verify(memoryStorage).setShops(shops);
    }

    @Test
    public void getShopLists_shouldCallApiAndThrowErrorOnErrorWhenCacheNotAvailable() {
        when(memoryStorage.getShops()).thenReturn(Observable.empty());
        ModelShop shop = ModelShopTest.getModelShop();
        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(shop);
        when(api.getShopLists(anyString())).thenReturn(Observable.error(new Throwable()));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(true).subscribe(sub);

        sub.assertError(Throwable.class);
        verify(api).getShopLists("Bearer " + TOKEN);
        verify(memoryStorage, never()).setShops(any(List.class));
    }

    @Test
    public void getShopLists_shouldCallApiWhenCacheIsNotRequested() {
        when(memoryStorage.getShops()).thenReturn(Observable.just(new ArrayList<>()));
        ModelShop shop = ModelShopTest.getModelShop();
        List<ModelShop> shops = new ArrayList<>(1);
        shops.add(shop);
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        manager.getShopLists(false).subscribe(new TestSubscriber<>());

        sub.assertNoErrors();
        verify(api).getShopLists("Bearer " + TOKEN);
    }

    @Test
    public void createShopList_shouldCallApiAndSaveInCacheOnSuccess() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.createShopList(anyString(), any(RequestCreateShopList.class))).thenReturn(Observable.just(shop));
        ArgumentCaptor<RequestCreateShopList> captor = ArgumentCaptor.forClass(RequestCreateShopList.class);
        TestSubscriber<ModelShop> sub = new TestSubscriber<>();

        manager.createShopList("shop name").subscribe(sub);

        sub.assertNoErrors();
        verify(api).createShopList(eq("Bearer " + TOKEN), captor.capture());
        assertEquals("shop name", captor.getValue().getShopName());
        verify(memoryStorage).addShop(shop);
    }

    @Test
    public void createShopList_shouldCallApiAndNotSaveInCacheOnError() {
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.createShopList(anyString(), any(RequestCreateShopList.class))).thenReturn(Observable.error(new Throwable("")));
        ArgumentCaptor<RequestCreateShopList> captor = ArgumentCaptor.forClass(RequestCreateShopList.class);
        TestSubscriber<ModelShop> sub = new TestSubscriber<>();

        manager.createShopList("shop name").subscribe(sub);

        sub.assertError(Throwable.class);
        verify(api).createShopList(eq("Bearer " + TOKEN), captor.capture());
        assertEquals("shop name", captor.getValue().getShopName());
        verify(memoryStorage, never()).addShop(any(ModelShop.class));
    }

    @Test
    public void getShopItems_shouldReturnCachedItemsWhenAvailableAndCacheRequested() {
        List<ModelShopItem> items = new ArrayList<>();
        items.add(ModelShopItemTest.getModelShopItem());
        when(memoryStorage.getShopItems(anyString())).thenReturn(Observable.just(items));
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));

        TestSubscriber<List<ModelShopItem>> subOne = new TestSubscriber<>();
        manager.getShopItems("123", true).subscribe(subOne);

        subOne.assertNoErrors();
        verify(api, never()).getItems(anyString(), anyString());
    }

    @Test
    public void getShopItems_shouldReturnNotCachedItemsWhenAvailableButCacheNotRequested() {
        List<ModelShopItem> items = new ArrayList<>();
        items.add(ModelShopItemTest.getModelShopItem());
        when(memoryStorage.getShopItems(anyString())).thenReturn(Observable.just(items));
        when(userManager.getToken()).thenReturn(TOKEN);
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));

        TestSubscriber<List<ModelShopItem>> subTwo = new TestSubscriber<>();
        manager.getShopItems("123", false).subscribe(subTwo);

        subTwo.assertNoErrors();
        verify(api).getItems("Bearer " + TOKEN, "123");
    }

    @Test
    public void getShopItems_shouldRemoveShoppingListFromCacheIfItDoesNotExistOnServer() {
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.error(ModelError.generateError(ModelError.LIST_NOT_EXIST)));
        ModelShop shop = ModelShopTest.getModelShop();

        manager.getShopItems(shop.getId(), false).subscribe(new TestSubscriber<>());

        verify(memoryStorage).removeShop(shop.getId());
    }

    @Test
    public void removeShopList_shouldCallApiAndRemoveFromCacheIfAvailable() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(api.deleteShopList(anyString(), anyString())).thenReturn(Observable.just(new ModelResponseSimple()));

        manager.removeShopList(shop.getId()).subscribe(new TestSubscriber<>());

        verify(api).deleteShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage).removeShop(shop.getId());
    }

    @Test
    public void removeShopList_shouldCallApiAndRemoveFromCacheIfServerReturnNoListError() {
        ModelShop shop = ModelShopTest.getModelShop();
        HttpException e = ModelError.generateError(ModelError.LIST_NOT_EXIST);
        when(api.deleteShopList(anyString(), anyString())).thenReturn(Observable.error(e));

        manager.removeShopList(shop.getId()).subscribe(new TestSubscriber<>());

        verify(api).deleteShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage).removeShop(shop.getId());
    }

    @Test
    public void removeShopList_shouldCallApiAndNotRemoveFromCacheIfDifferentErrorOccurred() {
        ModelShop shop = ModelShopTest.getModelShop();
        HttpException e = ModelError.generateError(ModelError.NOT_PERMITTED);
        when(api.deleteShopList(anyString(), anyString())).thenReturn(Observable.error(e));

        manager.removeShopList(shop.getId()).subscribe(new TestSubscriber<>());

        verify(api).deleteShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage, never()).removeShop(anyString());
    }

    @Test
    public void getShopList_shouldCallApiWhenCacheNotRequested() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), false).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage, never()).getShop(anyString());
    }

    @Test
    public void getShopList_callApiAndSaveInCacheOnSuccessWhenNotAvailableInCache() {
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.empty());
        ModelShop shop = ModelShopTest.getModelShop();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), true).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage).addShop(shop);
    }

    @Test
    public void getShopList_returnErrorOnApiErrorAndNotRemoveFromCache() {
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.empty());
        ModelShop shop = ModelShopTest.getModelShop();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.error(ModelError.generateError(ModelError.UNKNOWN)));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), true).subscribe(sub);

        sub.assertError(Throwable.class);
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage, never()).addShop(shop);
        verify(memoryStorage, never()).removeShop(anyString());
    }

    @Test
    public void getShopList_shouldRemoveFromCacheOnApiListNotAvailableError() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.error(ModelError.generateError(ModelError.LIST_NOT_EXIST)));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), false).subscribe(sub);

        sub.assertError(Throwable.class);
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
        verify(memoryStorage).removeShop(shop.getId());
    }

    @Test
    public void getShopList_returnItemFromCacheIfAvailableInCache() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), true).subscribe(sub);

        sub.assertNoErrors();
        assertEquals(shop.getId(), sub.getOnNextEvents().get(0).getId());
        verify(api, never()).getShopList(anyString(), anyString());
        verify(memoryStorage).getShop(shop.getId());
    }

    @Test
    public void getShopList_returnItemFromApiIfNotAvailableFromCache() {
        ModelShop shop = ModelShopTest.getModelShop();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.empty());
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), true).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
    }

    @Test
    public void inviteToShop_shouldCallApiAndAddUserToList() {
        ModelShop shop = getShopId1();

        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));
        when(userManager.getUser()).thenReturn(getUser());
        when(api.inviteToList(anyString(), any(RequestShopUser.class))).thenReturn(Observable.just(null));

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop(shop.getId(), new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertNoErrors();
        ArgumentCaptor<RequestShopUser> argument = ArgumentCaptor.forClass(RequestShopUser.class);
        verify(api).inviteToList(eq("Bearer " + TOKEN), argument.capture());
        assertEquals(shop.getId(), argument.getValue().getShopId());
        assertEquals(3L, argument.getValue().getUserId());
        verify(memoryStorage).updateShop(shop);
    }

    @Test
    public void inviteToShop_shouldReturnErrorWhenUserIsNotOwner() {
        ModelShop shop = getShopId2();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));
        when(userManager.getUser()).thenReturn(getUser());

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop(shop.getId(), new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.NOT_PERMITTED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
        verify(memoryStorage, never()).updateShop(any(ModelShop.class));
    }

    @Test
    public void inviteToShop_shouldReturnErrorWhenUserAlreadyInvited() {
        ModelShop shop = getShopId1();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));
        when(userManager.getUser()).thenReturn(getUser());

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop(shop.getId(), new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.ALREADY_INVITED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
        verify(memoryStorage, never()).updateShop(any(ModelShop.class));
    }

    @Test
    public void inviteToShop_shouldReturnErrorWhenUserNotOnFriendList() {
        ModelShop shop = getShopId1();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));
        when(userManager.getUser()).thenReturn(getUser());

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop(shop.getId(), new ModelFriend(4, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.NOT_ON_FRIEND_LIST, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
        verify(memoryStorage, never()).updateShop(any(ModelShop.class));
    }

    @Test
    public void removeFromShop_shouldCallApiAndRemoveUserFromCache() {
        when(userManager.getUser()).thenReturn(getUser());
        ModelShop shop = getShopId1();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));
        when(api.removeFromList(anyString(), any(RequestShopUser.class))).thenReturn(Observable.just(null));

        TestSubscriber<ModelShopUser> sub = new TestSubscriber<>();
        manager.removeFromShop("1", new ModelShopUser(2, "", "")).subscribe(sub);

        sub.assertNoErrors();
        ArgumentCaptor<RequestShopUser> argument = ArgumentCaptor.forClass(RequestShopUser.class);
        verify(api).removeFromList(eq("Bearer " + TOKEN), argument.capture());
        assertEquals("1", argument.getValue().getShopId());
        assertEquals(2L, argument.getValue().getUserId());
        verify(memoryStorage).updateShop(shop);
    }

    @Test
    public void removeFromShop_shouldReturnErrorWhenUserIsNotOwner() {
        when(userManager.getUser()).thenReturn(getUser());
        ModelShop shop = getShopId2();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShopUser> sub = new TestSubscriber<>();
        manager.removeFromShop(shop.getId(), new ModelShopUser(2, "", "")).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.NOT_PERMITTED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).removeFromList(anyString(), any(RequestShopUser.class));
        verify(memoryStorage, never()).updateShop(any(ModelShop.class));
    }

    @Test
    public void removeFromShop_shouldReturnErrorWhenUserWasNotInvited() {
        when(userManager.getUser()).thenReturn(getUser());
        ModelShop shop = getShopId1();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShopUser> sub = new TestSubscriber<>();
        manager.removeFromShop(shop.getId(), new ModelShopUser(3, "", "")).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.USER_IS_NOT_INVITED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).removeFromList(anyString(), any(RequestShopUser.class));
        verify(memoryStorage, never()).updateShop(any(ModelShop.class));
    }

    @Test
    public void removeFromShop_shouldReturnErrorWhenUserWantsToRemoveOwner() {
        when(userManager.getUser()).thenReturn(getUser());
        ModelShop shop = getShopId1();
        when(memoryStorage.getShop(anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShopUser> sub = new TestSubscriber<>();
        manager.removeFromShop(shop.getId(), new ModelShopUser(1, "", "")).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.CANNOT_REMOVE_OWNER, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).removeFromList(anyString(), any(RequestShopUser.class));
        verify(memoryStorage, never()).updateShop(any(ModelShop.class));
    }

    private ModelUser getUser() {
        List<ModelFriend> friends = new ArrayList<>(2);
        friends.add(new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED));
        friends.add(new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED));
        friends.add(new ModelFriend(4, "", "", ModelFriend.STATUS_INVITED));
        return new ModelUser(1, "", "", "", friends);
    }

    private ModelShop getShopId1() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "name 1", ""));
        users.add(new ModelShopUser(2, "name 2", ""));
        return new ModelShop("1", users.get(0), users, "list 1", Calendar.getInstance().getTime());
    }

    private ModelShop getShopId2() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "name 1", ""));
        users.add(new ModelShopUser(2, "name 2", ""));
        return new ModelShop("2", users.get(1), users, "list 2", Calendar.getInstance().getTime());
    }
}