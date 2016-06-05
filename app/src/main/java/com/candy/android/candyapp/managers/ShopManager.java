package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.model.ModelShop;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * @author Marcin
 */

public class ShopManager {
    private UserManager userManager;
    private CandyApi api;

    private List<ModelShop> shops;

    public ShopManager(UserManager userManager, CandyApi api) {
        this.userManager = userManager;
        this.api = api;
    }

    public void logout() {
        this.shops = null;
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
}
