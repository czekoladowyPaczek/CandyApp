package com.candy.android.candyapp.login;

import android.content.Intent;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.ModelUserLogin;
import com.candy.android.candyapp.testUtils.RxSchedulersOverrideRule;
import com.facebook.FacebookException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class LoginPresenterTest {

    private LoginPresenter presenter;
    private FacebookLogin facebook;
    private LoginFragment fragment;
    private UserManager userManager;

    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Before
    public void setup() {
        fragment = mock(LoginFragment.class);
        facebook = mock(FacebookLogin.class);
        userManager = mock(UserManager.class);
        presenter = new LoginPresenter(facebook, userManager);
        presenter.setParent(fragment);
    }

    @Test
    public void startFacebookLogin() throws Exception {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.never());

        presenter.startFacebookLogin();
        presenter.onActivityResult(1, 1, mock(Intent.class));

        ArgumentCaptor<LoginFragment> argument = ArgumentCaptor.forClass(LoginFragment.class);
        verify(facebook).loginWithEmailPermission(argument.capture());
        assertEquals(fragment, argument.getValue());
        verify(fragment).showLoadingView();
        verify(facebook).onActivityResult(eq(1), eq(1), any(Intent.class));
    }

    @Test
    public void shouldCallApiWhenFacebookCompleted() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.just("token"));

        presenter.startFacebookLogin();

        verify(userManager).login("Bearer " + "token");
    }

    @Test
    public void shouldUnsubscribeIfLoginInProgress() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.never());
        when(userManager.login(anyString())).thenReturn(Observable.never());

        presenter.startFacebookLogin();
        presenter.removeParent();
    }

    @Test
    public void shouldNotUnsubscribeIfLoginNotInProgress() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.never());
        when(userManager.login(anyString())).thenReturn(Observable.never());

        presenter.removeParent();
    }

    @Test
    public void shouldSubscribeAndShowLoadingViewIfLoginWasInProgressAndRecreated() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.never());
        when(userManager.login(anyString())).thenReturn(Observable.never());

        presenter.startFacebookLogin();
        presenter.removeParent();
        presenter.setParent(fragment);
        verify(fragment, times(2)).showLoadingView();
    }

    @Test
    public void shouldDoNothingWhenUserCancelsFacebookLogin() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.error(new FacebookLogin.FacebookCancelException()));
        when(userManager.login(anyString())).thenReturn(Observable.never());

        presenter.startFacebookLogin();
        verify(fragment).showLoadingView();
        verify(fragment).removeLoadingView();
        verifyNoMoreInteractions(fragment);
    }

    @Test
    public void shouldShowErrorOnFacebookError() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.error(new FacebookException()));
        when(userManager.login(anyString())).thenReturn(Observable.never());

        presenter.startFacebookLogin();

        verify(fragment).removeLoadingView();
        verify(fragment).showError(R.string.login_error_facebook);
    }

    @Test
    public void shouldShowErrorOnApiError() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.just("token"));
        when(userManager.login(anyString())).thenReturn(Observable.error(new Throwable("api")));

        presenter.startFacebookLogin();

        verify(fragment).removeLoadingView();
        verify(fragment).showError(R.string.login_error_api);
    }

    @Test
    public void shouldCallSuccessOnLoginInSuccess() {
        when(facebook.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.just("token"));
        when(userManager.login(anyString())).thenReturn(Observable.just(new ModelUserLogin("api_token", null)));

        presenter.startFacebookLogin();

        verify(fragment).removeLoadingView();
        verify(fragment).onLoginSuccess();
    }
}