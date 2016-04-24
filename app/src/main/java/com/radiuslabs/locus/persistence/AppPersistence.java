package com.radiuslabs.locus.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPersistence {

    public static final String PREFS_NAME = "locusprefs";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public AppPersistence(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setAccessToken(String accessToken) {
        editor.putString("accessToken", accessToken);
        editor.commit();
    }

    public String getAccessToken() {
        return preferences.getString("accessToken", null);
    }

}
