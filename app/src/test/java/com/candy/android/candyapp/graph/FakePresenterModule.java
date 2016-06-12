package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.login.LoginPresenter;
import com.candy.android.candyapp.profile.ProfilePresenter;
import com.candy.android.candyapp.shop.ShopDetailPresenter;
import com.candy.android.candyapp.shop.ShopFriendPresenter;
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
    private ShopDetailPresenter shopDetailPresenter;
    private ShopFriendPresenter shopFriendPresenter;

    private FakePresenterModule() {
        loginPresenter = mock(LoginPresenter.class);
        profilePresenter = mock(ProfilePresenter.class);
        shopListPresenter = mock(ShopListPresenter.class);
        shopDetailPresenter = mock(ShopDetailPresenter.class);
    }

    public FakePresenterModule(LoginPresenter presenter) {
        this();
        this.loginPresenter = presenter;
    }

    public FakePresenterModule(ProfilePresenter presenter) {
        this();
        this.profilePresenter = presenter;
    }

    public FakePresenterModule(ShopListPresenter shop) {
        this();
        this.shopListPresenter = shop;
    }

    public FakePresenterModule(ShopDetailPresenter shop) {
        this();
        this.shopDetailPresenter = shop;
    }

    public FakePresenterModule(ShopFriendPresenter presenter) {
        this();
        this.shopFriendPresenter = presenter;
    }

    @Provides
    LoginPresenter provideLoginPresenter() {
        return loginPresenter;
    }

    @Provides
    ProfilePresenter provideProfilePresenter() {
        return profilePresenter;
    }

    @Provides
    ShopListPresenter shopListPresenter() {
        return shopListPresenter;
    }

    @Provides
    ShopDetailPresenter shopDetailPresenter() {
        return shopDetailPresenter;
    }

    @Provides
    ShopFriendPresenter shopFriendPresenter() {
        return shopFriendPresenter;
    }
}
