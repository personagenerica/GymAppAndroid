package com.gymapp.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {

            // Gson con ISO 8601 compatible con Jackson
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX") // usa ':' en offset
                    .create();

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request().newBuilder()
                                .header("Content-Type", "application/json");

                        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("jwt_token", null);
                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // ✅ usar Gson personalizado
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}