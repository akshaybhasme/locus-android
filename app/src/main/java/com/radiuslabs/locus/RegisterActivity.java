package com.radiuslabs.locus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.radiuslabs.locus.models.AccessToken;
import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.persistence.AppPersistence;
import com.radiuslabs.locus.restservices.RestClient;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity {

    public static final String TAG = "RegisterActivity";

    public static final int REQUEST_IMAGE_CAPTURE = 111;
    public static final int REQUEST_CROP_CAPTURE = 222;

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private ImageView ivProfilePic;

    private Uri outputFileUri, croppedImageUri;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initTextFields();

        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputFileUri = Util.openImageIntent(RegisterActivity.this, REQUEST_IMAGE_CAPTURE);
            }
        });

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
        pd = new ProgressDialog(RegisterActivity.this, R.style.ProgressDialogTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.setMessage("Creating your account...");
        pd.show();
        registerUser("");
    }

    private void registerUser(String profilePic) {
        User user = new User();
        user.setFirst_name(etFirstName.getText().toString());
        user.setLast_name(etLastName.getText().toString());
        user.setEmail_id(etEmail.getText().toString());
        user.setUser_name(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setProfile_pic(profilePic);
        Call<User> req = RestClient.getInstance().getUserService().createUser(user);
        req.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Util.user = response.body();
                    User u = new User();
                    u.setUser_name(response.body().getUser_name());
                    u.setPassword(etPassword.getText().toString());
                    RestClient.getInstance().getUserService().login(u).enqueue(new Callback<AccessToken>() {
                        @Override
                        public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                            if (pd.isShowing()) pd.dismiss();
                            if (response.isSuccessful()) {

                                RestClient.getInstance().setAccessToken(response.body().getAccess_token());

                                AppPersistence persistence = new AppPersistence(RegisterActivity.this);
                                persistence.setAccessToken(response.body().getAccess_token());

                                if (croppedImageUri != null) {
                                    File f = new File(croppedImageUri.getPath());
                                    RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), f);

                                    MultipartBody.Part body =
                                            MultipartBody.Part.createFormData("file", f.getName(), file);

                                    RestClient.getInstance().getUserService().setProfilePicture(body).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) {
                                                goToLauncherActivity();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            t.printStackTrace();
                                        }
                                    });
                                } else {
                                    goToLauncherActivity();
                                }



                            } else {
                                Snackbar.make(
                                        findViewById(R.id.rootView),
                                        "Username / Email already taken",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AccessToken> call, Throwable t) {
                            t.printStackTrace();
                            if (pd.isShowing()) pd.dismiss();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(
                        findViewById(R.id.rootView),
                        "Error: " + t.getMessage(),
                        Snackbar.LENGTH_SHORT
                ).show();
                if (pd.isShowing()) pd.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }
                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data.getData();
                }
                Log.d(TAG, "Path: " + selectedImageUri.getPath());
                croppedImageUri = Util.getImageUri(true);
                Crop.of(selectedImageUri, croppedImageUri).asSquare().withMaxSize(720, 720).start(RegisterActivity.this, REQUEST_CROP_CAPTURE);
            } else if (requestCode == REQUEST_CROP_CAPTURE) {
                ivProfilePic.setImageURI(croppedImageUri);
            }
        }
    }

    private void goToLauncherActivity() {
        Intent intent = new Intent(RegisterActivity.this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

}
