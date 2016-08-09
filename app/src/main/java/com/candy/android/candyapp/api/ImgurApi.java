package com.candy.android.candyapp.api;

import com.candy.android.candyapp.model.UploadedImage;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author Marcin
 */
public interface ImgurApi {
    String URL = "https://api.imgur.com";

    @POST("/3/image")
    Observable<UploadedImage> postImage(@Header("Authorization") String auth, @Body RequestBody image);
}
