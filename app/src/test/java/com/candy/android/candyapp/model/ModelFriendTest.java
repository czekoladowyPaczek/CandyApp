package com.candy.android.candyapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
public class ModelFriendTest {
    public static final String TEST_FRIEND = "{" +
            "\"id\": 109370069480913," +
            "\"name\": \"Will Alabbcgfjgjfe Liwitz\",\n" +
            "\"picture\": \"https://graph.facebook.com/109370069480913/picture?type=large\"," +
            "\"status\": \"W\"" +
            "}";

    @Test
    public void shouldBuildObject() {
        Gson gson = new GsonBuilder()
                .create();

        ModelFriend friend = gson.fromJson(TEST_FRIEND, ModelFriend.class);

        assertEquals(109370069480913L, friend.getId());
        assertEquals("Will Alabbcgfjgjfe Liwitz", friend.getName());
        assertEquals("https://graph.facebook.com/109370069480913/picture?type=large", friend.getPicture());
        assertEquals("W", friend.getStatus());
    }
}