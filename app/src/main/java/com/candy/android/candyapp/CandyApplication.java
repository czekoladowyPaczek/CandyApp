package com.candy.android.candyapp;

import android.app.Application;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.graph.component.DaggerActivityComponent;
import com.candy.android.candyapp.graph.module.ApiModule;
import com.candy.android.zlog.ZLog;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Marcin
 */

public class CandyApplication extends Application {

    private static CandyApplication app;
    private ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        ZLog.init(BuildConfig.DEBUG);
        FacebookSdk.sdkInitialize(this);

        DisplayImageOptions opts = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration imgConfig = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(opts)
                .build();
        ImageLoader.getInstance().init(imgConfig);

        activityComponent = DaggerActivityComponent.builder()
                .apiModule(new ApiModule(this))
                .build();

        app = this;
    }

    public static CandyApplication getApplication() {
        return app;
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    public void setActivityComponent(ActivityComponent activityComponent) {
        this.activityComponent = activityComponent;
    }
}
