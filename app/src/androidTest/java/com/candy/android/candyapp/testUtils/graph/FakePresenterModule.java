package com.candy.android.candyapp.testUtils.graph;

import com.candy.android.candyapp.login.LoginPresenter;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by marcingawel on 25.05.2016.
 */
@Module
public class FakePresenterModule {

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return mock(LoginPresenter.class);
    }
}
