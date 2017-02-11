package com.candy.android.candyapp.storage;

import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopUser;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * @author Marcin
 */
public class ShopMemoryStorageTest {

    private ShopMemoryStorage storage;

    @Before
    public void setUp() {
        storage = new ShopMemoryStorage();
    }

    @Test
    public void getShops_shouldNotReturnDataWhenShopsNotAvailable() {
        TestSubscriber<List<ModelShop>> subscriber = new TestSubscriber<>();

        storage.getShops().subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertNoValues();
    }

    @Test
    public void getShops_shouldReturnAllShopsAndOverrideAllPreviousShops() {
        List<ModelShop> shops = getTestShops();
        storage.setShops(shops);

        TestSubscriber<List<ModelShop>> subscriber = new TestSubscriber<>();
        storage.getShops().subscribe(subscriber);

        subscriber.assertNoErrors();
        assertEquals(shops.size(), subscriber.getOnNextEvents().get(0).size());
        assertEquals(shops.get(0).getId(), subscriber.getOnNextEvents().get(0).get(0).getId());

        shops.clear();
        shops.add(new ModelShop("10", new ModelShopUser(1, "", ""), new ArrayList<>(), "", Calendar.getInstance().getTime()));
        storage.setShops(shops);

        TestSubscriber<List<ModelShop>> newSub = new TestSubscriber<>();
        storage.getShops().subscribe(newSub);
        assertEquals(shops.size(), subscriber.getOnNextEvents().get(0).size());
        assertEquals(shops.get(0).getId(), subscriber.getOnNextEvents().get(0).get(0).getId());
    }

