package com.candy.android.candyapp;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.graph.component.DaggerActivityComponent;
import com.candy.android.candyapp.graph.module.ApiModule;
import com.candy.android.candyapp.graph.module.UtilModule;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import eu.boiled.android.zlog.ZLog;
import io.fabric.sdk.android.Fabric;

public class CandyApplication extends Application {
    private static CandyApplication app;
    private ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
        activityComponent = createActivityComponent();

        app = this;
    }

    public static CandyApplication getApplication() {
        return app;
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    private ActivityComponent createActivityComponent() {
        return DaggerActivityComponent.builder()
                .apiModule(new ApiModule(this))
                .utilModule(new UtilModule(this))
                .build();
    }

    protected void init() {
        Fabric.with(this, new Crashlytics());
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
    }

    @VisibleForTesting
    public void setActivityComponent(ActivityComponent activityComponent) {
        this.activityComponent = activityComponent;
    }
}
