package com.radiuslabs.locus.restservices;

import com.radiuslabs.locus.models.AccessToken;
import com.radiuslabs.locus.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {

    @GET("/api/users")
    Call<List<User>> getUsers();

    @POST("/api/users/register")
    Call<User> createUser(@Body User user);

    @GET("/api/users/profile")
    Call<User> getSelf();

    @POST("/auth")
    Call<AccessToken> login(@Body User user);

}
