package com.petcare.mobile.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.petcare.mobile.model.LoginResponse;

public class SessionManager {

    private static final String PREF_NAME = "petcare_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(LoginResponse loginResponse) {
        preferences.edit()
                .putLong(KEY_USER_ID, loginResponse.getUserId() == null ? -1L : loginResponse.getUserId())
                .putString(KEY_FULL_NAME, loginResponse.getFullName())
                .putString(KEY_EMAIL, loginResponse.getEmail())
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.contains(KEY_USER_ID) && preferences.getLong(KEY_USER_ID, -1L) > 0;
    }

    public long getUserId() {
        return preferences.getLong(KEY_USER_ID, -1L);
    }

    public String getFullName() {
        return preferences.getString(KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
