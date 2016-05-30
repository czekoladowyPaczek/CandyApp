package com.candy.android.candyapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.candy.android.candyapp.model.ModelUser;
import com.google.gson.Gson;

/**
 * @author Marcin
 */

public class UserStorage {
    public static final String PREFS_NAME = "com.candy.android.candyapp.user_prefs";
    public static final String PREF_TOKEN = "com.candy.android.candyapp.user_storage.token";
    public static final String PREF_USER = "com.candy.android.candyapp.user_user";

    private Context context;
    private Gson gson;

    public UserStorage(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void saveUserToken(String token) {
        getPrefs().edit().putString(PREF_TOKEN, token).apply();
    }

    public String getToken() {
        return getPrefs().getString(PREF_TOKEN, null);
    }

    public void saveUser(ModelUser user) {
        getPrefs().edit().putString(PREF_USER, gson.toJson(user)).apply();
    }

    public ModelUser getUser() {
        return gson.fromJson(getPrefs().getString(PREF_USER, ""), ModelUser.class);
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
