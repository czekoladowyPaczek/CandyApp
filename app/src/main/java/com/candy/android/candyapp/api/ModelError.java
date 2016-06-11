package com.candy.android.candyapp.api;

import android.support.annotation.StringRes;

import com.candy.android.candyapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

/**
 * @author Marcin
 */

public enum ModelError {
    INTERNET_CONNECTION(-1, R.string.error_connection),
    AUTHENTICATION(1000, R.string.error_authentication),
    UNKNOWN(0, R.string.error_unknown),
    MISSING_PROPERTIES(1, R.string.error_internal),
    MISSING_EMAIL(2, R.string.error_missing_email),
    INCORRECT_TOKEN(3, R.string.error_token),
    INCORRECT_EMAIL(4, R.string.error_incorrect_email),
    EMAIL_ALREADY_EXISTS(5, R.string.error_existing_email),
    NO_USER(6, R.string.error_no_user),

    SELF_INVITATION(21, R.string.error_cannot_invite_self),
    ALREADY_FRIEND(22, R.string.error_already_friend),
    NOT_INVITED(23, R.string.error_not_invited),

    LIST_COUNT_LIMIT_EXCEEDED(31, R.string.error_list_count_limit),
    LIST_NOT_EXIST(32, R.string.error_list_not_exist),
    NOT_PERMITTED(33, R.string.error_not_permitted),

    CANNOT_INVITE_SELF(41, R.string.error_cannot_invite_self),
    CANNOT_REMOVE_OWNER(42, R.string.error_owner_remove),
    USER_IS_NOT_INVITED(43, R.string.error_remove_uninvited),
    ALREADY_INVITED(44, R.string.error_already_friend),
    NOT_ON_FRIEND_LIST(45, R.string.error_invite_not_friend),
    CANNOT_REMOVE_SELF(46, R.string.error_remove_self),
    LIST_SIZE_LIMIT_EXCEEDED(47, R.string.error_item_count_limit),

    SHOP_ITEM_NOT_CHANGED(51, R.string.error_item_not_changed),
    SHOP_ITEM_NOT_EXIST(52, R.string.error_item_deleted);

    private int code;
    private @StringRes int resource;

    ModelError(int code, int resource) {
        this.code = code;
        this.resource = resource;
    }

    public int getCode() {
        return code;
    }

    public @StringRes int getResourceMessage() {
        return resource;
    }

    public static ModelError fromRetrofit(Throwable exception) {
        if (exception instanceof HttpException) {
            HttpException e = (HttpException) exception;
            if (e.code() == 401) {
                return ModelError.AUTHENTICATION;
            } else if (e.code() == 500) {
                try {
                    JSONObject obj = new JSONObject(e.response().errorBody().string());
                    int code = obj.getInt("code");
                    for (ModelError error : ModelError.values()) {
                        if (error.getCode() == code) {
                            return error;
                        }
                    }
                } catch (IOException | JSONException ex) {
                    e.printStackTrace();
                }
            }
        } else {
            return ModelError.INTERNET_CONNECTION;
        }

        return ModelError.UNKNOWN;
    }

    public static HttpException generateError(ModelError code) {
        return new HttpException(Response.error(500, ResponseBody.create(null, "{\"code\":" + code.getCode() + ", \"message\":\"\"}")));
    }
}
