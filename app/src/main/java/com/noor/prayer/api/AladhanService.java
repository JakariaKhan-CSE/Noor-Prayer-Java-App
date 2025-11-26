package com.noor.prayer.api;

import com.noor.prayer.models.PrayerTimesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanService {
    @GET("timings")
    Call<PrayerTimesResponse> getPrayerTimes(
        @Query("latitude") double latitude,
        @Query("longitude") double longitude,
        @Query("method") int method
    );
}
