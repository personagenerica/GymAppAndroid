package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        // Cargar productos
        cargarProductos();

        // Botón para crear un nuevo producto
        Button btnNuevo = findViewById(R.id.btnNuevoProducto);
        btnNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(ProductoActivity.this, FormularioProductoActivity.class);
            startActivity(intent);
        });

        // ====== SharedPreferences ======
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);

        // ====== Menú inferior (igual que en MainActivity) ======
        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);
        bottomMenu.setSelectedItemId(R.id.navigation_productos); // Tab actual

        bottomMenu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }

            if (id == R.id.navigation_clases) {
                startActivity(new Intent(this, ReservarClasesActivity.class));
                return true;
            }

            if (id == R.id.navigation_productos) {
                // Ya estamos aquí
                return true;
            }

            if (id == R.id.navigation_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            if (id == R.id.navigation_logout) {
                prefs.edit().clear().apply();
                Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos(); // recarga cada vez que vuelve la Activity
    }

    /** Carga la lista de productos desde la API */
    private void cargarProductos() {
        productoService.getProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductoAdapter adapter = new ProductoAdapter(ProductoActivity.this, response.body());
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(ProductoActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(ProductoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}