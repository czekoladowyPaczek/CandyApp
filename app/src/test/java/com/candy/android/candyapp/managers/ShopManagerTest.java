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
import static org.mockito.Mockito.never;
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
    public void shouldCreateNewShopListAndSaveOnSuccessOnFirstPosition() {
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
        assertEquals(shop.getName(), shopsSub.getOnNextEvents().get(0).get(0).getName());
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
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.error(ModelError.generateError(ModelError.LIST_NOT_EXIST)));

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
        HttpException e = ModelError.generateError(ModelError.LIST_NOT_EXIST);
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
    public void getShopList_returnItemFromApiAndSaveInCacheOnSuccess() {
        ModelShop shop = ModelShopTest.getModelShop();
        insertShopListsToCache();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), true).subscribe(sub);
        manager.getShopList(shop.getId(), true).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
    }

    @Test
    public void getShopList_returnErrorOnApiErrorAndNotRemoveFromCache() {
        insertShopListsToCache();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.error(new Throwable()));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList("2", false).subscribe(sub);
        TestSubscriber<ModelShop> sub2 = new TestSubscriber<>();
        manager.getShopList("2", true).subscribe(sub2);

        sub.assertError(Throwable.class);
        verify(api, times(1)).getShopList(anyString(), anyString());
    }

    @Test
    public void getShopList_shouldRemoveFromCacheOnApiListNotAvailableError() {
        insertShopListsToCache();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.error(ModelError.generateError(ModelError.LIST_NOT_EXIST)));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList("2", false).subscribe(sub);
        TestSubscriber<ModelShop> sub2 = new TestSubscriber<>();
        manager.getShopList("2", true).subscribe(sub2);

        sub.assertError(Throwable.class);
        verify(api, times(2)).getShopList(anyString(), anyString());
    }

    @Test
    public void getShopList_returnItemFromApiIfCacheNotRequested() {
        insertShopListsToCache();
        ModelShop shop = ModelShopTest.getModelShop();
        insertShopListsToCache();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList("1", false).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopList("Bearer " + TOKEN, "1");
    }

    @Test
    public void getShopList_returnItemFromCacheIfAvailableInCache() {
        insertShopListsToCache();

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList("1", true).subscribe(sub);

        sub.assertNoErrors();
        assertEquals("1", sub.getOnNextEvents().get(0).getId());
        verify(api, never()).getShopList(anyString(), anyString());
    }

    @Test
    public void getShopList_returnItemFromApiIfNotAvailableFromCache() {
        ModelShop shop = ModelShopTest.getModelShop();
        insertShopListsToCache();
        when(api.getShopList(anyString(), anyString())).thenReturn(Observable.just(shop));

        TestSubscriber<ModelShop> sub = new TestSubscriber<>();
        manager.getShopList(shop.getId(), true).subscribe(sub);

        sub.assertNoErrors();
        verify(api).getShopList("Bearer " + TOKEN, shop.getId());
    }

    @Test
    public void inviteToShop_shouldCallApiAndAddUserToList() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();
        when(api.inviteToList(anyString(), any(RequestShopUser.class))).thenReturn(Observable.just(null));
        TestSubscriber<ModelShop> testShop = new TestSubscriber<>();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop("1", new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertNoErrors();
        ArgumentCaptor<RequestShopUser> argument = ArgumentCaptor.forClass(RequestShopUser.class);
        verify(api).inviteToList(eq("Bearer " + TOKEN), argument.capture());
        assertEquals("1", argument.getValue().getShopId());
        assertEquals(3L, argument.getValue().getUserId());
        manager.getShopList("1", true).subscribe(testShop);
        assertEquals(3, testShop.getOnNextEvents().get(0).getUsers().size());
    }

    @Test
    public void inviteToShop_shouldReturnErrorWhenUserIsNotOwner() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop("2", new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.NOT_PERMITTED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
    }

    @Test
    public void inviteToShop_shouldReturnErrorWhenUserAlreadyInvited() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop("1", new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.ALREADY_INVITED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
    }

    @Test
    public void inviteToShop_shouldReturnErrorWhenUserNotOnFriendList() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.inviteToShop("1", new ModelFriend(4, "", "", ModelFriend.STATUS_INVITED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.NOT_ON_FRIEND_LIST, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
    }

    @Test
    public void removeFromShop_shouldCallApiAndRemoveUserFromCache() {
        when(api.removeFromList(anyString(), any(RequestShopUser.class))).thenReturn(Observable.just(null));

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.removeFromShop("1", new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertNoErrors();
        ArgumentCaptor<RequestShopUser> argument = ArgumentCaptor.forClass(RequestShopUser.class);
        verify(api).removeFromList(eq("Bearer " + TOKEN), argument.capture());
        assertEquals("1", argument.getValue().getShopId());
        assertEquals(2L, argument.getValue().getUserId());
    }

    @Test
    public void removeFromShop_shouldReturnErrorWhenUserIsNotOwner() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.removeFromShop("2", new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.NOT_PERMITTED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
    }

    @Test
    public void removeFromShop_shouldReturnErrorWhenUserWasNotInvited() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.removeFromShop("1", new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.USER_IS_NOT_INVITED, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
    }

    @Test
    public void removeFromShop_shouldReturnErrorWhenUserWantsToRemoveOwner() {
        when(userManager.getUser()).thenReturn(getUser());
        insertShopListsToCache();

        TestSubscriber<Void> sub = new TestSubscriber<>();
        manager.removeFromShop("1", new ModelFriend(1, "", "", ModelFriend.STATUS_ACCEPTED)).subscribe(sub);

        sub.assertError(HttpException.class);
        assertEquals(ModelError.CANNOT_REMOVE_OWNER, ModelError.fromRetrofit(sub.getOnErrorEvents().get(0)));
        verify(api, never()).inviteToList(anyString(), any(RequestShopUser.class));
    }

    public void removeShopList_shouldCallApiAndNotRemoveFromCacheIfDifferentErrorOccurred() {
        List<ModelShop> shops = new ArrayList<>();
        shops.add(ModelShopTest.getModelShop());
        shops.add(new ModelShop("123", null, null, "name", null));
        List<ModelShopItem> items = new ArrayList<>(1);
        items.add(ModelShopItemTest.getModelShopItem());
        when(api.getItems(anyString(), anyString())).thenReturn(Observable.just(items));
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        HttpException e = ModelError.generateError(ModelError.NOT_PERMITTED);
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

    private ModelUser getUser() {
        List<ModelFriend> friends = new ArrayList<>(2);
        friends.add(new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED));
        friends.add(new ModelFriend(3, "", "", ModelFriend.STATUS_ACCEPTED));
        friends.add(new ModelFriend(4, "", "", ModelFriend.STATUS_INVITED));
        return new ModelUser(1, "", "", "", friends);
    }

    private void insertShopListsToCache() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "name 1", ""));
        users.add(new ModelShopUser(2, "name 2", ""));
        List<ModelShop> shops = new ArrayList<>(2);
        shops.add(new ModelShop("1", users.get(0), users, "list 1", Calendar.getInstance().getTime()));
        shops.add(new ModelShop("2", users.get(1), users, "list 2", Calendar.getInstance().getTime()));
        when(api.getShopLists(anyString())).thenReturn(Observable.just(shops));
        manager.getShopLists(false).subscribe(new TestSubscriber<>());
    }
}