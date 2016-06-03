package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.login.LoginActivity;
import com.candy.android.candyapp.login.LoginFragment;
import com.candy.android.candyapp.profile.ProfileActivity;
import com.candy.android.candyapp.shop.ShopListFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcingawel on 25.05.2016.
 */
@Singleton
@Component(modules = {FakePresenterModule.class, FakeManagerModule.class})
public interface FakeActivityComponent extends ActivityComponent {
    void inject(LoginActivity activity);
    void inject(LoginFragment fragment);
    void inject(ProfileActivity activity);
    void inject(ShopListFragment fragment);
}
