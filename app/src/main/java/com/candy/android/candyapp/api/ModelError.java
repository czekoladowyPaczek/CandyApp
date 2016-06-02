package com.candy.android.candyapp.api;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * @author Marcin
 */

public class ModelError {
    public static final int INTERNET_CONNECTION = -1;
    public static final int AUTHENTICATION = 1000;
    public static final int UNKNOWN = 0;
    public static final int MISSING_PROPERTIES = 1;
    public static final int MISSING_EMAIL = 2;
    public static final int INCORRECT_TOKEN = 3;
    public static final int INCORRECT_EMAIL = 4;
    public static final int EMAIL_ALREADY_EXISTS = 5;
    public static final int NO_USER = 6;
    public static final int SELF_INVITATION = 21;
    public static final int ALREADY_FRIEND = 22;
    public static final int NOT_INVITED = 23;
    public static final int LIST_COUNT_LIMIT_EXCEEDED = 31;
    public static final int LIST_NOT_EXIST = 32;
    public static final int NOT_PERMITTED = 33;
    public static final int CANNOT_INVITE_SELF = 41;
    public static final int CANNOT_REMOVE_OWNER = 42;
    public static final int USER_IS_NOT_INVITED = 43;
    public static final int ALREADY_INVITED = 44;
    public static final int NOT_ON_FRIEND_LIST = 45;
    public static final int CANNOT_REMOVE_SELF = 46;
    public static final int LIST_SIZE_LIMIT_EXCEEDED = 47;
    public static final int SHOP_ITEM_NOT_CHANGED = 51;
    public static final int SHOP_ITEM_NOT_EXIST = 52;

    private int code;

    private ModelError(int code) {
        this.code = code;
    }

    public static ModelError fromRetrofit(Throwable exception) {
        if (exception instanceof HttpException) {
            HttpException e = (HttpException) exception;
            if (e.code() == 401) {
                return new ModelError(AUTHENTICATION);
            } else if (e.code() == 500) {
                Gson gson = new Gson();
                try {
                    return gson.fromJson(e.response().errorBody().string(), ModelError.class);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return new ModelError(UNKNOWN);
                }
            } else {
                return new ModelError(UNKNOWN);
            }
        } else {
            return new ModelError(INTERNET_CONNECTION);
        }
    }
    public int getCode() {
        return code;
    }
}
