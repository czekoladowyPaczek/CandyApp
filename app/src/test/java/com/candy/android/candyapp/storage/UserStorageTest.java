package com.candy.android.candyapp.storage;

import android.content.Context;

import com.candy.android.candyapp.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
}