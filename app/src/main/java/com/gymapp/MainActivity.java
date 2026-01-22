package com.gymapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gymapp.database.ApiClient;
import com.gymapp.model.Producto;
import com.gymapp.services.ProductoService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ProductoService productoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Creación e Inicialización del Servicio
        productoService = ApiClient.getClient(getBaseContext()).create(ProductoService.class);
        obtenerListadoProductos(); // Llamada al listado de productos
    }

    private void obtenerListadoProductos() {
        Call<List<Producto>> call = productoService.getProductos();
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            //Bien
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();
                    for (Producto p : productos) {
                        Log.i("Producto", "Nombre: " + p.getNombre());
                    }
                }
            }

            @Override
            //Si falla
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);

            }
        });
    }

}