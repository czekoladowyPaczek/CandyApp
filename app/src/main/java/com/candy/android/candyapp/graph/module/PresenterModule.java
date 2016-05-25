package com.candy.android.candyapp.graph.module;

import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.login.LoginPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Marcin
 */
@Module
public class PresenterModule {
    @Provides
    public LoginPresenter provideLoginPresenter(FacebookLogin facebookLogin) {
        return new LoginPresenter(facebookLogin);
    }
}
