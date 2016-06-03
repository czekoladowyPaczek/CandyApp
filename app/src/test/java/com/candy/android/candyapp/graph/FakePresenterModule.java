package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.login.LoginPresenter;
import com.candy.android.candyapp.profile.ProfilePresenter;
import com.candy.android.candyapp.shop.ShopListPresenter;

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
    private ShopListPresenter shopListPresenter;

    public FakePresenterModule(LoginPresenter presenter) {
        this.loginPresenter = presenter;
        profilePresenter = mock(ProfilePresenter.class);
        shopListPresenter = mock(ShopListPresenter.class);
    }

    public FakePresenterModule(ProfilePresenter presenter) {
        this.profilePresenter = presenter;
        loginPresenter = mock(LoginPresenter.class);
        shopListPresenter = mock(ShopListPresenter.class);
    }

    public FakePresenterModule(ShopListPresenter shop) {
        this.shopListPresenter = shop;
        loginPresenter = mock(LoginPresenter.class);
        profilePresenter = mock(ProfilePresenter.class);
    }

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return loginPresenter;
    }

    @Provides
    public ProfilePresenter provideProfilePresenter() {
        return profilePresenter;
    }

    @Provides
    public ShopListPresenter shopListPresenter() {
        return shopListPresenter;
    }
}
