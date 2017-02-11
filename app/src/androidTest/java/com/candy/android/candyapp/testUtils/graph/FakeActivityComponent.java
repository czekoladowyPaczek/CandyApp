package com.candy.android.candyapp.testUtils.graph;

import com.candy.android.candyapp.graph.component.ActivityComponent;
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
@Component(modules = {FakeUserManagerModule.class, FakePresenterModule.class, FakeUtilModule.class})
public interface FakeActivityComponent extends ActivityComponent {
    void inject(LoginFragment fragment);
    void inject(ProfileActivity activity);
    void inject(ShopListFragment fragment);
    void inject(ShopDetailFragment fragment);
}
