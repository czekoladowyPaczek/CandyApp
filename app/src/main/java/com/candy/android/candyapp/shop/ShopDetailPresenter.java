package com.candy.android.candyapp.shop;

import android.support.annotation.StringRes;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.model.ModelShop;
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

    private ShopDetailFragmentContract fragment;
    private ModelShop shop;
    private List<ModelShopItem> items;

    private Observable<List<ModelShopItem>> getItemsObs;
    private Subscription getItemsSub;

    private Observable<ModelResponseSimple> removeListObs;
    private Subscription removeListSub;

    public ShopDetailPresenter(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    public void setParent(ShopDetailFragmentContract fragment) {
        this.fragment = fragment;

        if (shop == null) {
            shop = fragment.getShopList();
            fragment.showListLoading();
            getShopListItems(true);
        } else if (getItemsObs != null) {
            fragment.showListLoading();
            getItemsSub = subscribeToGetItems(getItemsObs);
        } else if (items != null) {
            fragment.setShopItems(items);
        }

//        if (removeListObs != null) {
//            fragment.showRemovingDialog();
//            removeListSub = subscribeToRemoveList(removeListObs);
//        }
    }

    public void removeParent() {
        fragment = null;
        if (getItemsSub != null && !getItemsSub.isUnsubscribed()) {
            getItemsSub.unsubscribe();
        }
        if (removeListSub != null && !removeListSub.isUnsubscribed()) {
            removeListSub.unsubscribe();
        }
    }

    public void getShopListItems(boolean cache) {
        getItemsObs = shopManager.getShopItems(shop.getId(), cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        getItemsSub = subscribeToGetItems(getItemsObs);
    }

    public void deleteList() {
//        removeListObs = shopManager.removeShopList(listId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .cache();
//        removeListSub = subscribeToRemoveList(removeListObs);
    }

    private Subscription subscribeToGetItems(Observable<List<ModelShopItem>> obs) {
        return obs.subscribe(items -> {
            this.items = items;
            fragment.setShopItems(this.items);
            fragment.hideListLoading();
            getItemsObs = null;
        }, err -> {
            fragment.hideListLoading();
            showError(err);
            getItemsObs = null;
        });
    }

//    private Subscription subscribeToRemoveList(Observable<ModelResponseSimple> obs) {
//        return obs.subscribe(response -> {
//            fragment.hideRemovingDialog();
//            fragment.onListDeleted();
//            removeListObs = null;
//        }, error -> {
//            fragment.hideRemovingDialog();
//            showError(error);
//            removeListObs = null;
//        });
//    }

    private void showError(Throwable error) {
        switch (ModelError.fromRetrofit(error)) {
            case INTERNET_CONNECTION:
                fragment.showError(R.string.error_connection);
                break;
            case LIST_NOT_EXIST:
                fragment.showError(R.string.shop_error_list_not_exist);
                break;
            case NOT_PERMITTED:
                fragment.showError(R.string.error_not_permitted);
                break;
            default:
                fragment.showError(R.string.error_unknown);
        }
    }

    public interface ShopDetailFragmentContract {
        ModelShop getShopList();
        void showError(@StringRes int res);
        void showListLoading();
        void hideListLoading();
        void setShopItems(List<ModelShopItem> items);
        void showRemovingDialog();
        void hideRemovingDialog();
    }
}
