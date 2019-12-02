package com.example.thesis1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//researched data services here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23

interface GetNRELDataService {
    @GET("api/alt-fuel-stations/v1/nearest.json")
    Call<NRELData> getTestDataBySearch(@Query("api_key") String key,
                                          @Query("location") String location,
                                          @Query("fuel_type") String fuelType,
                                          @Query("limit") int limit);
}
