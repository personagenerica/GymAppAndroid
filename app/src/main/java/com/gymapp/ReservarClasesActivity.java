package com.gymapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gymapp.adapter.ClaseAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Clase;
import com.gymapp.model.MonitorId;
import com.gymapp.services.ClaseService;

import java.text.SimpleDateFormat;
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
    private int monitorId; // solo ID del monitor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_clases);

        recyclerClases = findViewById(R.id.recyclerClases);
        etHora = findViewById(R.id.etHora);
        etAforo = findViewById(R.id.etAforo);
        btnCrearClase = findViewById(R.id.btnCrearClase);

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        rol = prefs.getString("rol", "");
        monitorId = prefs.getInt("monitor_id", 0); // el ID del monitor guardado en login

        claseService = ApiClient.getClient(this).create(ClaseService.class);

        // SOLO MONITOR puede crear clase
        if ("MONITOR".equalsIgnoreCase(rol)) {
            btnCrearClase.setOnClickListener(v -> crearClase());
        } else {
            btnCrearClase.setVisibility(Button.GONE);
            etHora.setVisibility(EditText.GONE);
            etAforo.setVisibility(EditText.GONE);
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

                            // Agrupar por fecha
                            Map<String, List<Clase>> agrupadas = new TreeMap<>();
                            for (Clase c : clases) {
                                String dia = c.getFechaInicio() != null ? c.getFechaInicio().substring(0, 10) : "Sin fecha";
                                agrupadas.computeIfAbsent(dia, k -> new ArrayList<>()).add(c);
                            }

                            // Agregar encabezados y clases
                            for (String dia : agrupadas.keySet()) {
                                items.add("📅 " + dia);
                                items.addAll(agrupadas.get(dia));
                            }

                            ClaseAdapter adapter = new ClaseAdapter(items, clase ->
                                    Toast.makeText(ReservarClasesActivity.this, "Reservar función pendiente", Toast.LENGTH_SHORT).show()
                            );
                            recyclerClases.setAdapter(adapter);

                        } else {
                            Toast.makeText(ReservarClasesActivity.this,"Error al cargar clases", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Clase>> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this,"Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void crearClase() {
        String horaStr = etHora.getText().toString().trim();
        String aforoStr = etAforo.getText().toString().trim();

        if (horaStr.isEmpty() || aforoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese hora y aforo", Toast.LENGTH_SHORT).show();
            return;
        }

        int aforo;
        try {
            aforo = Integer.parseInt(aforoStr);
            if (aforo <= 0) throw new Exception();
        } catch (Exception e) {
            Toast.makeText(this, "Aforo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Fecha inicio
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaHoy = sdfDate.format(cal.getTime());

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date fechaInicio = sdfInput.parse(fechaHoy + " " + horaStr);

            // Fecha fin 1 hora después
            Calendar calFin = Calendar.getInstance();
            calFin.setTime(fechaInicio);
            calFin.add(Calendar.HOUR_OF_DAY, 1);
            Date fechaFin = calFin.getTime();

            // Convertir a ISO 8601 UTC
            SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdfIso.setTimeZone(TimeZone.getTimeZone("UTC"));
            String inicioIso = sdfIso.format(fechaInicio);
            String finIso = sdfIso.format(fechaFin);

            // Crear objeto Clase
            Clase clase = new Clase();
            clase.setFechaInicio(inicioIso);
            clase.setFechaFin(finIso);
            clase.setAforo(aforo);
            clase.setUsuarios(new ArrayList<>());
            clase.setMonitorId(monitorId); // ✅ solo el ID del monitor

            // Llamada API
            claseService.crearClase(clase, "Bearer " + token)
                    .enqueue(new Callback<Clase>() {
                        @Override
                        public void onResponse(Call<Clase> call, Response<Clase> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(ReservarClasesActivity.this,"Clase creada ✅", Toast.LENGTH_SHORT).show();
                                etHora.setText("");
                                etAforo.setText("");
                                cargarClases();
                            } else {
                                Toast.makeText(ReservarClasesActivity.this,"Error al crear clase ❌", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Clase> call, Throwable t) {
                            Toast.makeText(ReservarClasesActivity.this,"Error de conexión ⚠", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Formato de hora inválido (HH:mm)", Toast.LENGTH_SHORT).show();
        }
    }
}