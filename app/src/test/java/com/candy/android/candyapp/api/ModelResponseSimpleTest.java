package com.candy.android.candyapp.api;

import com.google.gson.Gson;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * @author Marcin
 */
public class ModelResponseSimpleTest {
    @Test
    public void getMessage() throws Exception {
        String json = "{\"message\":\"deleted\"}";
        Gson gson = new Gson();

        ModelResponseSimple response = gson.fromJson(json, ModelResponseSimple.class);
        assertEquals("deleted", response.getMessage());
    }
}