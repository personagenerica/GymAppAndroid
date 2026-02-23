package com.gymapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CrearClaseActivity extends AppCompatActivity {

    private EditText etNombreClase, etHora;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_clase);

        etNombreClase = findViewById(R.id.etNombreClase);
        etHora = findViewById(R.id.etHora);
        btnGuardar = findViewById(R.id.btnGuardarClase);

        btnGuardar.setOnClickListener(v -> {

            String nombre = etNombreClase.getText().toString();
            String hora = etHora.getText().toString();

            // Aquí deberías llamar a tu API para guardar la clase

            Toast.makeText(this, "Clase creada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}