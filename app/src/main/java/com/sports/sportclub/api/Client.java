
package com.sports.sportclub.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 16301 on 2018/10/31 0031.
 */

public class Client {



    public static final String BASE_URL = "http://api2.bmob.cn/";
    public static Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();



}

