package com.candy.android.candyapp.graph.module;

import android.content.Context;

import com.candy.android.candyapp.util.PermissionsHelper;
import com.candy.android.candyapp.util.PictureSelectHelper;

import dagger.Module;
import dagger.Provides;

/**
 * @author Marcin
 */
@Module
public class UtilModule {

    private Context context;

    public UtilModule(Context context) {
        this.context = context;
    }

    @Provides
    PictureSelectHelper pictureSelectHelper() {
        return new PictureSelectHelper(context);
    }

    @Provides
    PermissionsHelper permissionsHelper() {
        return new PermissionsHelper();
    }
}
