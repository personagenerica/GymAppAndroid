package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gymapp.database.ApiClient;
import com.gymapp.model.Actor;
import com.gymapp.model.Admin;
import com.gymapp.model.Monitor;
import com.gymapp.model.Rol;
import com.gymapp.model.Usuario;
import com.gymapp.services.ActorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombre, tvUsername, tvEmail, tvRol;
    private Button btnCerrarSesion, btnCrearUsuarios;
    private ActorService actorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvNombre = findViewById(R.id.tvNombre);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRol = findViewById(R.id.tvRol);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCrearUsuarios = findViewById(R.id.btnCrearUsuarios);

        actorService = ApiClient.getClient(this).create(ActorService.class);

        cargarDatos();
        mostrarBotonSiEsAdmin();

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);
        bottomMenu.setSelectedItemId(R.id.navigation_perfil);

        bottomMenu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                return true;
            }

            if (id == R.id.navigation_clases) {
                startActivity(new Intent(PerfilActivity.this, ReservarClasesActivity.class));
                return true;
            }

            if (id == R.id.navigation_productos) {
                startActivity(new Intent(PerfilActivity.this, ProductoActivity.class));
                return true;
            }

            if (id == R.id.navigation_perfil) {
                return true;
            }

            if (id == R.id.navigation_logout) {
                cerrarSesion();
                return true;
            }

            return false;
        });
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

    private void mostrarBotonSiEsAdmin() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        if ("Admin".equalsIgnoreCase(rol)) {
            btnCrearUsuarios.setVisibility(Button.VISIBLE);
        } else {
            btnCrearUsuarios.setVisibility(Button.GONE);
        }
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



    private Callback<ResponseBody> crearCallback(String username, StringBuilder resumen) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    resumen.append(username).append(" ✅\n");
                } else {
                    resumen.append(username).append(" ❌\n");
                }
                Toast.makeText(PerfilActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resumen.append(username).append(" ⚠\n");
                Toast.makeText(PerfilActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
            }


        };
    }
}