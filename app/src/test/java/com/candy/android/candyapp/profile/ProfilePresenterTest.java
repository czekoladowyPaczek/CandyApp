package com.candy.android.candyapp.profile;

import android.os.Bundle;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.managers.UserManagerTest;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.testUtils.RxSchedulersOverrideRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class ProfilePresenterTest {

    private ProfileActivity activity;
    private UserManager userManager;
    private ProfilePresenter presenter;

    @Before
    public void setup() {
        activity = mock(ProfileActivity.class);
        userManager = mock(UserManager.class);
        presenter = new ProfilePresenter(userManager);
    }

    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Test
    public void shouldInitializeActivityWhenNotSaved() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getUser()).thenReturn(user);

        presenter.setParent(activity, null);

        verify(userManager).getUser();
        verify(activity).setUserData(user);
    }

    @Test
    public void shouldInitializeActivityWhenProfileSaved() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getUser()).thenReturn(user);
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.never());
        Bundle bundle = mock(Bundle.class);
        when(bundle.getBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, false)).thenReturn(true);
        presenter.setParent(activity, bundle);

        verify(activity).showLoading();
        verify(userManager).getUser();
        verify(activity).setUserData(user);
        verify(userManager).getProfile(true);
    }

    @Test
    public void shouldLogout() {
        presenter.logout();

        verify(userManager).logout();
    }

    @Test
    public void shouldSetDataOnProfileSuccess() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.just(user));

        presenter.setParent(activity, null);
        presenter.loadProfile(true);

        verify(activity).cancelLoading();
        verify(activity).setUserData(null);
        verify(activity).setUserData(user);
        verify(userManager).getProfile(true);
    }

    @Test
    public void shouldShowErrorOnProfileError() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.error(new Throwable("")));

        presenter.setParent(activity, null);
        presenter.loadProfile(true);

        verify(activity).cancelLoading();
        verify(activity).setUserData(null);
        verify(activity, never()).setUserData(user);
        verify(userManager).getProfile(true);
        verify(activity).showError();
    }

    @Test
    public void assertLifecycleWhenProfileCallInProgress() {
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.never());

        presenter.setParent(activity, null);
        presenter.loadProfile(true);
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();

        verify(bundle).putBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, true);
    }

    @Test
    public void assertLifecycleWhenProfileCallFinished() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.just(user));

        presenter.setParent(activity, null);
        presenter.loadProfile(true);
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();

        verify(bundle).putBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, false);
    }

    @Test
    public void assertLifecycleWhenProfileCallNotStarted() {
        presenter.setParent(activity, null);
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();

        verify(bundle).putBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, false);
    }

    @Test
    public void shouldAddFriend() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getUser()).thenReturn(user);
        when(userManager.inviteFriend(anyString(), anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(activity, null);

        presenter.inviteFriend("email@email.com", false);

        verify(userManager).inviteFriend("email@email.com", false);
        verify(activity).showLoadingDialog(R.string.profile_message_inviting);
    }

    @Test
    public void shouldCallFriendInviteWhenFriendInviteWasCalledBeforeRecrete() {
        when(userManager.inviteFriend(anyString(), anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(activity, null);
        presenter.inviteFriend("", false);

        Bundle bundle = mock(Bundle.class);
        when(bundle.getBoolean(ProfilePresenter.SAVE_INVITATION, false)).thenReturn(true);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();
        presenter.setParent(activity, bundle);

        verify(bundle).getBoolean(ProfilePresenter.SAVE_INVITATION, false);
        verify(userManager).inviteFriend(anyString(), eq(true));
    }

    @Test
    public void shouldUpdateUserWhenFriendInvitationIsSuccess() {
        presenter.setParent(activity, null);
        List<ModelFriend> friends = new ArrayList<>(2);
        friends.add(new ModelFriend(1, "name", "", ModelFriend.STATUS_INVITED));
        friends.add(new ModelFriend(2, "name 1", "", ModelFriend.STATUS_INVITED));
        ModelUser user = UserManagerTest.getUser();
        user.setFriends(friends);
        when(userManager.inviteFriend(anyString(), anyBoolean())).thenReturn(Observable.just(user));
        ModelUser modelUser = UserManagerTest.getUser();
        when(userManager.getUser()).thenReturn(modelUser);

        presenter.inviteFriend("", false);

        ArgumentCaptor<ModelUser> captor = ArgumentCaptor.forClass(ModelUser.class);
        verify(activity).removeDialog();
        verify(activity, times(2)).setUserData(captor.capture());
        assertEquals(2, captor.getValue().getFriends().size());
    }
}