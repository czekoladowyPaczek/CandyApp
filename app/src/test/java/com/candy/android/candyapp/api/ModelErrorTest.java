package com.candy.android.candyapp.api;

import com.candy.android.candyapp.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ModelErrorTest {
    @Test
    public void fromRetrofit_shouldCreateInternetConnectionError() {
        Throwable th = new Throwable();

        assertEquals(ModelError.INTERNET_CONNECTION, ModelError.fromRetrofit(th));
    }

    @Test
    public void fromRetrofit_shouldCreateAuthenticationError() {
        Throwable th = new HttpException(Response.error(401, ResponseBody.create(null, "")));

        assertEquals(ModelError.AUTHENTICATION, ModelError.fromRetrofit(th));
    }

    @Test
    public void fromRetrofit_shouldCreateSpecificError() {
        Throwable th = new HttpException(Response.error(500, ResponseBody.create(null, "{\"code\":22, \"message\":\"test message\"}")));

        assertEquals(ModelError.ALREADY_FRIEND, ModelError.fromRetrofit(th));
    }

    @Test
    public void fromRetrofit_shouldCreateUnknownErrorWhenCodeOtherThan500() {
        Throwable th = new HttpException(Response.error(501, ResponseBody.create(null, "{\"code\":22, \"message\":\"test message\"}")));

        assertEquals(ModelError.UNKNOWN, ModelError.fromRetrofit(th));
    }

    @Test
    public void generateError() throws IOException {
        assertEquals("{\"code\":22, \"message\":\"\"}", ModelError.generateError(ModelError.ALREADY_FRIEND).response().errorBody().string());
    }
}