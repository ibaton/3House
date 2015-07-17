package treehou.se.habit.connector;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface MyOpenHabService {

    @Headers("Accept: text/plain")
    @GET("/addAndroidRegistration")
    void registerGCM(@Query("deviceId") String deviceId, @Query("deviceModel") String deviceModel, @Query("regId") String regId, Callback<String> listener);
}