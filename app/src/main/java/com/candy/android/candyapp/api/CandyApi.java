package com.candy.android.candyapp.api;

import com.candy.android.candyapp.api.request.RequestAcceptFriend;
import com.candy.android.candyapp.api.request.RequestCreateShopList;
import com.candy.android.candyapp.api.request.RequestInviteFriend;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.candyapp.model.ModelUserLogin;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @POST("/friend")
    Observable<List<ModelFriend>> inviteFriend(@Header("Authorization") String token, @Body RequestInviteFriend body);
    @POST("/friend")
    Observable<List<ModelFriend>> acceptFriend(@Header("Authorization") String token, @Body RequestAcceptFriend body);
    @DELETE("/friend/{id}")
    Observable<List<ModelFriend>> deleteFriend(@Header("Authorization") String token, @Path("id") long id);

    @GET("/shop")
    Observable<List<ModelShop>> getShopLists(@Header("Authorization") String token);
    @POST("/shop")
    Observable<ModelShop> createShopList(@Header("Authorization") String token, @Body RequestCreateShopList body);
    @DELETE("/shop/{id}")
    Observable<ModelResponseSimple> deleteShopList(@Header("Authorization") String token, @Path("id") String id);

    @GET("/shop/{id}/item")
    Observable<List<ModelShopItem>> getItems(@Header("Authorization") String token, @Path("id") String id);
}
