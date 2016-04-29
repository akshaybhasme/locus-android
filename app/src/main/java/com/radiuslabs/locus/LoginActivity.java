package com.radiuslabs.locus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.radiuslabs.locus.models.AccessToken;
import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.persistence.AppPersistence;
import com.radiuslabs.locus.restservices.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends ActionBarActivity {

    public static final String TAG = "LoginActivity";

    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        findViewById(R.id.bLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User();
                user.setUser_name(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());

                Call<AccessToken> call = RestClient.getInstance().getUserService().login(user);
                final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...");
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Access Token: " + response.body());
                            AppPersistence persistence = new AppPersistence(LoginActivity.this);
                            persistence.setAccessToken(response.body().getAccess_token());
                            RestClient.getInstance().setAccessToken(response.body().getAccess_token());
                            Intent intent = new Intent(LoginActivity.this, LauncherActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(
                                    findViewById(R.id.rootView),
                                    "Invalid credentials",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        t.printStackTrace();
                        progressDialog.dismiss();
                        Snackbar.make(
                                findViewById(R.id.rootView),
                                "Something broke :(",
                                Snackbar.LENGTH_SHORT
                        ).show();
                    }
                });

            }
        });

        findViewById(R.id.tvNewUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
