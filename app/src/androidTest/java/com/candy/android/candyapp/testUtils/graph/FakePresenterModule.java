package com.candy.android.candyapp.testUtils.graph;

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

    private LoginPresenter login;
    private ProfilePresenter profile;
    private ShopListPresenter shopList;

    public FakePresenterModule() {
        login = mock(LoginPresenter.class);
        profile = mock(ProfilePresenter.class);
        shopList = mock(ShopListPresenter.class);
    }

    public FakePresenterModule(LoginPresenter login, ProfilePresenter profile) {
        this.login = login;
        this.profile = profile;
    }

    public FakePresenterModule(ShopListPresenter shop) {
        this();
        this.shopList = shop;
    }

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return login;
    }

    @Provides
    ProfilePresenter provideProfilePresenter() {
        return profile;
    }

    @Provides
    ShopListPresenter provideShopListPresenter() {
        return shopList;
    }
}
