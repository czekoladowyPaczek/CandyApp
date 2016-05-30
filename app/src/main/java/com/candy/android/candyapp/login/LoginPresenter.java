package com.candy.android.candyapp.login;

import android.content.Intent;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelUserLogin;
import com.facebook.FacebookException;

import java.io.IOException;

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
        this.userManager = userManager;
    }

    public void setParent(LoginFragment parent) {
        this.fragment = parent;

        if (loginObservable != null) {
            fragment.showLoadingView();
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
        fragment.showLoadingView();
        loginObservable = facebookLogin.loginWithEmailPermission(fragment)
                .observeOn(Schedulers.io())
                .flatMap(token -> userManager.login("Bearer " + token))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();

        loginSubscription = subscribeToLogin(loginObservable);
    }

    private Subscription subscribeToLogin(Observable<ModelUserLogin> obs) {
        return obs.subscribe(user -> {
            fragment.removeLoadingView();
            fragment.onLoginSuccess();
        }, error -> {
            error.printStackTrace();
            fragment.removeLoadingView();

            if (error instanceof FacebookException) {
                fragment.showError(R.string.login_error_facebook);
            } else if (error instanceof FacebookLogin.FacebookCancelException) {

            } else if (error instanceof IOException) {
                fragment.showError(R.string.login_error_connection);
            } else {
                fragment.showError(R.string.login_error_api);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLogin.onActivityResult(requestCode, resultCode, data);
    }
}
