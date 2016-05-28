package com.candy.android.candyapp.login;

import android.content.Intent;

import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelUserLogin;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Marcin
 */

public class LoginPresenter {
    private FacebookLogin facebookLogin;
    private UserManager userManager;

    private LoginFragment fragment;

    private Subscription loginSubscription;
    private Observable<ModelUserLogin> loginObservable;

    public LoginPresenter(FacebookLogin facebookLogin, UserManager userManager) {
        this.facebookLogin = facebookLogin;
    }

    public void setParent(LoginFragment parent) {
        this.fragment = parent;

        if (loginObservable != null) {
            loginSubscription = subscribeToLogin(loginObservable);
        }
    }

    public void removeParent() {
        this.fragment = null;
        if (loginSubscription != null && !loginSubscription.isUnsubscribed()) {
            loginSubscription.unsubscribe();
        }
    }

    public void startFacebookLogin() {
        loginObservable = facebookLogin.loginWithEmailPermission(fragment)
                .flatMap(token -> userManager.login(token))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();

        loginSubscription = subscribeToLogin(loginObservable);
    }

    private Subscription subscribeToLogin(Observable<ModelUserLogin> obs) {
        return obs.subscribe(user -> {

        }, error -> {

        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLogin.onActivityResult(requestCode, resultCode, data);
    }
}
