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
    private String clientId;
    private ImgurApi api;

    public ImageUploadManager(String clientId, ImgurApi api) {
        this.api = api;
        this.clientId = clientId;
    }

    public Observable<UploadedImage> uploadImage(String path) {
        return api.postImage(AUTHENTICATION + clientId, RequestBody.create(MediaType.parse("image/*"), new File(path)));
    }
}
