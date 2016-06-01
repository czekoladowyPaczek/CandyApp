package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.request.RequestInviteFriend;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.model.ModelUserLogin;
import com.candy.android.candyapp.storage.UserStorage;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class UserManagerTest {

    private CandyApi api;
    private UserStorage storage;
    private UserManager manager;

    @Before
    public void setup() {
        api = mock(CandyApi.class);
        storage = mock(UserStorage.class);

        manager = new UserManager(api, storage);
    }

    @Test
    public void userLoggedIn() {
        when(storage.getToken()).thenReturn("test_token");

        assertTrue(manager.isLoggedIn());
    }

    @Test
    public void userNotLoggedIn() {
        when(storage.getToken()).thenReturn(null);

        assertFalse(manager.isLoggedIn());
    }

    @Test
    public void shouldReturnTokenIfUserIsLoggedIn() {
        when(storage.getToken()).thenReturn("test_token");

        assertEquals("test_token", manager.getToken());
    }

    @Test
    public void shouldReturnTokenFromCache() {
        when(storage.getToken()).thenReturn("token");
        manager.getToken();
        manager.getToken();

        verify(storage, times(1)).getToken();
    }

    @Test
    public void shouldNotReturnTokenIfUserIsNotLoggedIn() {
        assertEquals(null, manager.getToken());
    }

    @Test
    public void loginSuccess() throws Exception {
        String token = "test_token";
        ModelUserLogin login = new ModelUserLogin("response_token", null);
        when(api.login(anyString())).thenReturn(Observable.just(login));
        TestSubscriber<ModelUserLogin> sub = new TestSubscriber<>();
        manager.login(token).subscribe(sub);

        sub.assertNoErrors();
        verify(api).login(token);
        verify(storage).saveUserToken(login.getToken());
        verify(storage).saveUser(login.getUser());
        assertEquals(login, sub.getOnNextEvents().get(0));
    }

    @Test
    public void loginError() {
        String token = "test_token";
        Throwable err = new Throwable("error");
        when(api.login(anyString())).thenReturn(Observable.error(err));
        TestSubscriber<ModelUserLogin> sub = new TestSubscriber<>();
        manager.login(token).subscribe(sub);

        sub.assertError(err);
        verify(api).login(token);
        verifyZeroInteractions(storage);
    }

    @Test
    public void shouldReturnUser() {
        ModelUser user = getUser();
        when(storage.getUser()).thenReturn(user);

        ModelUser actualUser = manager.getUser();
        manager.getUser();

        verify(storage, times(1)).getUser();
        assertEquals(actualUser, user);
    }

    @Test
    public void shouldReturnNewProfileObservable() {
        ModelUser user = getUser();
        when(api.getProfile(anyString())).thenReturn(Observable.just(user));

        TestSubscriber<ModelUser> sub = new TestSubscriber<>();
        manager.getProfile(true).subscribe(sub);

        sub.assertNoErrors();
        assertEquals(user, sub.getOnNextEvents().get(0));
        verify(storage).saveUser(user);
        verify(api).getProfile(anyString());
    }

    @Test
    public void shouldGetProfileFromCache() {
        ModelUser user = getUser();
        when(api.getProfile(anyString())).thenReturn(Observable.just(user));

        TestSubscriber<ModelUser> sub = new TestSubscriber<>();
        TestSubscriber<ModelUser> cacheSub = new TestSubscriber<>();
        manager.getProfile(false).subscribe(sub);
        manager.getProfile(true).subscribe(cacheSub);

        sub.assertNoErrors();
        cacheSub.assertNoErrors();
        assertEquals(user, sub.getOnNextEvents().get(0));
        verify(storage, times(1)).saveUser(user);
        verify(api, times(1)).getProfile(anyString());
    }

    @Test
    public void shouldNotGetProfileFromCache() {
        ModelUser user = getUser();
        when(api.getProfile(anyString())).thenReturn(Observable.just(user));
        when(storage.getToken()).thenReturn("token");

        TestSubscriber<ModelUser> sub = new TestSubscriber<>();
        TestSubscriber<ModelUser> cacheSub = new TestSubscriber<>();
        manager.getProfile(false).subscribe(sub);
        manager.getProfile(false).subscribe(cacheSub);

        sub.assertNoErrors();
        cacheSub.assertNoErrors();
        assertEquals(user, sub.getOnNextEvents().get(0));
        verify(storage, times(2)).saveUser(user);
        verify(api, times(2)).getProfile("Bearer token");
    }

    @Test
    public void shouldLogout() {
        manager.logout();

        verify(storage).clear();
        assertFalse(manager.isLoggedIn());
    }

    @Test
    public void shouldSaveFriendsOnSuccessfulFriendInvitation() {
        ModelUser user = getUser();
        when(storage.getUser()).thenReturn(user);
        List<ModelFriend> friends = new ArrayList<>();
        when(api.inviteFriend(anyString(), any(RequestInviteFriend.class))).thenReturn(Observable.just(friends));

        manager.getUser();
        TestSubscriber<List<ModelFriend>> sub = new TestSubscriber<>();
        manager.inviteFriend("email@email.com", true).subscribe(sub);

        sub.assertNoErrors();
        verify(api).inviteFriend(anyString(), any(RequestInviteFriend.class));
        assertEquals(friends, sub.getOnNextEvents().get(0));
        verify(storage).saveUser(any(ModelUser.class));
        assertEquals(0, manager.getUser().getFriends().size());
    }

    @Test
    public void shouldNotSaveFriendsOnErrorFriendInvitation() {
        ModelUser user = getUser();
        when(storage.getUser()).thenReturn(user);
        Throwable err = new Throwable("");
        when(api.inviteFriend(anyString(), any(RequestInviteFriend.class))).thenReturn(Observable.error(err));

        manager.getUser();
        TestSubscriber<List<ModelFriend>> sub = new TestSubscriber<>();
        manager.inviteFriend("email@email.com", true).subscribe(sub);

        verify(api).inviteFriend(anyString(), any(RequestInviteFriend.class));
        sub.assertError(err);
        verify(storage, never()).saveUser(any(ModelUser.class));
        assertEquals(user.getFriends().size(), manager.getUser().getFriends().size());
    }

    @Test
    public void shouldReturnCachedObservable() {
        when(api.inviteFriend(anyString(), any(RequestInviteFriend.class))).thenReturn(Observable.never());
        TestSubscriber<List<ModelFriend>> sub = new TestSubscriber<>();
        manager.inviteFriend("email@email.com", true).subscribe(sub);
        TestSubscriber<List<ModelFriend>> sub2 = new TestSubscriber<>();
        manager.inviteFriend("email@email.com", true).subscribe(sub2);

        verify(api, times(1)).inviteFriend(anyString(), any(RequestInviteFriend.class));
    }

    @Test
    public void shouldReturnFreshObservable() {
        when(api.inviteFriend(anyString(), any(RequestInviteFriend.class))).thenReturn(Observable.never());
        TestSubscriber<List<ModelFriend>> sub = new TestSubscriber<>();
        manager.inviteFriend("email@email.com", true).subscribe(sub);
        TestSubscriber<List<ModelFriend>> sub2 = new TestSubscriber<>();
        manager.inviteFriend("email@email.com", false).subscribe(sub2);

        verify(api, times(2)).inviteFriend(anyString(), any(RequestInviteFriend.class));
    }

    private ModelUser getUser() {
        long id = 1;
        String name= "name";
        String picture = "http://picture.com";
        String email = "email";
        List<ModelFriend> friends = new ArrayList<>(1);
        friends.add(new ModelFriend(1, "friendName", "http://friend.com", ModelFriend.STATUS_ACCEPTED));
        return new ModelUser(id, name, picture, email, friends);
    }
}