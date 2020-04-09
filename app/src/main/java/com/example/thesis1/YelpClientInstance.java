package com.example.thesis1;

//researched retrofit here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class YelpClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.yelp.com/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}