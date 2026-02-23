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

    // No funciona con localhost, para emulador usar 10.0.2.2
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {

            // 1️⃣ Configurar Gson con formato ISO 8601
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ") // ISO 8601 con zona horaria
                    .create();

            // 2️⃣ Configurar OkHttpClient para añadir headers automáticamente
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

            // 3️⃣ Construir Retrofit con Gson personalizado
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // ✅ usar nuestro Gson
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}