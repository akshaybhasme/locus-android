package com.radiuslabs.locus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.radiuslabs.locus.persistence.AppPersistence;
import com.radiuslabs.locus.restservices.RestClient;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        AppPersistence persistence = new AppPersistence(this);
        String accessToken = persistence.getAccessToken();

        if (accessToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            RestClient.getInstance().setAccessToken(accessToken);
            Intent intent = new Intent(this, NewsFeedActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
