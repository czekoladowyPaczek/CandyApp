package com.candy.android.candyapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
public class ModelUserTest {
    public static final String TEST_USER_LOGIN = "{" +
            "\"id\": 106795866405632,\n" +
            "\"name\": \"Dorothy Alabbefefeefi Wisemanson\"," +
            "\"email\": \"jpodjwv_wisemanson_1463582102@tfbnw.net\"," +
            "\"picture\": \"https://graph.facebook.com/106795866405632/picture?type=large\"," +
            "\"friends\": [" +
            "{" +
            "\"id\": 109370069480913," +
            "\"name\": \"Will Alabbcgfjgjfe Liwitz\",\n" +
            "\"picture\": \"https://graph.facebook.com/109370069480913/picture?type=large\"," +
            "\"status\": \"W\"" +
            "}," +
            "{" +
            "\"id\": 115864548829752," +
            "\"name\": \"Joe Alabahfbbjejd Putnamwitz\"," +
            "\"picture\": \"https://graph.facebook.com/115864548829752/picture?type=large\"," +
            "\"status\": \"A\"" +
            "}" +
            "]" +
            "}";

    @Test
    public void shouldBuildObject() {
        Gson gson = new GsonBuilder()
                .create();

        ModelUser user = gson.fromJson(TEST_USER_LOGIN, ModelUser.class);

        assertEquals(106795866405632L, user.getId());
        assertEquals("Dorothy Alabbefefeefi Wisemanson", user.getName());
        assertEquals("jpodjwv_wisemanson_1463582102@tfbnw.net", user.getEmail());
        assertEquals("https://graph.facebook.com/106795866405632/picture?type=large", user.getPicture());
        assertEquals(2, user.getFriends().size());
        assertEquals(109370069480913L, user.getFriends().get(0).getId());
        assertEquals(115864548829752L, user.getFriends().get(1).getId());
    }
}