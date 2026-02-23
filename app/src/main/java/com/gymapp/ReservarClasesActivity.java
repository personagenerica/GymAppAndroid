package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.database.ApiClient;
import com.gymapp.model.Clase;
import com.gymapp.services.ClaseService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarClasesActivity extends AppCompatActivity {

    private ListView lvClases;
    private Button btnReservar;
    private Button btnCrearClase;
    private ClaseService claseService;
    private List<Clase> listaClases;
    private Clase claseSeleccionada;

    private SharedPreferences prefs;
    private String token;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_clases);

        lvClases = findViewById(R.id.lvClases);
        btnReservar = findViewById(R.id.btnReservar);
        btnCrearClase = findViewById(R.id.btnCrearClase);

        // Inicializamos SharedPreferences
        prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        rol = prefs.getString("rol", "");

        // Inicializamos el servicio Retrofit
        claseService = ApiClient.getClient(this).create(ClaseService.class);

        // Mostrar botón solo si es monitor
        if (!"MONITOR".equalsIgnoreCase(rol)) {
            btnCrearClase.setVisibility(View.GONE);
        } else {
            btnCrearClase.setOnClickListener(v -> {
                Intent intent = new Intent(ReservarClasesActivity.this, CrearClaseActivity.class);
                startActivity(intent);
            });
        }

        // Cargar las clases
        cargarClases();

        // Selección de clase
        lvClases.setOnItemClickListener((parent, view, position, id) -> claseSeleccionada = listaClases.get(position));

        // Botón para reservar
        btnReservar.setOnClickListener(v -> {
            if (claseSeleccionada == null) {
                Toast.makeText(this, "Selecciona una clase primero", Toast.LENGTH_SHORT).show();
                return;
            }

            if (claseSeleccionada.estaCompleta()) {
                Toast.makeText(this, "Clase completa, no se puede reservar", Toast.LENGTH_SHORT).show();
                return;
            }

            reservarClase(claseSeleccionada);
        });
    }

    private void cargarClases() {
        claseService.listarClases("Bearer " + token).enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaClases = response.body();

                    // Mostramos en un ListView
                    ArrayAdapter<Clase> adapter = new ArrayAdapter<>(ReservarClasesActivity.this,
                            android.R.layout.simple_list_item_1,
                            listaClases);
                    lvClases.setAdapter(adapter);

                } else {
                    Toast.makeText(ReservarClasesActivity.this,
                            "Error cargando clases",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                Toast.makeText(ReservarClasesActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void reservarClase(Clase clase) {
        claseService.reservarClase(clase.getId(), "Bearer " + token).enqueue(new Callback<Clase>() {
            @Override
            public void onResponse(Call<Clase> call, Response<Clase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ReservarClasesActivity.this,
                            "Reserva realizada correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Actualizamos la lista
                    cargarClases();
                    claseSeleccionada = null;
                } else {
                    Toast.makeText(ReservarClasesActivity.this,
                            "No se pudo reservar",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Clase> call, Throwable t) {
                Toast.makeText(ReservarClasesActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}