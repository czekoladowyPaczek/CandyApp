package com.candy.android.candyapp.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.zlog.ZLog;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Marcin
 */

public class ProfilePresenter {
    public static final String SAVE_PROFILE_LOADING = "com.candy.profile_loading";
    public static final String SAVE_INVITATION = "com.candy.profile_invite";
    private UserManager manager;
    private ProfileActivity activity;

    private Subscription profileSubscription;
    private Subscription friendInvitation;

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
            if (savedInstance.getBoolean(SAVE_INVITATION, false)) {
                inviteFriend("" ,true);
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

        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
    }

    public void logout() {
        manager.logout();
    }

    public void inviteFriend(String email, boolean cache) {
        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
        activity.showLoadingDialog(R.string.profile_message_inviting);
        friendInvitation = manager.inviteFriend(email, cache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    activity.setUserData(user);
                    activity.removeDialog();
                }, error -> {
                    ZLog.e("onError");
                    if (error instanceof HttpException) {
                        ResponseBody body = ((HttpException) error).response().errorBody();
                        try {
                            ZLog.e(body.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ZLog.e("some other");
                    }
                });
    }

    public void friendDialogCancelled() {
        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
    }
}
