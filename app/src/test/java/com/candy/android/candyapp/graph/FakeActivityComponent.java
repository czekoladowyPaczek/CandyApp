package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.login.LoginFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcingawel on 25.05.2016.
 */
@Singleton
@Component(modules = {FakePresenterModule.class})
public interface FakeActivityComponent extends ActivityComponent {
    void inject(LoginFragment fragment);
}
