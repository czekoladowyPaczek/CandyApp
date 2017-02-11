package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.managers.ImageUploadManager;
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
    private ImageUploadManager imageUploadManager;

    public FakeManagerModule(UserManager userManager) {
        this.userManager = userManager;
        this.shopManager = mock(ShopManager.class);
        this.imageUploadManager = mock(ImageUploadManager.class);
    }

    public FakeManagerModule(ShopManager shopManager) {
        this.shopManager = shopManager;
        this.userManager = mock(UserManager.class);
        this.imageUploadManager = mock(ImageUploadManager.class);
    }

    @Provides
    public ShopManager shopManager() {
        return shopManager;
    }

    @Provides
    public UserManager provideUserManager() {
        return userManager;
    }

    @Provides
    ImageUploadManager imageUploadManager() {
        return imageUploadManager;
    }
}
