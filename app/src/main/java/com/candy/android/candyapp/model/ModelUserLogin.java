package com.candy.android.candyapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Marcin
 */

public class ModelUserLogin {
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private ModelUser user;

    public ModelUserLogin(String token, ModelUser user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public ModelUser getUser() {
        return user;
    }

}
