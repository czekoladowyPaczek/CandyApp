package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.login.LoginPresenter;
import com.candy.android.candyapp.profile.ProfilePresenter;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by marcingawel on 25.05.2016.
 */
@Module
public class FakePresenterModule {

    private LoginPresenter loginPresenter;
    private ProfilePresenter profilePresenter;

    public FakePresenterModule(LoginPresenter presenter) {
        this.loginPresenter = presenter;
        profilePresenter = mock(ProfilePresenter.class);
    }

    public FakePresenterModule(ProfilePresenter presenter) {
        this.profilePresenter = presenter;
        loginPresenter = mock(LoginPresenter.class);
    }

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return loginPresenter;
    }

    @Provides
    public ProfilePresenter provideProfilePresenter() {
        return profilePresenter;
    }
}
