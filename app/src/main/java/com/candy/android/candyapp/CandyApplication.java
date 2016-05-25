package com.candy.android.candyapp;

import android.app.Application;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.graph.component.DaggerActivityComponent;
import com.candy.android.zlog.ZLog;
import com.facebook.FacebookSdk;

/**
 * @author Marcin
 */

public class CandyApplication extends Application {

    private ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        ZLog.init(BuildConfig.DEBUG);
        FacebookSdk.sdkInitialize(this);

        activityComponent = DaggerActivityComponent.builder()
                .build();
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    public void setActivityComponent(ActivityComponent activityComponent) {
        this.activityComponent = activityComponent;
    }
}
