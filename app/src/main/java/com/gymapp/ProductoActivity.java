package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class ProductoActivity extends AppCompatActivity {
    private ProductoService productoService;
    private ListView lvProducts;
    private ProductoAdapter adapter;
    private Spinner spinnerProductos;

    private EditText etNombre,etTipo,etPrecio,etStock;
    private Button btnListar;
    private Button btnCrear;
    private Button btnEditar;
    private Button btnEliminar;

    private Producto productoSeleccionado; // Aquí guardamos el producto que el usuario selecciona

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        lvProducts = findViewById(R.id.lvProducts);
        spinnerProductos = findViewById(R.id.spinnerProductos);
        btnListar = findViewById(R.id.btnListar);
        btnCrear = findViewById(R.id.btnCrear);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        //Edit text
        etNombre = findViewById(R.id.etNombre);
        etTipo = findViewById(R.id.etTipo);
        etPrecio = findViewById(R.id.etPrecio);
        etStock = findViewById(R.id.etStock);

        adapter = new ProductoAdapter(this, new ArrayList<>());
        lvProducts.setAdapter(adapter);
        cerrarSesion();
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
    //Hay que poner los text
    private void crearProducto() {

        String nombre = etNombre.getText().toString();
        String tipo = etTipo.getText().toString();
        String precioStr = etPrecio.getText().toString().trim(); // Paso 1: texto
        Integer precio = 0; // valor por defecto

        if(!precioStr.isEmpty()) {
            try {
                precio = Integer.parseInt(precioStr); // Paso 2: parsear a Integer
            } catch (NumberFormatException e) {
                // Manejar error de número inválido
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            }
        }

        String stockStr = etStock.getText().toString().trim(); // Paso 1: texto
        Integer stock = 0; // valor por defecto

        if(!stockStr.isEmpty()) {
            try {
                stock = Integer.parseInt(precioStr); // Paso 2: parsear a Integer
            } catch (NumberFormatException e) {
                // Manejar error de número inválido
                Toast.makeText(this, "Stock inválido", Toast.LENGTH_SHORT).show();
            }
        }



        Producto producto = new Producto();

        producto.setNombre(nombre);
        producto.setTipo(tipo);
        producto.setPrecio(precio);
        producto.setStock(stock);

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

    private void cerrarSesion(){
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            // Obtener SharedPreferences
            SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);

            // Borrar solo el token
            prefs.edit().remove("jwt_token").apply();

            // Si quieres borrar todo:
            // prefs.edit().clear().apply();

            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

            // Opcional: volver al LoginActivity
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);

        });
    }



}
