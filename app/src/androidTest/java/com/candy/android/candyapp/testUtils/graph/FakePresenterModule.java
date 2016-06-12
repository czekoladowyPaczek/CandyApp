package com.candy.android.candyapp.testUtils.graph;

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

    private LoginPresenter login;
    private ProfilePresenter profile;
    private ShopListPresenter shopList;
    private ShopDetailPresenter shopDetail;
    private ShopFriendPresenter shopFriend;

    public FakePresenterModule() {
        login = mock(LoginPresenter.class);
        profile = mock(ProfilePresenter.class);
        shopList = mock(ShopListPresenter.class);
        shopDetail = mock(ShopDetailPresenter.class);
        shopFriend = mock(ShopFriendPresenter.class);
    }

    public FakePresenterModule(LoginPresenter login, ProfilePresenter profile) {
        this.login = login;
        this.profile = profile;
    }

    public FakePresenterModule(ShopListPresenter shop) {
        this();
        this.shopList = shop;
    }

    public FakePresenterModule(ShopDetailPresenter presenter) {
        this();
        shopDetail = presenter;
    }

    public FakePresenterModule(ShopListPresenter list, ShopDetailPresenter detail) {
        this();
        this.shopList = list;
        this.shopDetail = detail;
    }

    public FakePresenterModule(ShopFriendPresenter presenter) {
        this();
        this.shopFriend = presenter;
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

    @Provides
    ShopDetailPresenter shopDetailPresenter() {
        return shopDetail;
    }

    @Provides
    ShopFriendPresenter shopFriendPresenter() {
        return shopFriend;
    }
}
