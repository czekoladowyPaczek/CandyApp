package com.candy.android.candyapp.api.request;

/**
 * Created by marcingawel on 11.06.2016.
 */

public class RequestShopUser {
    private long userId;
    private String shopId;

    public RequestShopUser(String shopId, long userId) {
        this.userId = userId;
        this.shopId = shopId;
    }

    public long getUserId() {
        return userId;
    }

    public String getShopId() {
        return shopId;
    }
}
