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
import com.candy.android.candyapp.storage.ShopMemoryStorage;

import java.util.List;

import rx.Observable;

/**
 * @author Marcin
 */

public class ShopManager {
    private UserManager userManager;
    private CandyApi api;
    private ShopMemoryStorage memoryStorage;

    public ShopManager(UserManager userManager, CandyApi api, ShopMemoryStorage memoryStorage) {
        this.userManager = userManager;
        this.api = api;
        this.memoryStorage = memoryStorage;
    }

    public void logout() {
        memoryStorage.clearData();
    }

    public Observable<List<ModelShop>> getShopLists(boolean cache) {
        Observable<List<ModelShop>> network = Observable.defer(() -> api.getShopLists(getToken())
                .doOnNext(memoryStorage::setShops));

        if (cache) {
            return Observable.concat(memoryStorage.getShops(), network)
                    .first();
        } else {
            return network;
        }
    }

    public Observable<ModelShop> getShopList(String shopId, boolean cache) {
        Observable<ModelShop> network = Observable.defer(() -> api.getShopList(getToken(), shopId)
                .doOnNext(memoryStorage::addShop)
                .doOnError(error -> {
                    if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST.getCode()) {
                        memoryStorage.removeShop(shopId);
                    }
                }));

        if (cache) {
            return Observable.concat(memoryStorage.getShop(shopId), network)
                    .first();
        }
        return network;
    }

    public Observable<ModelShop> createShopList(String name) {
        return api.createShopList(getToken(), new RequestCreateShopList(name))
                .doOnNext(memoryStorage::addShop);
    }

    public Observable<ModelResponseSimple> removeShopList(String id) {
        return api.deleteShopList(getToken(), id)
                .doOnNext(response -> memoryStorage.removeShop(id))
                .doOnError(error -> {
                    if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST.getCode()) {

                        memoryStorage.removeShop(id);
                    }
                });
    }

    public Observable<List<ModelShopItem>> getShopItems(String id, boolean cache) {
        Observable<List<ModelShopItem>> network = Observable.defer(() -> api.getItems(getToken(), id)
                .doOnNext(items -> memoryStorage.setShopItems(id, items))
                .doOnError(error -> {
                    if (ModelError.fromRetrofit(error).getCode() == ModelError.LIST_NOT_EXIST.getCode()) {
                        memoryStorage.removeShop(id);
                    }
                }));

        if (cache) {
            return Observable.concat(memoryStorage.getShopItems(id), network)
                    .first();
        }

        return network;
    }

    public Observable<Void> inviteToShop(String shopId, ModelFriend friend) {
        return Observable.defer(() -> memoryStorage.getShop(shopId)
                .flatMap(cachedShop -> {
                    ModelUser currentUser = userManager.getUser();
                    if (!cachedShop.isOwner(currentUser.getId())) {
                        return Observable.error(ModelError.generateError(ModelError.NOT_PERMITTED));
                    } else if (cachedShop.isInvited(friend.getId())) {
                        return Observable.error(ModelError.generateError(ModelError.ALREADY_INVITED));
                    } else if (!currentUser.isFriend(friend.getId())) {
                        return Observable.error(ModelError.generateError(ModelError.NOT_ON_FRIEND_LIST));
                    }
                    return Observable.just(cachedShop);
                })
                .flatMap(cachedShop -> api.inviteToList(getToken(), new RequestShopUser(shopId, friend.getId()))
                        .doOnNext(v -> {
                            cachedShop.getUsers().add(new ModelShopUser(friend.getId(), friend.getName(), friend.getPicture()));
                            memoryStorage.updateShop(cachedShop);
                        })));
    }

    public Observable<ModelShopUser> removeFromShop(String shopId, ModelShopUser friend) {
        return Observable.defer(() -> memoryStorage.getShop(shopId)
                .flatMap(shop -> {
                    ModelUser user = userManager.getUser();
                    if (!shop.isOwner(user.getId())) {
                        return Observable.error(ModelError.generateError(ModelError.NOT_PERMITTED));
                    } else if (!shop.isInvited(friend.getId())) {
                        return Observable.error(ModelError.generateError(ModelError.USER_IS_NOT_INVITED));
                    } else if (shop.isOwner(friend.getId())) {
                        return Observable.error(ModelError.generateError(ModelError.CANNOT_REMOVE_OWNER));
                    }
                    return Observable.just(shop);
                })
                .flatMap(shop -> api.removeFromList(getToken(), new RequestShopUser(shopId, friend.getId()))
                        .doOnNext(v -> {
                            shop.removeUser(friend.getId());
                            memoryStorage.updateShop(shop);
                        }))
                .flatMap(aVoid -> Observable.just(friend)));
    }

    private String getToken() {
        return "Bearer " + userManager.getToken();
    }
}
