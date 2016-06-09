package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Marcin
 */

public class ShopManager {
    private UserManager userManager;
    private CandyApi api;

    private List<ModelShop> shops;
    private Map<String, List<ModelShopItem>> items;

    public ShopManager(UserManager userManager, CandyApi api) {
        this.userManager = userManager;
        this.api = api;

        items = new HashMap<>();
    }

    public void logout() {
        this.shops = null;
        this.items.clear();
    }

    public Observable<List<ModelShop>> getShopLists(boolean cache) {
        if (shops != null && cache) {
            return Observable.just(shops);
        } else {
            return api.getShopLists("Bearer " + userManager.getToken())
                    .doOnNext(shops -> this.shops = shops);
        }
    }

    public Observable<ModelShop> createShopList(String name) {
        return api.createShopList("Bearer " + userManager.getToken(), new RequestCreateShopList(name))
                .doOnNext(shop -> {
                    if (shops == null) {
                        shops = new ArrayList<>();
                    }
                    shops.add(shop);
                });
    }

    public Observable<ModelResponseSimple> removeShopList(String id) {
        return api.deleteShopList("Bearer " + userManager.getToken(), id)
                .doOnNext(response -> removeShopFromCache(id))
                .doOnError(error -> {
                    if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST) {
                        removeShopFromCache(id);
                    }
                });
    }

    private void removeShopFromCache(String shopId) {
        if (shops != null) {
            Iterator<ModelShop> shopIterator = shops.iterator();
            while (shopIterator.hasNext()) {
                final ModelShop shop = shopIterator.next();
                if (shop.getId().equals(shopId)) {
                    shopIterator.remove();
                    break;
                }
            }
        }
        if (items.containsKey(shopId)) {
            items.remove(shopId);
        }
    }

    public Observable<List<ModelShopItem>> getShopItems(String id, boolean cache) {
        if (cache && items.containsKey(id)) {
            return Observable.just(items.get(id));
        } else {
            return api.getItems("Bearer " + userManager.getToken(), id)
                    .doOnNext(items -> this.items.put(id, items));
        }
    }
}
