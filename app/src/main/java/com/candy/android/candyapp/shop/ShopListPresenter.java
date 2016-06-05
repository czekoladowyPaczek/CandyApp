package com.candy.android.candyapp.shop;

import android.support.annotation.StringRes;

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

    private Subscription shopsSubscription;
    private Observable<List<ModelShop>> shopsObservable;

    private Subscription shopCreateSubscription;
    private Observable<ModelShop> shopCreateObservable;

    public ShopListPresenter(ShopManager manager) {
        this.manager = manager;
    }

    public void setParent(ShopListFragment parent) {
        this.parent = parent;

        if (shopsObservable != null) {
            parent.showListLoading(true);
            shopsSubscription = subscribeToShops(shopsObservable);
        } else {
            parent.showListLoading(true);
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
        @StringRes int res;
        switch (ModelError.fromRetrofit(error).getCode()) {
            case ModelError.INTERNET_CONNECTION:
                res = R.string.error_connection;
                break;
            case ModelError.AUTHENTICATION:
                res = R.string.error_authentication;
                break;
            case ModelError.MISSING_PROPERTIES:
                res = R.string.shop_error_missing_name;
                break;
            case ModelError.LIST_COUNT_LIMIT_EXCEEDED:
                res = R.string.shop_error_count_limit;
                break;
            default:
                res = R.string.error_unknown;

        }
        parent.showError(res);
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
            shopCreateSubscription = null;
            shopCreateObservable = null;
        }
    }
}
