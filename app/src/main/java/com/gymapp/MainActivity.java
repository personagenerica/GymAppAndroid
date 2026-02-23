package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button btnClases, btnMonitores, btnProductos, btnPerfil, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");


       Log.d("knsdnjn",  prefs.getString("jwt_token", "jwt_token"));

        //Mostrar botones segun el rol
        if (rol.equals("Admin")){




        }
        // =========================
        // BOTONES SUPERIORES
        // =========================
        btnClases = findViewById(R.id.btnClases);
        btnMonitores = findViewById(R.id.btnMonitores);
        btnProductos = findViewById(R.id.btnProductos);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnLogout = findViewById(R.id.btnLogout);

        btnClases.setOnClickListener(v ->
                startActivity(new Intent(this, ReservarClasesActivity.class)));

        btnMonitores.setOnClickListener(v ->
                Toast.makeText(this, "Pantalla Monitores pendiente", Toast.LENGTH_SHORT).show());

        btnProductos.setOnClickListener(v ->
                startActivity(new Intent(this, ProductoActivity.class)));

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));

        btnLogout.setOnClickListener(v -> cerrarSesion());

        // =========================
        // MENÚ INFERIOR
        // =========================
        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);

        bottomMenu.setSelectedItemId(R.id.navigation_home);

        bottomMenu.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                return true; // ya estamos aquí
            }

            if (id == R.id.navigation_search) {
                startActivity(new Intent(this, ProductoActivity.class));
                return true;
            }

            if (id == R.id.navigation_profile) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    // =========================
    // CERRAR SESIÓN CORRECTO
    // =========================
    private void cerrarSesion() {

        SharedPreferences prefs =
                getSharedPreferences("auth_prefs", MODE_PRIVATE);

        prefs.edit().clear().apply();

        Toast.makeText(this,
                "Sesión cerrada correctamente",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}