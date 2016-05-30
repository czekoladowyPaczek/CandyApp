package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.model.ModelUserLogin;
import com.candy.android.candyapp.storage.UserStorage;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
}