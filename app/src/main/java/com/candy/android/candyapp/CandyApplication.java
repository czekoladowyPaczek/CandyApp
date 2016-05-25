package com.candy.android.candyapp;

import android.app.Application;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.graph.component.DaggerActivityComponent;
import com.facebook.FacebookSdk;

/**
 * @author Marcin
 */

public class CandyApplication extends Application {

    private ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(this);

        activityComponent = DaggerActivityComponent.builder()
                .build();
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
