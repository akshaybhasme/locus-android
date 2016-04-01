package com.radiuslabs.locus.restservices;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static final String apiUrl = "http://django-env.2ic7rrtr2b.us-west-2.elasticbeanstalk.com/";

    private static RestClient instance;

    private UserService userService;

    private RestClient(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        userService = retrofit.create(UserService.class);
    }

    public static RestClient getInstance(){
        if(instance == null)
            instance = new RestClient();
        return instance;
    }

    public UserService getUserService() {
        return userService;
    }

}
