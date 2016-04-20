package com.radiuslabs.locus.restservices;

import android.util.Log;

import java.io.IOException;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static final String TAG = "RestClient";

    public static final String apiUrl = "http://flask-env.vf8pkwjdey.us-west-2.elasticbeanstalk.com/";

    private static RestClient instance;

    private UserService userService;

    private StoryService storyService;

    private String accessToken;

    private RestClient(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (accessToken != null) {
                            request = request.newBuilder().addHeader("Authorization", "JWT " + accessToken).build();
                        }
                        Log.d(TAG, "***** Headers *****");
                        Headers headers = request.headers();
                        Set<String> names = headers.names();
                        for (String name : names) {
                            Log.d(TAG, name + ": " + headers.get(name));
                        }
                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        userService = retrofit.create(UserService.class);
        storyService = retrofit.create(StoryService.class);
    }

    public static RestClient getInstance() {
        if (instance == null)
            instance = new RestClient();
        return instance;
    }

    public UserService getUserService() {
        return userService;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public StoryService getStoryService() {
        return storyService;
    }

    public void setStoryService(StoryService storyService) {
        this.storyService = storyService;
    }

}
