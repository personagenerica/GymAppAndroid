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
    // 1. Obtener todos los productos
    @GET("producto")
    Call<List<Producto>> getProductos();
    // 2. Obtener un producto por ID
    @GET("producto/{id}")
    Call<Producto> getProducto(@Path("id") int id);
    // 3. Crear un nuevo producto
    @POST("producto")
    Call<Producto> crearProducto(@Body Producto producto);
    // 4. Actualizar un producto existente
    @PUT("producto/{id}")
    Call<Producto> actualizarProducto(@Path("id") int id, @Body Producto producto);
    // 5. Eliminar un producto
    @DELETE("producto/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);
    // 6. Cambiar el estado (borrador/activo) usando el ID del producto
    @PUT("producto/{id}/estado")
    Call<Boolean> cambiarEstadoProducto(@Path("id") int id, @Body Boolean borrador);
}
