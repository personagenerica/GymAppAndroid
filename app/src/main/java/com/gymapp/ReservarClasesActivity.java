package com.gymapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gymapp.adapter.ClaseAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Clase;
import com.gymapp.model.Monitor;
import com.gymapp.model.Rol;
import com.gymapp.services.ClaseService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_clases);

        recyclerClases = findViewById(R.id.recyclerClases);
        etHora = findViewById(R.id.etHora);
        etAforo = findViewById(R.id.etAforo);
        btnCrearClase = findViewById(R.id.btnCrearClase);

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));

        // ====== SharedPreferences ======
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        rol = prefs.getString("rol", "");

        claseService = ApiClient.getClient(this).create(ClaseService.class);

        // ====== Solo monitor puede crear clases ======
        if ("MONITOR".equalsIgnoreCase(rol)) {
            btnCrearClase.setOnClickListener(v -> crearClase());
        } else {
            btnCrearClase.setVisibility(View.GONE);
            etHora.setVisibility(View.GONE);
            etAforo.setVisibility(View.GONE);
        }

        cargarClases();
    }

    /** ====== Cargar clases y agrupar por fecha ====== */
    private void cargarClases() {
        claseService.listarClases("Bearer " + token)
                .enqueue(new Callback<List<Clase>>() {
                    @Override
                    public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Clase> clases = response.body();
                            List<Object> items = new ArrayList<>();

                            Map<String, List<Clase>> agrupadas = new TreeMap<>();
                            for (Clase c : clases) {
                                String dia = c.getFechaInicio() != null && c.getFechaInicio().length() >= 10
                                        ? c.getFechaInicio().substring(0, 10)
                                        : "Sin fecha";
                                agrupadas.computeIfAbsent(dia, k -> new ArrayList<>()).add(c);
                            }

                            for (String dia : agrupadas.keySet()) {
                                items.add("📅 " + dia);
                                items.addAll(agrupadas.get(dia));
                            }

                            ClaseAdapter adapter = new ClaseAdapter(items,
                                    clase -> reservarClase(clase.getId()));
                            recyclerClases.setAdapter(adapter);
                        } else {
                            Toast.makeText(ReservarClasesActivity.this,
                                    "No se pudieron cargar las clases ❌", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Clase>> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this,
                                "Error de conexión ⚠", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR_LOAD", t.getMessage(), t);
                    }
                });
    }

    /** ====== Crear clase (solo monitor) ====== */
    private void crearClase() {
        String hora = etHora.getText().toString().trim();
        String aforoStr = etAforo.getText().toString().trim();

        if (hora.isEmpty() || aforoStr.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int aforo = Integer.parseInt(aforoStr);
        Clase nuevaClase = new Clase();

        // ====== Fechas ISO ======
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1); // mañana
        int horaInt = Integer.parseInt(hora.split(":")[0]);
        int minInt = Integer.parseInt(hora.split(":")[1]);
        cal.set(Calendar.HOUR_OF_DAY, horaInt);
        cal.set(Calendar.MINUTE, minInt);
        cal.set(Calendar.SECOND, 0);

        Date fechaInicio = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 1); // duración 1h
        Date fechaFin = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        nuevaClase.setFechaInicio(sdf.format(fechaInicio));
        nuevaClase.setFechaFin(sdf.format(fechaFin));
        nuevaClase.setAforo(aforo);

        // ====== Crear monitor completo desde SharedPreferences ======
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        Monitor monitorActual = new Monitor(
                prefs.getInt("actor_id", 0),
                prefs.getString("nombre", ""),
                prefs.getString("username", ""),
                prefs.getString("apellidos", ""),
                prefs.getString("email", ""),
                prefs.getString("fotografia", null),
                prefs.getString("telefono", ""),
                prefs.getInt("edad", 0),
                Rol.Monitor,
                prefs.getString("password", "")
        );

        nuevaClase.setMonitor(monitorActual);

        // ====== Log JSON ======
        Gson gson = new Gson();
        Log.d("DEBUG_JSON", gson.toJson(nuevaClase));

        // ====== Enviar clase ======
        claseService.crearClase(nuevaClase, "Bearer " + token)
                .enqueue(new Callback<Clase>() {
                    @Override
                    public void onResponse(Call<Clase> call, Response<Clase> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ReservarClasesActivity.this,
                                    "Clase creada ✅", Toast.LENGTH_SHORT).show();
                            cargarClases();
                        } else {
                            Toast.makeText(ReservarClasesActivity.this,
                                    "Error al crear ❌ (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR_CREATE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Clase> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this,
                                "Error de conexión ⚠", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR_CREATE", t.getMessage(), t);
                    }
                });
    }

    /** ====== Reservar clase (usuario) ====== */
    private void reservarClase(int claseId) {
        claseService.reservarClase(claseId, "Bearer " + token)
                .enqueue(new Callback<Clase>() {
                    @Override
                    public void onResponse(Call<Clase> call, Response<Clase> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ReservarClasesActivity.this,
                                    "Clase reservada ✅", Toast.LENGTH_SHORT).show();
                            cargarClases();
                        } else {
                            Toast.makeText(ReservarClasesActivity.this,
                                    "No se pudo reservar ❌ (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR_RESERVA", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Clase> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this,
                                "Error de conexión ⚠", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR_RESERVA", t.getMessage(), t);
                    }
                });
    }
}