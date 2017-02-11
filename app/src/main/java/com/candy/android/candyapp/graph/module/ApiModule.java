package com.candy.android.candyapp.graph.module;

import android.content.Context;

import com.candy.android.candyapp.api.CandyApi;
import com.candy.android.candyapp.api.ImgurApi;
import com.candy.android.candyapp.storage.ShopMemoryStorage;
import com.candy.android.candyapp.storage.UserStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(CandyApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(CandyApi.class);
    }

    @Provides
    @Singleton
    ImgurApi imgurApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(ImgurApi.URL)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(ImgurApi.class);
    }

    private Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
    }

    @Provides
    UserStorage provideUserStorage() {
        return new UserStorage(context);
    }

    @Provides
    @Singleton
    ShopMemoryStorage shopMemoryStorage() {
        return new ShopMemoryStorage();
    }
}
