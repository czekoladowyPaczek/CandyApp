package com.candy.android.candyapp.api;

import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.model.ModelUserLogin;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

/**
 * @author Marcin
 */

public interface  CandyApi {
    String ENDPOINT = "http://52.29.51.166:3000";

    @GET("/user/login")
    Observable<ModelUserLogin> login(@Header("Authorization") String token);

    @GET("/user/profile")
    Observable<ModelUser> getProfile(@Header("Authorization") String token);
}
