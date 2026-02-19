package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gymapp.adapter.ProductoAdapter;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Producto;
import com.gymapp.services.ProductoService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);


    }


    private void cerrarSesion(){
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

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
