package com.candy.android.candyapp.graph.component;

import com.candy.android.candyapp.graph.module.ApiModule;
import com.candy.android.candyapp.graph.module.FacebookModule;
import com.candy.android.candyapp.graph.module.ManagerModule;
import com.candy.android.candyapp.graph.module.PresenterModule;
import com.candy.android.candyapp.login.LoginActivity;
import com.candy.android.candyapp.login.LoginFragment;
import com.candy.android.candyapp.profile.ProfileActivity;
import com.candy.android.candyapp.shop.ShopDetailFragment;
import com.candy.android.candyapp.shop.ShopListFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Marcin
 */
@Singleton
@Component(modules = {PresenterModule.class, FacebookModule.class, ManagerModule.class, ApiModule.class})
public interface ActivityComponent {
    void inject(LoginActivity activity);
    void inject(LoginFragment fragment);
    void inject(ProfileActivity activity);
    void inject(ShopListFragment fragment);
    void inject(ShopDetailFragment fragment);
}
