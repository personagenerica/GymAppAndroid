package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);

// Marcar el item seleccionado según la Activity actual
        bottomMenu.setSelectedItemId(R.id.navigation_home); // Cambia según la Activity

        bottomMenu.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home)
                startActivity(new Intent(this, ProductoActivity.class));
            else if (item.getItemId() == R.id.navigation_search)
                startActivity(new Intent(this, ProductoActivity.class));
            else if (item.getItemId() == R.id.navigation_profile)
                startActivity(new Intent(this, ClaseActivity.class));
            return true;
        });


    }


    private void cerrarSesion(){
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductoActivity.class));

            // Obtener SharedPreferences
            SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);

            // Borrar solo el token
            prefs.edit().remove("jwt_token").apply();

            // Si quieres borrar todo:
            // prefs.edit().clear().apply();

            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

            // Opcional: volver al LoginActivity
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);

        });
    }



}
