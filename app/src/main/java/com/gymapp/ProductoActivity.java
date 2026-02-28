package com.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.adapter.ProductoAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Producto;
import com.gymapp.services.ProductoService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoActivity extends AppCompatActivity {

    private ListView listView;
    private ProductoService productoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        listView = findViewById(R.id.lvProducts);
        productoService = ApiClient.getClient(this).create(ProductoService.class);

        cargarProductos();

        Button btnNuevo = findViewById(R.id.btnNuevoProducto);

        btnNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(ProductoActivity.this, FormularioProductoActivity.class);
            startActivity(intent);
        });
    }

    private void cargarProductos() {

        productoService.getProductos().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Producto> productos = response.body();

                    ProductoAdapter adapter = new ProductoAdapter(
                            ProductoActivity.this,
                            productos
                    );

                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(ProductoActivity.this,
                            "Error al cargar productos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(ProductoActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos(); // recarga la lista cada vez que la actividad se muestra
    }
}