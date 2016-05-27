package com.candy.android.candyapp.login;

import android.widget.Button;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.graph.FakeActivityComponent;
import com.candy.android.candyapp.graph.FakePresenterModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by marcingawel on 25.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginFragmentTest {

    LoginFragment fragment;
    LoginPresenter presenter;

    @Before
    public void setup() {
        presenter = mock(LoginPresenter.class);
        FakeActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakePresenterModule(new FakePresenterModule(presenter))
                .build();
        ((CandyApplication) RuntimeEnvironment.application).setActivityComponent(component);

        fragment = new LoginFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment);
    }

    @Test
    public void testLifecycle() {
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
}