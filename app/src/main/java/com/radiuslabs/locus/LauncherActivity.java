package com.radiuslabs.locus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.persistence.AppPersistence;
import com.radiuslabs.locus.restservices.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            RestClient.getInstance().getUserService().getSelf().enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Util.user = response.body();
                    Intent intent = new Intent(LauncherActivity.this, NewsFeedActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                    finish();
                }
            });
        }
    }
}
