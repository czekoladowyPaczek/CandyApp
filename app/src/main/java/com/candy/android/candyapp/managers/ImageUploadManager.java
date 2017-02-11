package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.ImgurApi;
import com.candy.android.candyapp.model.UploadedImage;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * @author Marcin
 */
public class ImageUploadManager {
    private static final String AUTHENTICATION = "Client-ID ";
    public static final String CLIENT_ID = "f6b95273d1d7f1d";
    private ImgurApi api;

    public ImageUploadManager(ImgurApi api) {
        this.api = api;
    }

    public Observable<UploadedImage> uploadImage(String path) {
        return api.postImage(AUTHENTICATION + CLIENT_ID, RequestBody.create(MediaType.parse("image/*"), new File(path)));
    }
}
