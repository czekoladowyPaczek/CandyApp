package com.candy.android.candyapp.graph.component;

import com.candy.android.candyapp.graph.module.FacebookModule;
import com.candy.android.candyapp.graph.module.PresenterModule;
import com.candy.android.candyapp.login.LoginFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Marcin
 */
@Singleton
@Component(modules = {PresenterModule.class, FacebookModule.class})
public interface ActivityComponent {
    void inject(LoginFragment fragment);
}
