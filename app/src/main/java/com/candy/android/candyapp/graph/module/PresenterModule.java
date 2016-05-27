package com.candy.android.candyapp.graph.module;

import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.login.LoginPresenter;
import com.candy.android.candyapp.managers.UserManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Marcin
 */
@Module
public class PresenterModule {
    @Provides
    public LoginPresenter provideLoginPresenter(FacebookLogin facebookLogin, UserManager manager) {
        return new LoginPresenter(facebookLogin, manager);
    }
}
