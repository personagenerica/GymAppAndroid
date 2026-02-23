package com.gymapp.services;

import com.gymapp.model.Actor;
import com.gymapp.model.Usuario;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActorService {

    @POST("usuario")  // Solo /actor
    Call<ResponseBody> registrar(@Body Usuario usuario);
}