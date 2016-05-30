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

    public boolean isLoggedIn() {
        return storage.getToken() != null;
    }

    public String getToken() {
        return storage.getToken();
    }

    public Observable<ModelUserLogin> login(String token) {
        return api.login(token)
                .doOnNext(login -> {
                    storage.saveUserToken(login.getToken());
                    storage.saveUser(login.getUser());
                });
    }
}
