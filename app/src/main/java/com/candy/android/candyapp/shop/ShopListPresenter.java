package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShop;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Marcin
 */

public class ShopListPresenter {
    private ShopManager manager;
    private ShopListFragment parent;

    private Subscription shopsSubscription;
    private Observable<List<ModelShop>> shopsObservable;

    private Subscription shopCreateSubscription;
    private Observable<ModelResponseSimple> shopCreateObservable;

    public ShopListPresenter(ShopManager manager) {
        this.manager = manager;
    }

    public void setParent(ShopListFragment parent) {
        this.parent = parent;

        if (shopsObservable != null) {
            parent.showListLoading();
            shopsSubscription = subscribeToShops(shopsObservable);
        } else {
            parent.showListLoading();
            getShopLists(true);
        }
        if (shopCreateObservable != null) {
            parent.showLoadingDialog(R.string.shop_dialog_creating);
            shopCreateSubscription = subscribeToCreateShop(shopCreateObservable);
        }
    }

    private Subscription subscribeToShops(Observable<List<ModelShop>> obs) {
        return obs.subscribe(shops -> {
            shopsObservable = null;
        }, err -> {
            shopsObservable = null;
        });
    }

    private Subscription subscribeToCreateShop(Observable<ModelResponseSimple> obs) {
        return obs.subscribe(shops -> {
            shopCreateObservable = null;
        }, err -> {
            shopCreateObservable = null;
        });
    }

    public void getShopLists(boolean cache) {
        shopsObservable = manager.getShopLists(cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        shopsSubscription = subscribeToShops(shopsObservable);
    }

    public void removeParent() {
        if (shopsSubscription != null && !shopsSubscription.isUnsubscribed()) {
            shopsSubscription.unsubscribe();
        }
        if (shopCreateSubscription != null && !shopCreateSubscription.isUnsubscribed()) {
            shopCreateSubscription.unsubscribe();
        }
    }
}
