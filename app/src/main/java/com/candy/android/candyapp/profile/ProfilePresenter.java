package com.candy.android.candyapp.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.api.ModelError;
import com.candy.android.candyapp.managers.ShopManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelUser;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Marcin
 */

public class ProfilePresenter {
    public static final String SAVE_PROFILE_LOADING = "com.candy.profile_loading";
    public static final String SAVE_INVITATION = "com.candy.profile_invite";
    public static final String SAVE_ACCEPT = "com.candy.profile_accept";
    public static final String SAVE_DELETE = "com.candy.profile_delete";
    private UserManager manager;
    private ShopManager shopManager;
    private ProfileActivity activity;

    private Subscription profileSubscription;
    private Subscription friendInvitation;

    public ProfilePresenter(UserManager manager, ShopManager shopManager) {
        this.manager = manager;
        this.shopManager = shopManager;
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
                inviteFriend("", true);
            }
            if (savedInstance.getBoolean(SAVE_ACCEPT, false)) {
                acceptFriend(1, true);
            }
            if (savedInstance.getBoolean(SAVE_DELETE, false)) {
                deleteFriend(1, true);
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
                            activity.cancelLoading();
                            if (error instanceof HttpException) {
                                activity.showError(R.string.error_unknown);
                            } else {
                                activity.showError(R.string.error_connection);
                            }
                        });
    }

    public void onSaveInstanceState(Bundle instance) {
        instance.putBoolean(SAVE_PROFILE_LOADING, profileSubscription != null && !profileSubscription.isUnsubscribed());
        instance.putBoolean(SAVE_INVITATION, friendInvitation != null && !friendInvitation.isUnsubscribed());
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
        shopManager.logout();
    }

    public void inviteFriend(String email, boolean cache) {
        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
        activity.showLoadingDialog(R.string.profile_message_inviting);
        Observable<ModelUser> call = manager.inviteFriend(email, cache);
        friendInvitation = subscribeToFriendCall(call);
    }

    public void acceptFriend(long id, boolean cache) {
        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
        activity.showLoadingDialog(R.string.profile_message_accepting);
        Observable<ModelUser> call = manager.acceptFriend(id, cache);
        friendInvitation = subscribeToFriendCall(call);
    }

    public void deleteFriend(long id, boolean cache) {
        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
        activity.showLoadingDialog(R.string.profile_message_deleting);
        Observable<ModelUser> call = manager.deleteFriend(id, cache);
        friendInvitation = subscribeToFriendCall(call);
    }

    private Subscription subscribeToFriendCall(Observable<ModelUser> friendCall) {
        return friendCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    activity.setUserData(user);
                    activity.removeDialog();
                }, error -> {
                    activity.removeDialog();
                    activity.showError(ModelError.fromRetrofit(error).getResourceMessage());
                });
    }

    public void friendDialogCancelled() {
        if (friendInvitation != null && !friendInvitation.isUnsubscribed()) {
            friendInvitation.unsubscribe();
        }
    }
}
