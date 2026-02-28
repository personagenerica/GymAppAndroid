package com.gymapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.database.ApiClient;
import com.gymapp.model.Producto;
import com.gymapp.services.ProductoService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioProductoActivity extends AppCompatActivity {

    private EditText etNombre, etTipo, etPrecio, etStock;
    private Button btnGuardar;
    private ProductoService productoService;
    private Integer productoId = null; // null = crear | no null = editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_producto);

        etNombre = findViewById(R.id.etNombre);
        etTipo = findViewById(R.id.etTipo);
        etPrecio = findViewById(R.id.etPrecio);
        etStock = findViewById(R.id.etStock);
        btnGuardar = findViewById(R.id.btnGuardar);

        productoService = ApiClient.getClient(this).create(ProductoService.class);

        // Verificar si viene ID → edición
        if (getIntent().hasExtra("id")) {
            productoId = getIntent().getIntExtra("id", -1);

            etNombre.setText(getIntent().getStringExtra("nombre"));
            etTipo.setText(getIntent().getStringExtra("tipo"));
            etPrecio.setText(String.valueOf(getIntent().getIntExtra("precio", 0)));
            etStock.setText(String.valueOf(getIntent().getIntExtra("stock", 0)));

            btnGuardar.setText("Actualizar");
        } else {
            btnGuardar.setText("Crear");
        }

        btnGuardar.setOnClickListener(v -> guardarOActualizar());
    }

    private void guardarOActualizar() {

        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (nombre.isEmpty() || tipo.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int precio, stock;
        try {
            precio = Integer.parseInt(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio o stock inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setTipo(tipo);
        producto.setPrecio(precio);
        producto.setStock(stock);
        // ⚠ NO seteamos id ni version

        if (productoId == null) {
            // CREAR
            productoService.crearProducto(producto)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioProductoActivity.this, "Producto creado", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioProductoActivity.this, "Error al crear producto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(FormularioProductoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // EDITAR
            productoService.actualizarProducto(productoId, producto)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(FormularioProductoActivity.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormularioProductoActivity.this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(FormularioProductoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}