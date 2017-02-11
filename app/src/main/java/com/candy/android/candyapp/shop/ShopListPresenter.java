package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.api.ModelError;
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
    private boolean freshStart = true;

    private Subscription shopsSubscription;
    private Observable<List<ModelShop>> shopsObservable;

    private Subscription shopCreateSubscription;
    private Observable<ModelShop> shopCreateObservable;

    public ShopListPresenter(ShopManager manager) {
        this.manager = manager;
    }

    public void setParent(ShopListFragment parent) {
        this.parent = parent;

        if (freshStart || shopsObservable != null) {
            freshStart = false;
            parent.showListLoading(true);
        }
        if (shopsObservable != null) {
            shopsSubscription = subscribeToShops(shopsObservable);
        } else {
            getShopLists(true);
        }
        if (shopCreateObservable != null) {
            parent.showLoadingDialog(R.string.shop_dialog_creating);
            shopCreateSubscription = subscribeToCreateShop(shopCreateObservable);
        }
    }

    private Subscription subscribeToShops(Observable<List<ModelShop>> obs) {
        return obs.subscribe(shops -> {
            parent.showListLoading(false);
            parent.setData(shops);
            shopsObservable = null;
        }, err -> {
            parent.showListLoading(false);
            showError(err);
            shopsObservable = null;
        });
    }

    private Subscription subscribeToCreateShop(Observable<ModelShop> obs) {
        return obs.subscribe(shop -> {
            parent.hideLoadingDialog();
            parent.addData(shop);
            shopCreateObservable = null;
        }, err -> {
            parent.hideLoadingDialog();
            showError(err);
            shopCreateObservable = null;
        });
    }

    private void showError(Throwable error) {
        parent.showError(ModelError.fromRetrofit(error).getResourceMessage());
    }

    public void getShopLists(boolean cache) {
        shopsObservable = manager.getShopLists(cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        shopsSubscription = subscribeToShops(shopsObservable);
    }

    public void createShopList(String name) {
        parent.showLoadingDialog(R.string.shop_dialog_creating);
        shopCreateObservable = manager.createShopList(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        shopCreateSubscription = subscribeToCreateShop(shopCreateObservable);
    }

    public void removeParent() {
        if (shopsSubscription != null && !shopsSubscription.isUnsubscribed()) {
            shopsSubscription.unsubscribe();
        }
        if (shopCreateSubscription != null && !shopCreateSubscription.isUnsubscribed()) {
            shopCreateSubscription.unsubscribe();
        }
    }

    public void cancelShopCreating() {
        if (shopCreateObservable != null && !shopCreateSubscription.isUnsubscribed()) {
            shopCreateSubscription.unsubscribe();
            shopCreateSubscription = null;
            shopCreateObservable = null;
        }
    }
}
