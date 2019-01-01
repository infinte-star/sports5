package com.sports.sportclub.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BmobService {

    @Headers({"X-Bmob-Application-Id: 5fad9f2543ffa83e56155a46398d6ede",
            "X-Bmob-REST-API-Key: 918a3c131997a216e99fd565230832f5",})
    @GET("/1/login/")
    Call<ResponseBody> getUser(@Query("username") String username, @Query("password") String password);

    @Headers({"X-Bmob-Application-Id: 5fad9f2543ffa83e56155a46398d6ede",
            "X-Bmob-REST-API-Key: 918a3c131997a216e99fd565230832f5",
            "Content-Type: application/json"})
    @POST("/1/users")
    Call<ResponseBody> postUser(@Body RequestBody body);

}
