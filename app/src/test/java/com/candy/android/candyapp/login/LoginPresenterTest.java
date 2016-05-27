package com.candy.android.candyapp.login;

import android.content.Intent;

import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.testUtils.RxSchedulersOverrideRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        verify(facebook).onActivityResult(eq(1), eq(1), any(Intent.class));
    }
}