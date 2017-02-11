package com.candy.android.candyapp.graph;

import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.graph.module.ApiModule;
import com.candy.android.candyapp.graph.module.UtilModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcingawel on 25.05.2016.
 */
@Singleton
@Component(modules = {FakePresenterModule.class, FakeManagerModule.class, UtilModule.class, ApiModule.class})
public interface FakeActivityComponent extends ActivityComponent {
}
