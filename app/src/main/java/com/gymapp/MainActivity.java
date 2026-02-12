package com.gymapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.adapter.ProductoAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Producto;
import com.gymapp.services.ProductoService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ProductoService productoService;
    private ListView lvProducts;
    private ProductoAdapter adapter;
    private Spinner spinnerProductos;

    private Button btnListar;
    private Button btnCrear;
    private Button btnEditar;
    private Button btnEliminar;

    private Producto productoSeleccionado; // Aquí guardamos el producto que el usuario selecciona

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvProducts = findViewById(R.id.lvProducts);
        spinnerProductos = findViewById(R.id.spinnerProductos);
        btnListar = findViewById(R.id.btnListar);
        btnCrear = findViewById(R.id.btnCrear);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        adapter = new ProductoAdapter(this, new ArrayList<>());
        lvProducts.setAdapter(adapter);

        productoService = ApiClient.getClient(getBaseContext())
                .create(ProductoService.class);

        // Selección de producto desde ListView
        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            productoSeleccionado = adapter.getItem(position);
            Log.d("API", "Producto seleccionado: " + productoSeleccionado.getNombre());
        });

        // Botones
        btnListar.setOnClickListener(v -> listarProductos());
        btnCrear.setOnClickListener(v -> crearProducto());
        btnEditar.setOnClickListener(v -> {
            if (productoSeleccionado != null) {
                editarProducto(productoSeleccionado);
            } else {
                Log.e("API", "No hay producto seleccionado para editar");
            }
        });
        btnEliminar.setOnClickListener(v -> {
            if (productoSeleccionado != null) {
                eliminarProducto(productoSeleccionado);
            } else {
                Log.e("API", "No hay producto seleccionado para eliminar");
            }
        });

        // Cargar lista inicial
        listarProductos();
    }

    // Listar productos
    private void listarProductos() {
        Call<List<Producto>> call = productoService.getProductos();
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.clear();
                    adapter.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("API", "Error listar productos", t);
            }
        });
    }

    // Crear producto
    private void crearProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto prueba");
        producto.setTipo("General");
        producto.setPrecio(10.0);
        producto.setStock(5);

        productoService.crearProducto(producto).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                Log.d("API", "Producto creado OK");
                listarProductos();
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.e("API", "Error al crear producto", t);
            }
        });
    }

    // Editar producto existente
    private void editarProducto(Producto productoExistente) {
        productoExistente.setNombre("Producto modificado"); // Ejemplo
        productoService.actualizarProducto(productoExistente.getId(), productoExistente)
                .enqueue(new Callback<Producto>() {
                    @Override
                    public void onResponse(Call<Producto> call, Response<Producto> response) {
                        Log.d("API", "Producto editado OK");
                        listarProductos();
                    }

                    @Override
                    public void onFailure(Call<Producto> call, Throwable t) {
                        Log.e("API", "Error al editar producto", t);
                    }
                });
    }

    // Eliminar producto existente
    private void eliminarProducto(Producto productoExistente) {
        productoService.eliminarProducto(productoExistente.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("API", "Producto eliminado OK");
                        listarProductos();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("API", "Error al eliminar producto", t);
                    }
                });
    }
}
