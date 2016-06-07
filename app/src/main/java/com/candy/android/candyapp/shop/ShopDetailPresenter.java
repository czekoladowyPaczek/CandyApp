package com.candy.android.candyapp.shop;

import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShopItem;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Marcin
 */

public class ShopDetailPresenter {

    private ShopManager shopManager;
    private String listId;
    private ShopDetailFragment fragment;

    private Observable<List<ModelShopItem>> getItemsObs;
    private Subscription getItemsSub;

    public ShopDetailPresenter(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    public void setParent(String listId, ShopDetailFragment fragment) {
        this.listId = listId;
        this.fragment = fragment;

        fragment.showListLoading(true);
        if (getItemsObs != null) {
            getItemsSub = subscribeToGetItems(getItemsObs);
        } else {
            getShopListItems(true);
        }
    }

    public void removeParent() {
        fragment = null;
        if (getItemsSub != null && !getItemsSub.isUnsubscribed()) {
            getItemsSub.unsubscribe();
        }
    }

    public void getShopListItems(boolean cache) {
        getItemsObs = shopManager.getShopItems(listId, cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        getItemsSub = subscribeToGetItems(getItemsObs);
    }

    private Subscription subscribeToGetItems(Observable<List<ModelShopItem>> obs) {
        return obs.subscribe(items -> {
            fragment.showListLoading(false);
            fragment.setData(items);
            getItemsObs = null;
        }, error -> {
            fragment.showListLoading(false);
            fragment.showError(0);
            getItemsObs = null;
        });
    }
}
