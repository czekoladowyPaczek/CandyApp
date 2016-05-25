package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.login.LoginPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcingawel on 25.05.2016.
 */
@Module
public class FakePresenterModule {

    private LoginPresenter loginPresenter;

    public FakePresenterModule(LoginPresenter presenter) {
        this.loginPresenter = presenter;
    }

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return loginPresenter;
    }
}
