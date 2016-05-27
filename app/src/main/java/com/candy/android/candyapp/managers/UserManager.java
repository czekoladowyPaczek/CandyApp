package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.model.ModelUserLogin;
import com.candy.android.candyapp.storage.UserStorage;

import rx.Observable;

/**
 * @author Marcin
 */

public class UserManager {

    private CandyApi api;
    private UserStorage storage;

    public UserManager(CandyApi api, UserStorage storage) {
        this.api = api;
        this.storage = storage;
    }

    public Observable<ModelUserLogin> login(String token) {
        return null;
    }
}
