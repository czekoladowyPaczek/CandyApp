package com.candy.android.candyapp.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.candy.android.candyapp.managers.UserManager;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Marcin
 */

public class ProfilePresenter {
    public static final String SAVE_PROFILE_LOADING = "com.candy.profile_loading";
    private UserManager manager;
    private ProfileActivity activity;

    private Subscription profileSubscription;

    public ProfilePresenter(UserManager manager) {
        this.manager = manager;
    }

    public void setParent(@NonNull ProfileActivity activity, @Nullable Bundle savedInstance) {
        this.activity = activity;

        activity.setUserData(manager.getUser());
        if (savedInstance != null) {
            if (savedInstance.getBoolean(SAVE_PROFILE_LOADING, false)) {
                activity.showLoading();
                loadProfile(true);
            }
        }
    }

    public void loadProfile(boolean cache) {
        if (profileSubscription != null && !profileSubscription.isUnsubscribed()) {
            profileSubscription.unsubscribe();
        }

        profileSubscription = manager.getProfile(cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                            activity.cancelLoading();
                            activity.setUserData(user);
                        },
                        error -> {
                            error.printStackTrace();
                            activity.cancelLoading();
                            activity.showError();
                        });
    }

    public void onSaveInstanceState(Bundle instance) {
        instance.putBoolean(SAVE_PROFILE_LOADING, profileSubscription != null && !profileSubscription.isUnsubscribed());
    }

    public void removeParent() {
        if (profileSubscription != null && !profileSubscription.isUnsubscribed()) {
            profileSubscription.unsubscribe();
        }
    }

    public void logout() {
        manager.logout();
    }
}
