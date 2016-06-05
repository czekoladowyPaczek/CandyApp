package com.candy.android.candyapp.profile;

import android.os.Bundle;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.ShopManager;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
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
    private ShopManager shopManager;
    private ProfilePresenter presenter;

    @Before
    public void setup() {
        activity = mock(ProfileActivity.class);
        userManager = mock(UserManager.class);
        shopManager = mock(ShopManager.class);
        presenter = new ProfilePresenter(userManager, shopManager);
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
        verify(shopManager).logout();
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
        verify(activity).showError(anyInt());
    }

    @Test
    public void assertLifecycleWhenCallsInProgress() {
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.never());
        when(userManager.inviteFriend(anyString(), anyBoolean())).thenReturn(Observable.never());

        presenter.setParent(activity, null);
        presenter.loadProfile(true);
        presenter.inviteFriend("", true);
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();

        verify(bundle).putBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, true);
        verify(bundle).putBoolean(ProfilePresenter.SAVE_INVITATION, true);
    }

    @Test
    public void assertLifecycleWhenCallsFinished() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getProfile(anyBoolean())).thenReturn(Observable.just(user));
        when(userManager.inviteFriend(anyString(), anyBoolean())).thenReturn(Observable.just(user));

        presenter.setParent(activity, null);
        presenter.loadProfile(true);
        presenter.inviteFriend("", true);
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();

        verify(bundle).putBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, false);
        verify(bundle).putBoolean(ProfilePresenter.SAVE_INVITATION, false);
    }

    @Test
    public void assertLifecycleWhenProfileCallNotStarted() {
        presenter.setParent(activity, null);
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();

        verify(bundle).putBoolean(ProfilePresenter.SAVE_PROFILE_LOADING, false);
        verify(bundle).putBoolean(ProfilePresenter.SAVE_INVITATION, false);
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
    public void shouldCallFriendInviteWhenFriendInviteWasCalledBeforeRecreate() {
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

        presenter.inviteFriend("", false);

        ArgumentCaptor<ModelUser> captor = ArgumentCaptor.forClass(ModelUser.class);
        verify(activity).removeDialog();
        verify(activity, times(2)).setUserData(captor.capture());
        assertEquals(2, captor.getValue().getFriends().size());
    }

    @Test
    public void shouldShowErrorWhenUserInviteFailed() {
        presenter.setParent(activity, null);

        when(userManager.inviteFriend(anyString(), anyBoolean())).thenReturn(Observable.error(new Throwable()));

        presenter.inviteFriend("", false);

        verify(activity).removeDialog();
        verify(activity, times(1)).setUserData(any(ModelUser.class));
        verify(activity).showError(anyInt());
    }

    @Test
    public void shouldConfirmFriend() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getUser()).thenReturn(user);
        when(userManager.acceptFriend(anyLong(), anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(activity, null);

        presenter.acceptFriend(1, false);

        verify(userManager).acceptFriend(1, false);
        verify(activity).showLoadingDialog(R.string.profile_message_accepting);
    }

    @Test
    public void shouldCallFriendAcceptWhenFriendAcceptWasCalledBeforeRecreate() {
        when(userManager.acceptFriend(anyLong(), anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(activity, null);
        presenter.acceptFriend(1, false);

        Bundle bundle = mock(Bundle.class);
        when(bundle.getBoolean(ProfilePresenter.SAVE_ACCEPT, false)).thenReturn(true);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();
        presenter.setParent(activity, bundle);

        verify(bundle).getBoolean(ProfilePresenter.SAVE_ACCEPT, false);
        verify(userManager).acceptFriend(anyLong(), eq(true));
    }

    @Test
    public void shouldUpdateUserWhenFriendAcceptIsSuccess() {
        presenter.setParent(activity, null);
        ModelUser user = UserManagerTest.getUser();
        when(userManager.acceptFriend(anyLong(), anyBoolean())).thenReturn(Observable.just(user));

        presenter.acceptFriend(1, false);

        ArgumentCaptor<ModelUser> captor = ArgumentCaptor.forClass(ModelUser.class);
        verify(activity).removeDialog();
        verify(activity, times(2)).setUserData(captor.capture());
        assertEquals(user.getFriends().size(), captor.getValue().getFriends().size());
    }

    @Test
    public void shouldShowErrorWhenFriendAcceptFailed() {
        presenter.setParent(activity, null);

        when(userManager.acceptFriend(anyLong(), anyBoolean())).thenReturn(Observable.error(new Throwable()));

        presenter.acceptFriend(1, false);

        verify(activity).removeDialog();
        verify(activity, times(1)).setUserData(any(ModelUser.class));
        verify(activity).showError(anyInt());
    }

    @Test
    public void shouldDeleteFriend() {
        ModelUser user = new ModelUser(1, "name", "pic", "email", new ArrayList<>());
        when(userManager.getUser()).thenReturn(user);
        when(userManager.deleteFriend(anyLong(), anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(activity, null);

        presenter.deleteFriend(1, false);

        verify(userManager).deleteFriend(1, false);
        verify(activity).showLoadingDialog(R.string.profile_message_deleting);
    }

    @Test
    public void shouldCallFriendDeleteWhenFriendAcceptWasCalledBeforeRecreate() {
        when(userManager.deleteFriend(anyLong(), anyBoolean())).thenReturn(Observable.never());
        presenter.setParent(activity, null);
        presenter.deleteFriend(1, false);

        Bundle bundle = mock(Bundle.class);
        when(bundle.getBoolean(ProfilePresenter.SAVE_DELETE, false)).thenReturn(true);
        presenter.onSaveInstanceState(bundle);
        presenter.removeParent();
        presenter.setParent(activity, bundle);

        verify(bundle).getBoolean(ProfilePresenter.SAVE_DELETE, false);
        verify(userManager).deleteFriend(anyLong(), eq(true));
    }

    @Test
    public void shouldUpdateUserWhenFriendDeleteIsSuccess() {
        presenter.setParent(activity, null);
        ModelUser user = UserManagerTest.getUser();
        when(userManager.deleteFriend(anyLong(), anyBoolean())).thenReturn(Observable.just(user));

        presenter.deleteFriend(1, false);

        ArgumentCaptor<ModelUser> captor = ArgumentCaptor.forClass(ModelUser.class);
        verify(activity).removeDialog();
        verify(activity, times(2)).setUserData(captor.capture());
        assertEquals(user.getFriends().size(), captor.getValue().getFriends().size());
    }

    @Test
    public void shouldShowErrorWhenFriendDeleteFailed() {
        presenter.setParent(activity, null);

        when(userManager.deleteFriend(anyLong(), anyBoolean())).thenReturn(Observable.error(new Throwable()));

        presenter.deleteFriend(1, false);

        verify(activity).removeDialog();
        verify(activity, times(1)).setUserData(any(ModelUser.class));
        verify(activity).showError(anyInt());
    }
}