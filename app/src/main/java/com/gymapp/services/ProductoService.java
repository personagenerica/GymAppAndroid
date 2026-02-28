package com.gymapp.services;

import com.gymapp.model.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoService {
    // Obtener todos los productos
    @GET("producto")
    Call<List<Producto>> getProductos();

    // Obtener un producto por ID
    @GET("producto/{id}")
    Call<Producto> getProducto(@Path("id") int id);

    // Crear un nuevo producto → ahora Call<Void>
    @POST("producto")
    Call<Void> crearProducto(@Body Producto producto);

    // Actualizar un producto existente → ahora Call<Void>
    @PUT("producto/{id}")
    Call<Void> actualizarProducto(@Path("id") int id, @Body Producto producto);

    // Eliminar un producto
    @DELETE("producto/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);

    // Cambiar el estado (borrador/activo)
    @PUT("producto/{id}/estado")
    Call<Boolean> cambiarEstadoProducto(@Path("id") int id, @Body Boolean borrador);
}