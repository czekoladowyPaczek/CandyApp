package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by marcingawel on 29.05.2016.
 */
@Module
public class FakeManagerModule {

    private UserManager userManager;
    private ShopManager shopManager;

    public FakeManagerModule(UserManager userManager) {
        this.userManager = userManager;
        this.shopManager = mock(ShopManager.class);
    }

    public FakeManagerModule(ShopManager shopManager) {
        this.shopManager = shopManager;
        this.userManager = mock(UserManager.class);
    }

    @Provides
    public ShopManager shopManager() {
        return shopManager;
    }

    @Provides
    public UserManager provideUserManager() {
        return userManager;
    }
}
