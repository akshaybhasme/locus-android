package com.radiuslabs.locus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.restservices.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initTextFields();

        findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.bSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

    }

    private void initTextFields(){
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
    }

    private void validateForm(){
        if(Util.isStringEmpty(etFirstName.getText().toString())){
            Snackbar.make(findViewById(R.id.rootView), "Enter first name", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(Util.isStringEmpty(etLastName.getText().toString())){
            Snackbar.make(findViewById(R.id.rootView), "Enter last name", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(Util.isStringEmpty(etEmail.getText().toString())){
            Snackbar.make(findViewById(R.id.rootView), "Enter email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(Util.isStringEmpty(etUsername.getText().toString())){
            Snackbar.make(findViewById(R.id.rootView), "Enter user name", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(Util.isStringEmpty(etPassword.getText().toString())){
            Snackbar.make(findViewById(R.id.rootView), "Enter Password", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(Util.isStringEmpty(etConfirmPassword.getText().toString())){
            Snackbar.make(findViewById(R.id.rootView), "Enter confirm password", Snackbar.LENGTH_SHORT).show();
            return;
        }

        validatePassword();
    }

    private void validatePassword(){
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if(!password.equals(confirmPassword)){
            Snackbar.make(findViewById(R.id.rootView), "Passwords do not match", Snackbar.LENGTH_SHORT).show();
            return;
        }
        registerUser();
    }

    private void registerUser(){
        User user = new User();
        user.setFirst_name(etFirstName.getText().toString());
        user.setLast_name(etLastName.getText().toString());
        user.setEmail_id(etEmail.getText().toString());
        user.setUser_name(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Please wait...");
        Call<User> req = RestClient.getInstance().getUserService().createUser(user);
        req.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Util.user = response.body();
                progressDialog.dismiss();
                Intent intent = new Intent(RegisterActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(
                        findViewById(R.id.rootView),
                        "Error: "+t.getMessage(),
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });
    }


}
