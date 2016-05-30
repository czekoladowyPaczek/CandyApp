package com.candy.android.candyapp.facebook;

import android.content.Intent;

import com.candy.android.candyapp.login.LoginFragment;
import com.candy.android.zlog.ZLog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import rx.Observable;

/**
 * @author Marcin
 */

public class FacebookLogin {
    public static class FacebookCancelException extends Throwable {}

    private static final String[] PERMISSIONS = {"email"};
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    public FacebookLogin(LoginManager loginManager) {
        this.loginManager = loginManager;
        callbackManager = CallbackManager.Factory.create();
    }

    public Observable<String> loginWithEmailPermission(LoginFragment fragment) {
        Observable<String> obs = Observable.create(subscriber -> {
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(loginResult.getAccessToken().getToken());
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onCancel() {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new FacebookCancelException());
                    }
                }

                @Override
                public void onError(FacebookException error) {
                    ZLog.e(error.getMessage());
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(error);
                    }
                }
            });
        });

        loginManager.logInWithReadPermissions(fragment, Arrays.asList(PERMISSIONS));
        return obs;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
