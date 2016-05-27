package com.candy.android.candyapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
public class ModelUserLoginTest {
    public static final String TEST_USER_LOGIN = "{" +
            "\"token\": \"eyJhbGciOiJIUzI1NiJ9.MTA2Nzk1ODY2NDA1NjMy.M17hOIosEavkuf4JbEOxt6bzV0zOcYzUgtWx7vJ85Io\"," +
            "\"user\": {" +
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
            "}" +
            "}";

    @Test
    public void shouldBuildObject() {
        Gson gson = new GsonBuilder()
                .create();

        ModelUserLogin user = gson.fromJson(TEST_USER_LOGIN, ModelUserLogin.class);

        assertEquals("eyJhbGciOiJIUzI1NiJ9.MTA2Nzk1ODY2NDA1NjMy.M17hOIosEavkuf4JbEOxt6bzV0zOcYzUgtWx7vJ85Io", user.getToken());
        assertEquals(106795866405632L, user.getUser().getId());
        assertEquals("Dorothy Alabbefefeefi Wisemanson", user.getUser().getName());
        assertEquals("jpodjwv_wisemanson_1463582102@tfbnw.net", user.getUser().getEmail());
        assertEquals("https://graph.facebook.com/106795866405632/picture?type=large", user.getUser().getPicture());
        assertEquals(2, user.getUser().getFriends().size());
        assertEquals(109370069480913L, user.getUser().getFriends().get(0).getId());
        assertEquals(115864548829752L, user.getUser().getFriends().get(1).getId());
    }
}