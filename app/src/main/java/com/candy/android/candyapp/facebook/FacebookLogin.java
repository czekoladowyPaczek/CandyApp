package com.candy.android.candyapp.facebook;

import com.candy.android.candyapp.login.LoginFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

/**
 * @author Marcin
 */

public class FacebookLogin {
    public static String[] PERMISSIONS = {"email"};
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    public FacebookLogin(LoginManager loginManager) {
        this.loginManager = loginManager;
        callbackManager = CallbackManager.Factory.create();
    }

    public void setCallback(FacebookCallback<LoginResult> callback) {
        loginManager.registerCallback(callbackManager, callback);
    }

    public void loginWithEmailPermission(LoginFragment fragment) {
        loginManager.logInWithReadPermissions(fragment, Arrays.asList(PERMISSIONS));
    }
}
