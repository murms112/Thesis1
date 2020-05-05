package com.example.thesis1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetCityFromCoordsService {
    @GET("geocoding/v1/reverse")
    Call<MapQuestData> getCityFromCoordinates(@Query("key") String APIKey,
                                              @Query("location") String location,
                                              @Query("outFormat") String outFormat);
}
