package com.radiuslabs.locus.restservices;

import com.radiuslabs.locus.models.AccessToken;
import com.radiuslabs.locus.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {

    @GET("/api/users")
    Call<List<User>> getUsers();

    @POST("/api/users/register")
    Call<User> createUser(@Body User user);

    @GET("/api/users/profile")
    Call<User> getSelf();

    @POST("/auth")
    Call<AccessToken> login(@Body User user);

    @Multipart
    @POST("/api/users/profile_pic")
    Call<ResponseBody> setProfilePicture(@Part MultipartBody.Part file);

}
