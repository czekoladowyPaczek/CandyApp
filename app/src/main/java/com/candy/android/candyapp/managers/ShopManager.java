package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.zlog.ZLog;

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

    public Observable<List<ModelShop>> getShopLists(boolean cache) {
        if (shops != null && cache) {
            return Observable.just(shops);
        } else {
            return api.getShopLists(userManager.getToken())
                    .doOnNext(shops -> {
                        this.shops = shops;
                        ZLog.e("onNext");
                    });
        }
    }
}
