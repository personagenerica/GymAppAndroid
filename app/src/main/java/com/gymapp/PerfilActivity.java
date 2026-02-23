package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombre, tvUsername, tvEmail, tvRol;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvNombre = findViewById(R.id.tvNombre);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRol = findViewById(R.id.tvRol);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        cargarDatos();
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarDatos() {

        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);

        String nombre = prefs.getString("nombre", "No disponible");
        String username = prefs.getString("username", "No disponible");
        String email = prefs.getString("email", "No disponible");
        String rol = prefs.getString("rol", "No disponible");

        tvNombre.setText("Nombre: " + nombre);
        tvUsername.setText("Usuario: " + username);
        tvEmail.setText("Email: " + email);
        tvRol.setText("Rol: " + rol);
    }

    private void cerrarSesion() {

        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}