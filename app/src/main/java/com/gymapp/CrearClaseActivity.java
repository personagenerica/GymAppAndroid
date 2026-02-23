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

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearClaseActivity extends AppCompatActivity {

    private EditText etFechaInicio, etFechaFin, etAforo;
    private Button btnCrear;
    private ClaseService claseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_clase);

        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        etAforo = findViewById(R.id.etAforo);
        btnCrear = findViewById(R.id.btnCrearClase);

        claseService = ApiClient.getClient(this).create(ClaseService.class);

        btnCrear.setOnClickListener(v -> crearClase());
    }

    private void crearClase() {
        try {
            // Leer campos
            String strFechaInicio = etFechaInicio.getText().toString(); // ej: 2026-02-23T18:00:00+01:00
            String strFechaFin = etFechaFin.getText().toString();       // ej: 2026-02-23T19:00:00+01:00
            int aforo = Integer.parseInt(etAforo.getText().toString());

            // Crear objeto Clase sin parsing manual, Gson hará la conversión
            Clase clase = new Clase();
            clase.setFechaInicio(new Date(strFechaInicio)); // se recomienda usar Date o LocalDateTime según tu backend
            clase.setFechaFin(new Date(strFechaFin));
            clase.setAforoMaximo(aforo);
            clase.setPlazasOcupadas(0);

            // Evitar null en usuarios
            clase.setUsuarios(new ArrayList<>());

            // Monitor desde sesión
            SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            Monitor monitor = new Monitor();
            monitor.setUsername(username);
            clase.setMonitor(monitor);

            // Enviar al backend
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