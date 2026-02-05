package com.gymapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    //Adapter es demasido generico utilizamos el suyo
    private ProductoAdapter adapter;

    private Spinner spinnerProductos;
    //Botones
    private Button btnListar;
    private Button btnCrear;
    private Button btnEditar;
    private Button btnEliminar;


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

        // 👉 AQUÍ conectas el botón
        btnListar.setOnClickListener(v -> listarProductos());
        btnCrear.setOnClickListener(v -> crearProducto());
    }




    //Aqui listamos todos los productos
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
                Log.e("Error", "Error al listar productos", t);
            }
        });
    }

    //Aqui creamos un producto
    private void crearProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto de prueba");
        producto.setTipo("General");
        producto.setPrecio(10.0);
        producto.setStock(5);
        producto.setVersion(1);

        productoService.crearProducto(producto).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    listarProductos();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.e("API", "Error al crear producto", t);
            }
        });
    }


    public Producto getById (int id){
        Producto producto = null;
        return producto;
    }





}