package com.candy.android.candyapp.graph.module;

import android.content.Context;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.storage.UserStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Marcin
 */
@Module
public class ApiModule {
    private Context context;

    public ApiModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    CandyApi provideCandyApi() {
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CandyApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(CandyApi.class);
    }

    @Provides
    UserStorage provideUserStorage() {
        return new UserStorage(context);
    }
}
