package com.candy.android.candyapp.managers;

import com.candy.android.candyapp.api.ImgurApi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.RequestBody;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
public class ImageUploadManagerTest {
    private ImageUploadManager manager;
    @Mock
    private ImgurApi api;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manager = new ImageUploadManager(api);
    }

    @Test
    public void uploadImage() {
        when(api.postImage(anyString(), any(RequestBody.class))).thenReturn(Observable.never());

        manager.uploadImage("test_path");

        verify(api).postImage(eq("Client-ID " + ImageUploadManager.CLIENT_ID), any(RequestBody.class));
    }
}