package com.petcare.mobile.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.petcare.mobile.model.LoginResponse;

/**
 * Kullanıcı oturumunu SharedPreferences ile yönetir.
 * USER ve VET rolleri için ortak kullanılır.
 */
public class SessionManager {

    private static final String PREF_NAME       = "petcare_session";
    private static final String KEY_USER_ID     = "user_id";
    private static final String KEY_FULL_NAME   = "full_name";
    private static final String KEY_EMAIL       = "email";
    private static final String KEY_USER_ROLE   = "user_role";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(LoginResponse loginResponse) {
        preferences.edit()
                .putLong(KEY_USER_ID,   loginResponse.getUserId() == null ? -1L : loginResponse.getUserId())
                .putString(KEY_FULL_NAME, loginResponse.getFullName())
                .putString(KEY_EMAIL,     loginResponse.getEmail())
                .putString(KEY_USER_ROLE, loginResponse.getUserRole() != null ? loginResponse.getUserRole() : "USER")
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

    /** "USER" veya "VET" döner */
    public String getUserRole() {
        return preferences.getString(KEY_USER_ROLE, "USER");
    }

    /** Oturumdaki kullanıcı veteriner mi? */
    public boolean isVet() {
        return "VET".equals(getUserRole());
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
