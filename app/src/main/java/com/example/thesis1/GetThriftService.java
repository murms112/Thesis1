package com.example.thesis1;

//researched data services here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

interface GetThriftService {
    @GET("v3/businesses/search")
    Call<YelpData> getTestDataBySearch(
            @Header("Authorization") String header,
                                       @Query("term") String term,
                                       @Query("latitude") String latitude,
                                       @Query("longitude") String longitude);
}
