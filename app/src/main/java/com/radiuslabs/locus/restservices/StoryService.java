package com.radiuslabs.locus.restservices;

import com.radiuslabs.locus.models.Story;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface StoryService {

    @POST("/api/stories")
    Call<Story> createStory(@Body Story story);

    @GET("/api/stories")
    Call<List<Story>> getUserStories();

    @Multipart
    @POST("/api/stories/upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);

}
