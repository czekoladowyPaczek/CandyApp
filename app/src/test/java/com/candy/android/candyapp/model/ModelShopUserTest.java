package com.candy.android.candyapp.model;

import com.google.gson.Gson;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
public class ModelShopUserTest {
    private static final String JSON = "{\"id\": 115864548829752, \"name\": \"Joe Alabahfbbjejd Putnamwitz\", \"picture\": \"https://graph.facebook.com/115864548829752/picture?type=large\"}";

    @Test
    public void shouldBuildInstance() {
        ModelShopUser user = getModelShopUser();

        assertEquals(115864548829752L, user.getId());
        assertEquals("Joe Alabahfbbjejd Putnamwitz", user.getName());
        assertEquals("https://graph.facebook.com/115864548829752/picture?type=large", user.getPicture());
    }

    public static ModelShopUser getModelShopUser() {
        return new Gson().fromJson(JSON, ModelShopUser.class);
    }
}