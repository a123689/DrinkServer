package com.example.dat.drinksever.Retrofit;

import android.os.Build;

import androidx.annotation.RequiresApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMRetrofitClient {
    private static Retrofit retrofit = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null)
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return retrofit;
    }
}
