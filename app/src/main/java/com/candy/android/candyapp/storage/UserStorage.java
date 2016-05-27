package com.candy.android.candyapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Marcin
 */

public class UserStorage {
    public static final String PREFS_NAME = "com.candy.android.candyapp.user_prefs";
    public static final String PREF_TOKEN = "com.candy.android.candyapp.user_storage.token";

    private Context context;

    public UserStorage(Context context) {
        this.context = context;
    }

    public void saveUserToken(String token) {
        getPrefs().edit().putString(PREF_TOKEN, token).apply();
    }

    public String getToken() {
        return getPrefs().getString(PREF_TOKEN, null);
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
