package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.model.ModelUserLogin;
import com.candy.android.candyapp.storage.UserStorage;

import rx.Observable;

/**
 * @author Marcin
 */

public class UserManager {

    private CandyApi api;
    private UserStorage storage;

    private String token;
    private ModelUser user;

    private Observable<ModelUser> profileObservable;

    public UserManager(CandyApi api, UserStorage storage) {
        this.api = api;
        this.storage = storage;
    }

    public boolean isLoggedIn() {
        return storage.getToken() != null;
    }

    public String getToken() {
        if (token == null) {
            token = storage.getToken();
        }

        return token;
    }

    public Observable<ModelUserLogin> login(String token) {
        return api.login(token)
                .doOnNext(login -> {
                    storage.saveUserToken(login.getToken());
                    storage.saveUser(login.getUser());
                    this.user = login.getUser();
                });
    }

    public ModelUser getUser() {
        if (user == null) {
            user = storage.getUser();
        }
        return user;
    }

    public Observable<ModelUser> getProfile(boolean cache) {
        if (!cache || profileObservable == null) {
            profileObservable = api.getProfile("Bearer " + getToken())
                    .doOnNext(user -> {
                        UserManager.this.user = user;
                        storage.saveUser(user);
                    })
                    .cache();
        }

        return profileObservable;
    }
}
