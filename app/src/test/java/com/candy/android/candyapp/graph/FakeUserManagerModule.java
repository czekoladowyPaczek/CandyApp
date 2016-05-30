package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.managers.UserManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcingawel on 29.05.2016.
 */
@Module
public class FakeUserManagerModule {

    private UserManager manager;

    public FakeUserManagerModule(UserManager userManager) {
        this.manager = userManager;
    }

    @Provides
    public UserManager provideUserManager() {
        return manager;
    }
}
