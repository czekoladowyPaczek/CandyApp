package com.candy.android.candyapp.testUtils.graph;

import com.candy.android.candyapp.managers.UserManager;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * @author Marcin
 */
@Module
public class FakeUserManagerModule {

    private UserManager userManager;

    public FakeUserManagerModule() {
        userManager = mock(UserManager.class);
    }

    public FakeUserManagerModule(UserManager manager) {
        this.userManager = manager;
    }

    @Provides
    public UserManager provideManager() {
        return userManager;
    }
}
