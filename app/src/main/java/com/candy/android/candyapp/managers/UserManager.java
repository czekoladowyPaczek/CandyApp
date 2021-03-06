package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.request.RequestAcceptFriend;
import com.candy.android.candyapp.api.request.RequestInviteFriend;
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
    private Observable<ModelUser> friendInvitationObservable;

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

    public void logout() {
        user = null;
        token = null;
        storage.clear();
    }

    public Observable<ModelUser> inviteFriend(String email, boolean cache) {
        if (!cache || friendInvitationObservable == null) {
            friendInvitationObservable = api.inviteFriend("Bearer " + getToken(), new RequestInviteFriend(email))
                    .flatMap(friends -> {
                        ModelUser user = getUser();
                        user.setFriends(friends);
                        storage.saveUser(user);
                        return Observable.just(user);
                    })
                    .cache();
        }
        return friendInvitationObservable;
    }

    public Observable<ModelUser> acceptFriend(long id, boolean cache) {
        if (!cache || friendInvitationObservable == null) {
            friendInvitationObservable = api.acceptFriend("Bearer " + getToken(), new RequestAcceptFriend(id))
                    .flatMap(friends -> {
                        ModelUser user = getUser();
                        user.setFriends(friends);
                        storage.saveUser(user);
                        return Observable.just(user);
                    })
                    .cache();
        }
        return friendInvitationObservable;
    }

    public Observable<ModelUser> deleteFriend(long id, boolean cache) {
        if (!cache || friendInvitationObservable == null) {
            friendInvitationObservable = api.deleteFriend("Bearer " + getToken(), id)
                    .flatMap(friends -> {
                        ModelUser user = getUser();
                        user.setFriends(friends);
                        storage.saveUser(user);
                        return Observable.just(user);
                    })
                    .cache();
        }
        return friendInvitationObservable;
    }
}
