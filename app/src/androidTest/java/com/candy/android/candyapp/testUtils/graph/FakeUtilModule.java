package com.candy.android.candyapp.testUtils.graph;

import com.candy.android.candyapp.util.PermissionsHelper;
import com.candy.android.candyapp.util.PictureSelectHelper;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * @author Marcin
 */
@Module
public class FakeUtilModule {

    private PictureSelectHelper pictureSelectHelper;
    private PermissionsHelper permissionsHelper;

    public FakeUtilModule() {
        pictureSelectHelper = mock(PictureSelectHelper.class);
        permissionsHelper = mock(PermissionsHelper.class);
    }

    public FakeUtilModule(PictureSelectHelper pictureSelectHelper, PermissionsHelper permissionsHelper) {
        this.pictureSelectHelper = pictureSelectHelper;
        this.permissionsHelper = permissionsHelper;
    }

    @Provides
    PictureSelectHelper pictureSelectHelper() {
        return pictureSelectHelper;
    }

    @Provides
    PermissionsHelper permissionsHelper() {
        return permissionsHelper;
    }
}
