package com.candy.android.candyapp.testUtils.graph;

import com.candy.android.candyapp.managers.ImageUploadManager;
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
    private ImageUploadManager imageManager;

    public FakeUserManagerModule() {
        userManager = mock(UserManager.class);
        imageManager = mock(ImageUploadManager.class);
    }

    public FakeUserManagerModule(UserManager manager) {
        this();
        this.userManager = manager;
    }

    public FakeUserManagerModule(ImageUploadManager manager) {
        this();
        this.imageManager = manager;
    }

    public FakeUserManagerModule(UserManager user, ImageUploadManager image) {
        this.userManager = user;
        this.imageManager = image;
    }

    @Provides
    public UserManager provideManager() {
        return userManager;
    }

    @Provides
    public ImageUploadManager imageUploadManager() {
        return imageManager;
    }
}
