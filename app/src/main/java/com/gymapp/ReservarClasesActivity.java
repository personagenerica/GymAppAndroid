package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gymapp.adapter.ClaseAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Clase;
import com.gymapp.services.ClaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarClasesActivity extends AppCompatActivity {

    private RecyclerView recyclerClases;
    private FloatingActionButton fabCrearClase;
    private ClaseService claseService;

    private SharedPreferences prefs;
    private String token;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_clases);

        recyclerClases = findViewById(R.id.recyclerClases);
        fabCrearClase = findViewById(R.id.fabCrearClase);

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        rol = prefs.getString("rol", "");

        claseService = ApiClient.getClient(this).create(ClaseService.class);

        if (!"MONITOR".equalsIgnoreCase(rol)) {
            fabCrearClase.setVisibility(android.view.View.GONE);
        } else {
            fabCrearClase.setOnClickListener(v ->
                    startActivity(new Intent(this, CrearClaseActivity.class)));
        }

        cargarClases();
    }

    private void cargarClases() {

        claseService.listarClases("Bearer " + token)
                .enqueue(new Callback<List<Clase>>() {

                    @Override
                    public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Clase> clases = response.body();
                            List<Object> items = new ArrayList<>();

                            Map<String, List<Clase>> agrupadas = new TreeMap<>();

                            SimpleDateFormat formatoDia =
                                    new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.getDefault());

                            for (Clase c : clases) {

                                String dia = formatoDia.format(c.getFechaInicio());

                                agrupadas
                                        .computeIfAbsent(dia, k -> new ArrayList<>())
                                        .add(c);
                            }

                            for (String dia : agrupadas.keySet()) {
                                items.add("📅 " + dia);
                                items.addAll(agrupadas.get(dia));
                            }

                            ClaseAdapter adapter =
                                    new ClaseAdapter(items, clase -> reservarClase(clase));

                            recyclerClases.setAdapter(adapter);

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
                    }
                });
    }

    private void reservarClase(Clase clase) {

        claseService.reservarClase(clase.getId(), "Bearer " + token)
                .enqueue(new Callback<Clase>() {

                    @Override
                    public void onResponse(Call<Clase> call, Response<Clase> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(ReservarClasesActivity.this,
                                    "Reserva realizada correctamente",
                                    Toast.LENGTH_SHORT).show();
                            cargarClases();
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
                    }
                });
    }
}