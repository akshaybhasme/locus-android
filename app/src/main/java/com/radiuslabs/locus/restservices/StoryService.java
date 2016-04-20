package com.radiuslabs.locus.restservices;

import com.radiuslabs.locus.models.Story;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StoryService {

    @POST("/api/stories")
    Call<Story> createStory(@Body Story story);

}
