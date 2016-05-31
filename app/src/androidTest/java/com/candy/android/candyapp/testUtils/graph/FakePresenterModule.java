package com.candy.android.candyapp.testUtils.graph;

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

    private LoginPresenter login;
    private ProfilePresenter profile;

    public FakePresenterModule() {
        login = mock(LoginPresenter.class);
        profile = mock(ProfilePresenter.class);
    }

    public FakePresenterModule(LoginPresenter login, ProfilePresenter profile) {
        this.login = login;
        this.profile = profile;
    }

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return login;
    }

    @Provides
    ProfilePresenter provideProfilePresenter() {
        return profile;
    }
}
