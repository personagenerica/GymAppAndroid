package com.gymapp.services;

import com.gymapp.model.Actor;
import com.gymapp.model.Admin;
import com.gymapp.model.Monitor;
import com.gymapp.model.Usuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ActorService {

    @POST("usuario")  // Solo /actor
    Call<ResponseBody> registrar(@Body Usuario usuario);

    @POST("monitor")  // Solo /actor
    Call<ResponseBody> registrar(@Body Monitor monitor);

    @POST("admin")  // Solo /actor
    Call<ResponseBody> registrar(@Body Admin admin);

    @GET("actores")
    Call<List<Actor>> obtenerActores();
}