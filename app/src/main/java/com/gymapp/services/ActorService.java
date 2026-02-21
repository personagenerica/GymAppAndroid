package com.gymapp.services;

import com.gymapp.model.Actor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActorService {

    @POST("actores")
    Call<Actor> registrar(@Body Actor actor);
}