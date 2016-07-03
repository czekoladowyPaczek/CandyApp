package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.api.ModelResponseSimple;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.api.request.RequestShopUser;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.zlog.ZLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

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
            return api.getShopLists(getToken())
                    .doOnNext(shops -> this.shops = shops);
        }
    }

    public Observable<ModelShop> getShopList(String shopId, boolean cache) {
        ModelShop cachedShop = null;
        if (cache) {
            cachedShop = getShopFromCache(shopId);
        }

        if (cachedShop != null) {
            return Observable.just(cachedShop);
        } else {
            return api.getShopList(getToken(), shopId)
                    .doOnNext(this::addShopToCache)
                    .doOnError(error -> {
                        if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST.getCode()) {
                            removeShopFromCache(shopId);
                        }
                    });
        }
    }

    public Observable<ModelShop> createShopList(String name) {
        return api.createShopList(getToken(), new RequestCreateShopList(name))
                .doOnNext(this::addShopToCache);
    }

    public Observable<ModelResponseSimple> removeShopList(String id) {
        return api.deleteShopList(getToken(), id)
                .doOnNext(response -> {
                    removeShopFromCache(id);
                    if (items.containsKey(id)) {
                        items.remove(id);
                    }
                })
                .doOnError(error -> {
                    if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST.getCode()) {
                        removeShopFromCache(id);
                    }
                });
    }

    public Observable<List<ModelShopItem>> getShopItems(String id, boolean cache) {
        if (cache && items.containsKey(id)) {
            return Observable.just(items.get(id));
        } else {
            return api.getItems(getToken(), id)
                    .doOnNext(items -> this.items.put(id, items))
                    .doOnError(error -> {
                        if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST.getCode()) {
                            removeShopFromCache(id);
                        }
                    });
        }
    }

    public Observable<Void> inviteToShop(String shopId, ModelFriend friend) {
        ModelShop cachedShop = getShopFromCache(shopId);
        ModelUser currentUser = userManager.getUser();
        if (cachedShop != null) {
            if (!cachedShop.isOwner(currentUser.getId())) {
                return Observable.error(ModelError.generateError(ModelError.NOT_PERMITTED));
            } else if (cachedShop.isInvited(friend.getId())) {
                return Observable.error(ModelError.generateError(ModelError.ALREADY_INVITED));
            } else if (!currentUser.isFriend(friend.getId())) {
                return Observable.error(ModelError.generateError(ModelError.NOT_ON_FRIEND_LIST));
            }
        }
        return api.inviteToList(getToken(), new RequestShopUser(shopId, friend.getId()))
                .doOnNext(v -> {
                    ModelShop shop = getShopFromCache(shopId);
                    if (shop != null) {
                        shop.getUsers().add(new ModelShopUser(friend.getId(), friend.getName(), friend.getPicture()));
                    }
                });
    }

    public Observable<ModelShopUser> removeFromShop(String shopId, ModelShopUser friend) {
        ModelShop shop = getShopFromCache(shopId);
        ModelUser user = userManager.getUser();
        if (shop != null) {
            if (!shop.isOwner(user.getId())) {
                return Observable.error(ModelError.generateError(ModelError.NOT_PERMITTED));
            } else if (!shop.isInvited(friend.getId())) {
                return Observable.error(ModelError.generateError(ModelError.USER_IS_NOT_INVITED));
            } else if (shop.isOwner(friend.getId())) {
                return Observable.error(ModelError.generateError(ModelError.CANNOT_REMOVE_OWNER));
            }
        }
        return api.removeFromList(getToken(), new RequestShopUser(shopId, friend.getId()))
                .doOnNext(v -> {
                    ModelShop cachedShop = getShopFromCache(shopId);
                    if (cachedShop != null) {
                        cachedShop.removeUser(friend.getId());
                    }
                })
                .flatMap((Func1<Void, Observable<ModelShopUser>>) aVoid -> Observable.just(friend));
    }

    private ModelShop getShopFromCache(String shopId) {
        if (shops != null) {
            for (ModelShop shop : shops) {
                if (shop.getId().equals(shopId)) {
                    return shop;
                }
            }
        }
        return null;
    }

    private void addShopToCache(ModelShop shop) {
        if (shops == null) {
            shops = new ArrayList<>();
        }
        int existingPosition = -1;
        for (int i = 0; i < shops.size(); i++) {
            if (shops.get(i).getId().equals(shop.getId())) {
                existingPosition = i;
                break;
            }
        }
        if (existingPosition > -1) {
            shops.remove(existingPosition);
            shops.add(existingPosition, shop);
        } else {
            shops.add(0, shop);
        }
    }

    private void removeShopFromCache(String id) {
        if (shops != null) {
            ZLog.e("remove " + id);
            Iterator<ModelShop> shopIterator = shops.iterator();
            while (shopIterator.hasNext()) {
                final ModelShop shop = shopIterator.next();
                if (shop.getId().equals(id)) {
                    shopIterator.remove();
                    break;
                }
            }
        }
        if (items.containsKey(id)) {
            items.remove(id);
        }
    }

    private String getToken() {
        return "Bearer " + userManager.getToken();
    }
}
