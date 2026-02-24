package com.gymapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gymapp.adapter.ClaseAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Clase;
import com.gymapp.services.ClaseService;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarClasesActivity extends AppCompatActivity {

    private RecyclerView recyclerClases;
    private EditText etHora, etAforo;
    private Button btnCrearClase;

    private ClaseService claseService;
    private String token;
    private String rol;
    private int monitorId;
    private int usuarioId; // ID del usuario para reservas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_clases);

        recyclerClases = findViewById(R.id.recyclerClases);
        etHora = findViewById(R.id.etHora);
        etAforo = findViewById(R.id.etAforo);
        btnCrearClase = findViewById(R.id.btnCrearClase);

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));

        // Recuperar datos de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        rol = prefs.getString("rol", "");
        monitorId = prefs.getInt("monitor_id", 0);
        usuarioId = prefs.getInt("usuario_id", 0); // clave para reservar

        claseService = ApiClient.getClient(this).create(ClaseService.class);

        // Solo MONITOR puede crear clase
        if ("MONITOR".equalsIgnoreCase(rol)) {
            btnCrearClase.setOnClickListener(v -> crearClase());
        } else {
            btnCrearClase.setVisibility(View.GONE);
            etHora.setVisibility(View.GONE);
            etAforo.setVisibility(View.GONE);
        }

        cargarClases();
    }

    /** Cargar y mostrar clases en RecyclerView */
    private void cargarClases() {
        claseService.listarClases("Bearer " + token)
                .enqueue(new Callback<List<Clase>>() {
                    @Override
                    public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Clase> clases = response.body();
                            List<Object> items = new ArrayList<>();

                            // Agrupar por fecha
                            Map<String, List<Clase>> agrupadas = new TreeMap<>();
                            for (Clase c : clases) {
                                String dia = c.getFechaInicio() != null ? c.getFechaInicio().substring(0, 10) : "Sin fecha";
                                agrupadas.computeIfAbsent(dia, k -> new ArrayList<>()).add(c);
                            }

                            for (String dia : agrupadas.keySet()) {
                                items.add("📅 " + dia);
                                items.addAll(agrupadas.get(dia));
                            }

                            ClaseAdapter adapter = new ClaseAdapter(items, clase -> reservarClase(clase.getId()));
                            recyclerClases.setAdapter(adapter);

                        } else {
                            Toast.makeText(ReservarClasesActivity.this,"No se pudieron cargar las clases ❌", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Clase>> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this,"Error de conexión ⚠", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** Crear clase (solo MONITOR) */
    private void crearClase() {
        // Lógica de creación de clase como antes
    }

    /** Reservar clase como usuario */
    private void reservarClase(int claseId) {
        claseService.reservarClase(claseId, usuarioId, "Bearer " + token)
                .enqueue(new Callback<Clase>() {
                    @Override
                    public void onResponse(Call<Clase> call, Response<Clase> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ReservarClasesActivity.this,"Clase reservada ✅", Toast.LENGTH_SHORT).show();
                            cargarClases(); // actualizar listado
                        } else {
                            Toast.makeText(ReservarClasesActivity.this,"No se pudo reservar ❌", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Clase> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this,"Error de conexión ⚠", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}