package com.candy.android.candyapp.login;

import com.candy.android.candyapp.facebook.FacebookLogin;

/**
 * @author Marcin
 */

public class LoginPresenter {
    private FacebookLogin facebookLogin;

    private LoginFragment fragment;

    public LoginPresenter(FacebookLogin facebookLogin) {
        this.facebookLogin = facebookLogin;
    }

    public void setParent(LoginFragment parent) {
        this.fragment = parent;
    }

    public void removeParent() {
        this.fragment = null;
    }
}
