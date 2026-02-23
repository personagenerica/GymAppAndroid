package com.gymapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.database.ApiClient;
import com.gymapp.model.Clase;
import com.gymapp.model.Monitor;
import com.gymapp.services.ClaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearClaseActivity extends AppCompatActivity {

    private EditText etFechaInicio, etHoraInicio, etFechaFin, etHoraFin, etAforo;
    private Button btnCrear;
    private ClaseService claseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_clase);

        // EditTexts separados
        etFechaInicio = findViewById(R.id.etFechaInicio); // formato yyyy-MM-dd
        etHoraInicio  = findViewById(R.id.etHoraInicio);  // formato HH:mm
        etFechaFin    = findViewById(R.id.etFechaFin);    // formato yyyy-MM-dd
        etHoraFin     = findViewById(R.id.etHoraFin);     // formato HH:mm
        etAforo       = findViewById(R.id.etAforo);
        btnCrear      = findViewById(R.id.btnCrearClase);

        claseService = ApiClient.getClient(this).create(ClaseService.class);

        btnCrear.setOnClickListener(v -> crearClase());
    }

    private void crearClase() {
        try {
            // 1️⃣ Leer valores del usuario
            String fechaInicioStr = etFechaInicio.getText().toString(); // ej: 2026-02-15
            String horaInicioStr  = etHoraInicio.getText().toString();  // ej: 01:00
            String fechaFinStr    = etFechaFin.getText().toString();    // ej: 2026-02-15
            String horaFinStr     = etHoraFin.getText().toString();     // ej: 02:00
            int aforo             = Integer.parseInt(etAforo.getText().toString());

            // 2️⃣ Juntar fecha + hora en formato ISO 8601
            String fechaHoraInicioIso = fechaInicioStr + "T" + horaInicioStr + ":00+01:00"; // zona horaria GMT+1
            String fechaHoraFinIso    = fechaFinStr + "T" + horaFinStr + ":00+01:00";

            // 3️⃣ Convertir a Date usando SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            Date fechaInicio = sdf.parse(fechaHoraInicioIso);
            Date fechaFin    = sdf.parse(fechaHoraFinIso);

            // 4️⃣ Crear objeto Clase
            Clase clase = new Clase();
            clase.setFechaInicio(fechaInicio);
            clase.setFechaFin(fechaFin);
            clase.setAforoMaximo(aforo);
            clase.setPlazasOcupadas(0);
            clase.setUsuarios(new ArrayList<>()); // evitar null

            // 5️⃣ Monitor desde sesión
            SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            Monitor monitor = new Monitor();
            monitor.setUsername(username);
            clase.setMonitor(monitor);

            // 6️⃣ Enviar al backend
            String token = prefs.getString("jwt_token", "");
            claseService.crearClase(clase, "Bearer " + token).enqueue(new Callback<Clase>() {
                @Override
                public void onResponse(Call<Clase> call, Response<Clase> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CrearClaseActivity.this, "Clase creada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CrearClaseActivity.this,
                                "Error creando clase: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Clase> call, Throwable t) {
                    Toast.makeText(CrearClaseActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
        }
    }
}