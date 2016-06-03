package com.candy.android.candyapp.api;

import org.junit.Test;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
public class ModelErrorTest {
    @Test
    public void shouldBuildInternetConnectionError() throws Exception {
        Throwable th = new Throwable("");

        ModelError err = ModelError.fromRetrofit(th);
        assertEquals(ModelError.INTERNET_CONNECTION, err.getCode());
    }

    @Test
    public void shouldBuildAuthenticationError() throws Exception {
        HttpException ex = new HttpException(Response.error(401, ResponseBody.create(null, "")));

        ModelError err = ModelError.fromRetrofit(ex);
        assertEquals(ModelError.AUTHENTICATION, err.getCode());
    }

    @Test
    public void shouldBuildSpecificError() throws Exception {
        HttpException ex = new HttpException(Response.error(500, ResponseBody.create(null, "{\"code\":1}")));

        ModelError err = ModelError.fromRetrofit(ex);
        assertEquals(ModelError.MISSING_PROPERTIES, err.getCode());
    }

    @Test
    public void shouldBuildUnknownError() throws Exception {
        HttpException ex = new HttpException(Response.error(501, ResponseBody.create(null, "{\"code\":1}")));

        ModelError err = ModelError.fromRetrofit(ex);
        assertEquals(ModelError.UNKNOWN, err.getCode());
    }
}