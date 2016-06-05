package com.candy.android.candyapp.api.request;

/**
 * @author Marcin
 */

public class RequestCreateShopList {
    private String name;

    public RequestCreateShopList(String name) {
        this.name = name;
    }

    public String getShopName() {
        return name;
    }
}
