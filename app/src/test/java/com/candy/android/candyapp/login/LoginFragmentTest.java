package com.candy.android.candyapp.login;

import android.app.AlertDialog;
import android.widget.Button;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.facebook.FacebookLogin;
import com.candy.android.candyapp.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.graph.FakeActivityComponent;
import com.candy.android.candyapp.graph.FakePresenterModule;
import com.candy.android.candyapp.managers.UserManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowProgressDialog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by marcingawel on 25.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginFragmentTest {

    private FacebookLogin facebookLogin;
    private UserManager userManager;
    private LoginFragment fragment;
    private LoginPresenter presenter;

    @Before
    public void setup() {
        facebookLogin = mock(FacebookLogin.class);
        userManager = mock(UserManager.class);
        when(facebookLogin.loginWithEmailPermission(any(LoginFragment.class))).thenReturn(Observable.never());
        when(userManager.login(anyString())).thenReturn(Observable.never());
        presenter = spy(new LoginPresenter(facebookLogin, userManager));
        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter))
                .build();
        ((CandyApplication) RuntimeEnvironment.application).setActivityComponent(component);

        fragment = new LoginFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment);
    }

    @Test
    public void testLifecycleWithNoLogin() {
        fragment.onDestroyView();
        verify(presenter).setParent(fragment);
        verify(presenter).removeParent();
    }

    @Test
    public void testLifecycleWithLogin() {
        presenter.startFacebookLogin();
        fragment.onDestroyView();
        verify(presenter).setParent(fragment);
        verify(presenter).removeParent();
    }

    @Test
    public void shouldStartLogin() {
        Button b = (Button) fragment.getView().findViewById(R.id.loginButton);
        b.performClick();

        verify(presenter).startFacebookLogin();
    }

    @Test
    public void shouldShowLoadingView() {
        fragment.showLoadingView();

        AlertDialog dialog = ShadowProgressDialog.getLatestAlertDialog();
        ShadowAlertDialog d = shadowOf(dialog);
        assertEquals(RuntimeEnvironment.application.getString(R.string.login_login_in_progress), d.getMessage());
    }
}