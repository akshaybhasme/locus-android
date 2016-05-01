package com.radiuslabs.locus.restservices;

import com.radiuslabs.locus.models.Comment;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.restservices.responses.NewsFeedResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StoryService {

    @POST("/api/stories")
    Call<Story> createStory(@Body Story story);

    @GET("/api/stories")
    Call<List<Story>> getUserStories();

    @Multipart
    @POST("/api/stories/upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);

    @GET("/api/geofeeds/location/{page}")
    Call<NewsFeedResponse> getNewsFeed(@Path("page") int page,
                                  @Query("latitude") double lat,
                                  @Query("longitude") double lon);

    @POST("/api/stories/{storyId}/likes")
    Call<ResponseBody> likeStory(@Path("storyId") String storyId);

    @DELETE("/api/stories/{storyId}/likes")
    Call<ResponseBody> unlikeStory(@Path("storyId") String storyId);

    @POST("/api/stories/{storyId}/comments")
    Call<ResponseBody> addComment(@Path("storyId") String storyId, @Body Comment comment);
}
