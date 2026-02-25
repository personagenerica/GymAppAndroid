package com.gymapp.services;

import com.gymapp.model.Clase;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ClaseService {

    // 🔹 Listar todas las clases
    @GET("clase")
    Call<List<Clase>> listarClases(@Header("Authorization") String token);

    // 🔹 Crear clase (solo monitor)
    @POST("clase")
    Call<Clase> crearClase(@Body Clase dto, @Header("Authorization") String token);
    // 🔹 Reservar clase usando solo JWT
    @POST("clase/{id}/reservar")
    Call<Clase> reservarClase(@Path("id") int id, @Header("Authorization") String token);

    // 🔹 Obtener clase por ID
    @GET("clase/{id}")
    Call<Clase> getClase(@Path("id") int id);
}