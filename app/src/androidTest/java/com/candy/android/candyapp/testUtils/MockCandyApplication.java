package com.candy.android.candyapp.testUtils;

import android.support.annotation.NonNull;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.graph.component.ActivityComponent;


public class MockCandyApplication extends CandyApplication {
    private ActivityComponent activityComponent;

    public void setActivityComponent(@NonNull ActivityComponent activityComponent) {
        this.activityComponent = activityComponent;
    }

    @Override
    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    @Override
    protected void init() {}
}
