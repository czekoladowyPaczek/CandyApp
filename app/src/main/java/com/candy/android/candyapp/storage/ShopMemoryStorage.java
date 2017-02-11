package com.candy.android.candyapp.storage;

import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Marcin
 */
public class ShopMemoryStorage implements IShopStorage {

    private List<ModelShop> shops;
    private Map<String, List<ModelShopItem>> items;

    public ShopMemoryStorage() {
        this.items = new HashMap<>();
    }

    public void clearData() {
        items.clear();
        shops = null;
    }

    @Override
    public Observable<List<ModelShop>> getShops() {
        return Observable.create(f -> {
            if (shops != null) {
                f.onNext(shops);
            }
            f.onCompleted();
        });
    }

    @Override
    public void setShops(List<ModelShop> shops) {
        this.shops = shops;
    }

    @Override
    public Observable<ModelShop> getShop(String id) {
        return Observable.create(f -> {
            if (shops != null) {
                for (ModelShop shop : shops) {
                    if (shop.getId().equals(id)) {
                        f.onNext(shop);
                        break;
                    }
                }
            }
            f.onCompleted();
        });
    }

    @Override
    public void addShop(ModelShop shop) {
        if (shops == null)
            shops = new ArrayList<>();

        shops.add(0, shop);
    }

    @Override
    public void removeShop(String id) {
        if (shops == null)
            return;

        for (Iterator<ModelShop> it = shops.iterator(); it.hasNext(); ) {
            final ModelShop shop = it.next();
            if (shop.getId().equals(id)) {
                it.remove();
                break;
            }
        }

        items.remove(id);
    }

    @Override
    public void updateShop(ModelShop shop) {
        if (shops == null)
            return;

        int position = -1;
        for (int i = 0; i < shops.size(); i++) {
            if (shops.get(i).getId().equals(shop.getId())) {
                position = i;
            }
        }

        if (position > -1) {
            shops.remove(position);
            shops.add(position, shop);
        }
    }

    @Override
    public Observable<List<ModelShopItem>> getShopItems(String shopId) {
        return Observable.create(f -> {
            if (items.containsKey(shopId)) {
                f.onNext(items.get(shopId));
            }
            f.onCompleted();
        });
    }

    @Override
    public void setShopItems(String shopId, List<ModelShopItem> items) {
        this.items.put(shopId, items);
    }
}
