package com.candy.android.candyapp.storage;

import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;

import java.util.List;

import rx.Observable;

/**
 * @author Marcin
 */
public interface IShopStorage {
    Observable<List<ModelShop>> getShops();
    void setShops(List<ModelShop> shops);

    Observable<ModelShop> getShop(String id);
    void addShop(ModelShop shop);
    void removeShop(String id);
    void updateShop(ModelShop shop);

    Observable<List<ModelShopItem>> getShopItems(String shopId);
    void setShopItems(String shopId, List<ModelShopItem> items);
}
