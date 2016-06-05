package com.candy.android.candyapp.graph.module;

import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.login.LoginPresenter;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.profile.ProfilePresenter;
import com.candy.android.candyapp.shop.ShopListPresenter;

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

    @Provides
    ProfilePresenter provideProfilePresenter(UserManager manager, ShopManager shopManager) {
        return new ProfilePresenter(manager, shopManager);
    }

    @Provides
    ShopListPresenter provideShopListPresenter(ShopManager manager) {
        return new ShopListPresenter(manager);
    }
}
