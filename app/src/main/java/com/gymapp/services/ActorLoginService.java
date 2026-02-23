package com.gymapp.services;

import com.gymapp.model.Actor;
import com.gymapp.model.ActorLogin;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ActorLoginService {
    @POST("/actor/login")
    Call<ResponseBody> login(@Body ActorLogin actorLogin);

    @GET("actor/actorLogin")
    Call<Actor> userLogin();
}