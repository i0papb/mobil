package com.example.hellojava;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static KlipperApi getApi(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        String ip      = db.getLastIpAddress();
        int    apiPort = db.getApiPort();
        String baseUrl = String.format("http://%s:%d/", ip, apiPort);

        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(KlipperApi.class);
    }
}