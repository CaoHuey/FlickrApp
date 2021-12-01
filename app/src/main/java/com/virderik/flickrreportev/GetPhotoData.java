package com.virderik.flickrreportev;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class GetPhotoData {
    private static final String TAG = "GetPhotoData";

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

     static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(PhotoConstants.FLICKR_SEARCH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
}
