package com.candy.android.candyapp.shop;

import android.util.SparseArray;

import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.model.ModelUser;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcingawel on 11.06.2016.
 */

public class ShopFriendPresenter {
    private static final int RETAIN_REFRESH = 1;
    private static final int RETAIN_REMOVE = 2;
    private ShopManager shopManager;
    private UserManager userManager;

    private ShopFriendActivity parent;

    private ModelShop shop;
    private ModelUser user;

    private Subscription refreshSub;
    private Observable<ModelShop> refreshObs;

    private Subscription removeSub;
    private Observable<ModelShopUser> removeObs;

    public ShopFriendPresenter(ShopManager shopManager, UserManager userManager) {
        this.shopManager = shopManager;
        this.userManager = userManager;
    }

    public void setParent(ShopFriendActivity activity, ModelShop shop, SparseArray<Observable> retain) {
        this.parent = activity;
        this.shop = shop;

        user = userManager.getUser();

        parent.showFabButton(shop.isOwner(user.getId()));

        if (retain != null) {
            refreshObs = retain.get(RETAIN_REFRESH);
            if (refreshObs != null) {
                refreshSub = subscribeToRefresh(refreshObs);
                parent.setRefreshing(true);
            }
            removeObs = retain.get(RETAIN_REMOVE);
            if (removeObs != null) {
                removeSub = subscribeToRemove(removeObs);
                parent.showRemovingProgressDialog();
            }
        }
    }

    public void removeParent() {
        if (refreshSub != null && !refreshSub.isUnsubscribed()) {
            refreshSub.unsubscribe();
        }
        if (removeSub != null && !removeSub.isUnsubscribed()) {
            removeSub.unsubscribe();
        }
    }

    public void refreshData() {
        refreshObs = shopManager.getShopList(shop.getId(), false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        refreshSub = subscribeToRefresh(refreshObs);
    }

    public SparseArray<Observable> onRetainCustomNonConfigurationInstance() {
        SparseArray<Observable> retain = new SparseArray<>(1);
        if (refreshObs != null) {
            retain.put(RETAIN_REFRESH, refreshObs);
        }
        if (removeObs != null) {
            retain.put(RETAIN_REMOVE, removeObs);
        }
        return retain;
    }

    public boolean isListOwner() {
        return shop.getOwner().getId() == user.getId();
    }

    public void removeUser(ModelShopUser user) {
        removeObs = shopManager.removeFromShop(shop.getId(), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        removeSub = subscribeToRemove(removeObs);
    }

    private Subscription subscribeToRefresh(Observable<ModelShop> obs) {
        return obs.subscribe(shop -> {
            this.shop = shop;
            parent.onDataRefresh(shop);
            refreshObs = null;
        }, error -> {
            error.printStackTrace();
            parent.setRefreshing(false);
            parent.showError(ModelError.fromRetrofit(error).getResourceMessage());
            refreshObs = null;
        });
    }

    private Subscription subscribeToRemove(Observable<ModelShopUser> obs) {
        return obs.subscribe(user -> {
            parent.removeRemovingProgressDialog();
            parent.removeUserFromList(user);
            removeObs = null;
        }, error -> {
            parent.removeRemovingProgressDialog();
            parent.showError(ModelError.fromRetrofit(error).getResourceMessage());
            removeObs = null;
        });
    }
}
