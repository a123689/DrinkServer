package com.example.dat.drinksever.Retrofit;

import com.example.dat.drinksever.Model.DataMessage;
import com.example.dat.drinksever.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAACxmp4i0:APA91bFS5M9-MGjxVWsFBofKsQt-pLFhhpsOx8JYegOAo-1gwf8SGa5IhnKfbibxkdfdNyAthINaEsRGeHpw-gRQfBaqaSqhtyl6ZctoY2xtJ_Z29yhXHQU3YJN5cAcj75fVTV1IJBvQ"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
