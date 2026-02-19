package com.gymapp.services;

import com.gymapp.model.Clase;
import com.gymapp.model.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ClaseService {
    @GET("clase")
    Call<List<Clase>> getClases();

    @GET("clase/{id}")
    Call<Clase> getClase(@Path("id") int id);

    @POST("clase")
    Call<Clase> crearClase(@Body Clase clase);

    @PUT("clase/{id}")
    Call<Clase> actualizarClase(@Path("id") int id, @Body Clase clase);

    @DELETE("clase/{id}")
    Call<Void> eliminarClase(@Path("id") int id);
}