    @Test
    public void getShop_shouldNotReturnAnythingIfShopIsNotAvailable() {
        TestSubscriber<ModelShop> subscriber = new TestSubscriber<>();
        storage.getShop("1").subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertNoValues();
        subscriber.assertCompleted();

        subscriber = new TestSubscriber<>();
        storage.setShops(getTestShops());
        storage.getShop("10").subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void getShop_shouldReturnCorrectShopWhenAvailable() {
        storage.setShops(getTestShops());
        TestSubscriber<ModelShop> subscriber = new TestSubscriber<>();
        storage.getShop("1").subscribe(subscriber);
        subscriber.assertNoErrors();
        assertEquals("1", subscriber.getOnNextEvents().get(0).getId());
    }

    @Test
    public void addShop_shouldAddShopToTop() {
        TestSubscriber<List<ModelShop>> subscriber = new TestSubscriber<>();

        storage.addShop(new ModelShop("10", new ModelShopUser(1, "", ""), new ArrayList<>(), "", Calendar.getInstance().getTime()));
        storage.getShops().subscribe(subscriber);
        assertEquals("10", subscriber.getOnNextEvents().get(0).get(0).getId());

        subscriber = new TestSubscriber<>();
        storage.setShops(getTestShops());
        storage.addShop(new ModelShop("10", new ModelShopUser(1, "", ""), new ArrayList<>(), "", Calendar.getInstance().getTime()));
        storage.getShops().subscribe(subscriber);
        assertEquals("10", subscriber.getOnNextEvents().get(0).get(0).getId());
        assertEquals(3, subscriber.getOnNextEvents().get(0).size());
    }

    @Test
    public void removeShop_shouldRemoveIfExists() {
        storage.removeShop("1");

        TestSubscriber<List<ModelShop>> subscriber = new TestSubscriber<>();
        storage.setShops(getTestShops());
        storage.removeShop("3");

        storage.getShops().subscribe(subscriber);
        assertEquals(getTestShops().size(), subscriber.getOnNextEvents().get(0).size());

        storage.removeShop("1");
        subscriber = new TestSubscriber<>();
        storage.getShops().subscribe(subscriber);
        assertEquals(getTestShops().size() - 1, subscriber.getOnNextEvents().get(0).size());
    }

    @Test
    public void removeShop_shouldRemoveShopItemsIfAvailable() {
        storage.setShops(getTestShops());
        List<ModelShopItem> items = new ArrayList<>();
        items.add(new ModelShopItem("1", "", 0, ModelShopItem.GRAM, Calendar.getInstance().getTime()));
        storage.setShopItems("1", items);

        TestSubscriber<List<ModelShopItem>> subscriber = new TestSubscriber<>();
        storage.getShopItems("1").subscribe(subscriber);
        assertEquals(items.size(), subscriber.getOnNextEvents().get(0).size());

        storage.removeShop("1");

        subscriber = new TestSubscriber<>();
        storage.getShopItems("1").subscribe(subscriber);
        subscriber.assertNoValues();
    }

    @Test
    public void updateShop_shouldUpdateShopAndKeepItOnSamePosition() {
        List<ModelShop> shops = getTestShops();
        storage.setShops(shops);
        ModelShop shop = new ModelShop("1", new ModelShopUser(1, "", ""), new ArrayList<>(), "some new name", Calendar.getInstance().getTime());
        storage.updateShop(shop);

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        TestSubscriber<ModelShop> subscriber = new TestSubscriber<>();
        storage.getShop("1").subscribe(subscriber);
        assertEquals(shop.getId(), subscriber.getOnNextEvents().get(0).getId());
        assertEquals(shop.getName(), subscriber.getOnNextEvents().get(0).getName());

        storage.getShops().subscribe(sub);
        assertEquals(shop.getId(), sub.getOnNextEvents().get(0).get(0).getId());

        subscriber = new TestSubscriber<>();
        storage.getShop("2").subscribe(subscriber);
        assertNotSame(shop.getName(), subscriber.getOnNextEvents().get(0).getName());
    }

    @Test
    public void updateShop_shouldDoNothingIfShopListNotExist() {
        ModelShop shop = new ModelShop("10", new ModelShopUser(1, "", ""), new ArrayList<>(), "some new name", Calendar.getInstance().getTime());
        storage.updateShop(shop);
        storage.setShops(getTestShops());

        storage.updateShop(shop);

        TestSubscriber<List<ModelShop>> sub = new TestSubscriber<>();
        storage.getShops().subscribe(sub);
        assertEquals(getTestShops().size(), sub.getOnNextEvents().get(0).size());
    }

    @Test
    public void getShopItems_shouldReturnNoValuesIfNoItemsCached() {
        TestSubscriber<List<ModelShopItem>> subscriber = new TestSubscriber<>();

        storage.getShopItems("1").subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertNoValues();

        storage.setShopItems("2", new ArrayList<>());
        subscriber = new TestSubscriber<>();
        storage.getShopItems("1").subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertNoValues();
    }

    @Test
    public void getShopItem_shouldReturnValuesIfAvailable() {
        TestSubscriber<List<ModelShopItem>> subscriber = new TestSubscriber<>();

        List<ModelShopItem> items = new ArrayList<>();
        items.add(new ModelShopItem("1", "", 0, ModelShopItem.GRAM, Calendar.getInstance().getTime()));
        storage.setShopItems("2", items);
        storage.getShopItems("2").subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        assertEquals(items.size(), subscriber.getOnNextEvents().get(0).size());
    }

    @Test
    public void clearData() {
        storage.setShops(getTestShops());
        List<ModelShopItem> items = new ArrayList<>();
        items.add(new ModelShopItem("1", "", 0, ModelShopItem.GRAM, Calendar.getInstance().getTime()));
        storage.setShopItems("1", items);
        TestSubscriber<List<ModelShop>> shopSub = new TestSubscriber<>();
        TestSubscriber<List<ModelShopItem>> listSub = new TestSubscriber<>();
        storage.getShops().subscribe(shopSub);
        storage.getShopItems("1").subscribe(listSub);
        shopSub.assertValueCount(1);
        listSub.assertValueCount(1);

        storage.clearData();

        shopSub = new TestSubscriber<>();
        listSub = new TestSubscriber<>();
        storage.getShops().subscribe(shopSub);
        shopSub.assertNoValues();
        listSub.assertNoValues();
    }

    private List<ModelShop> getTestShops() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "name 1", ""));
        users.add(new ModelShopUser(2, "name 2", ""));
        List<ModelShop> shops = new ArrayList<>(2);
        shops.add(new ModelShop("1", users.get(0), users, "list 1", Calendar.getInstance().getTime()));
        shops.add(new ModelShop("2", users.get(1), users, "list 2", Calendar.getInstance().getTime()));
        return shops;
    }
}