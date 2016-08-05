package com.candy.android.candyapp.graph.module;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.storage.ShopMemoryStorage;
import com.candy.android.candyapp.storage.UserStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Marcin
 */

@Module
public class ManagerModule {
    @Provides
    @Singleton
    public UserManager provideUserManager(CandyApi api, UserStorage storage) {
        return new UserManager(api, storage);
    }

    @Provides
    @Singleton
    public ShopManager provideShopManager(CandyApi api, UserManager manager, ShopMemoryStorage memory) {
        return new ShopManager(manager, api, memory);
    }
}
