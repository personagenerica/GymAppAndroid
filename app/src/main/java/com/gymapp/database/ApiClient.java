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
    //No funciona con localhost
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            //Para las fechas
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // ISO 8601
                    .create();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        //Obtiene la petición original (chain.request()) y se
                        //crea un builder para enviar los datos en formato JSON
                        Request.Builder builder = chain.request().newBuilder().header("Content-Type", "application/json");
                        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("jwt_token", null);
                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                        //Se obtiene el token de SharedPreferences
                        //y se añade al header Authorization
                        return chain.proceed(builder.build());
                    })
                    .build();
            //Se construye la instancia de Retrofit con la URL base, el
            //convertidor Gson y los headers configurados previamente.
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
