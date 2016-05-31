package com.candy.android.candyapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.candy.android.candyapp.BuildConfig;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelUser;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Marcin
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserStorageTest {
    private UserStorage storage;

    @Before
    public void setup() {
        RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
        storage = new UserStorage(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() {
        RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    @Test
    public void shouldStoreToken() {
        String token = "test_token";
        storage.saveUserToken(token);

        assertEquals(token, RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE).getString(UserStorage.PREF_TOKEN, ""));
    }

    @Test
    public void shouldGetToken() {
        assertNull(storage.getToken());

        String token = "test_token";
        RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE).
                edit()
                .putString(UserStorage.PREF_TOKEN, token)
                .apply();
        assertEquals(token, storage.getToken());
    }

    @Test
    public void shouldSaveUser() {
        long id = 1;
        String name = "name";
        String picture = "http://picture.com";
        String email = "email";
        List<ModelFriend> friends = new ArrayList<>(1);
        friends.add(new ModelFriend(1, "friendName", "http://friend.com", ModelFriend.STATUS_ACCEPTED));
        ModelUser user = new ModelUser(id, name, picture, email, friends);
        Gson gson = new Gson();

        storage.saveUser(user);

        assertEquals(gson.toJson(user), RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE).getString(UserStorage.PREF_USER, ""));
    }

    @Test
    public void shouldGetUser() {
        long id = 1;
        String name = "name";
        String picture = "http://picture.com";
        String email = "email";
        List<ModelFriend> friends = new ArrayList<>(1);
        friends.add(new ModelFriend(1, "friendName", "http://friend.com", ModelFriend.STATUS_ACCEPTED));
        ModelUser user = new ModelUser(id, name, picture, email, friends);
        Gson gson = new Gson();
        RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE).edit().putString(UserStorage.PREF_USER, gson.toJson(user)).apply();

        ModelUser actualUser = storage.getUser();

        assertEquals(user.getName(), actualUser.getName());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getEmail(), actualUser.getEmail());
        assertEquals(user.getFriends().size(), actualUser.getFriends().size());
    }

    @Test
    public void shouldClearPrefs() {
        long id = 1;
        String name = "name";
        String picture = "http://picture.com";
        String email = "email";
        List<ModelFriend> friends = new ArrayList<>(1);
        friends.add(new ModelFriend(1, "friendName", "http://friend.com", ModelFriend.STATUS_ACCEPTED));
        ModelUser user = new ModelUser(id, name, picture, email, friends);
        Gson gson = new Gson();
        SharedPreferences prefs = RuntimeEnvironment.application.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putString(UserStorage.PREF_USER, gson.toJson(user))
                .apply();

        String token = "test_token";
        prefs.edit()
                .putString(UserStorage.PREF_TOKEN, token)
                .apply();

        storage.clear();

        assertEquals("", prefs.getString(UserStorage.PREF_USER, ""));
        assertEquals("", prefs.getString(UserStorage.PREF_TOKEN, ""));
    }
}