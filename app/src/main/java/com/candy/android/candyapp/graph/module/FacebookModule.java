package com.candy.android.candyapp.graph.module;

import com.candy.android.candyapp.facebook.FacebookLogin;
import com.facebook.login.LoginManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Marcin
 */
@Module
public class FacebookModule {

    @Provides
    FacebookLogin provideFacebookLogin() {
        return new FacebookLogin(LoginManager.getInstance());
    }
}
