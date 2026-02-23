package com.gymapp.services;

import com.gymapp.model.Clase;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ClaseService {

    // Lista todas las clases con JWT
    @GET("clase")
    Call<List<Clase>> listarClases(@Header("Authorization") String token);

    // Reservar una clase específica con JWT
    @POST("clase/{id}/reservar")
    Call<Clase> reservarClase(@Path("id") int id, @Header("Authorization") String token);

    // CRUD opcional
    @GET("clase/{id}")
    Call<Clase> getClase(@Path("id") int id);

    @POST("clase")
    Call<Clase> crearClase(@Body Clase clase, @Header("Authorization") String token);
    @PUT("clase/{id}")
    Call<Clase> actualizarClase(@Path("id") int id, @Body Clase clase);

    @DELETE("clase/{id}")
    Call<Void> eliminarClase(@Path("id") int id);
}