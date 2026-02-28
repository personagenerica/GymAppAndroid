package com.gymapp;

import static com.gymapp.database.ApiClient.getClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gymapp.model.Actor;
import com.gymapp.model.Admin;
import com.gymapp.model.Monitor;
import com.gymapp.model.Rol;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gymapp.model.Usuario;
import com.gymapp.services.ActorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private ActorService actorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ====== SharedPreferences ======
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");
        Log.d("DEBUG", prefs.getString("jwt_token", "jwt_token"));

        actorService = getClient(this).create(ActorService.class);

        // ====== Menú inferior ======
        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);
        bottomMenu.setSelectedItemId(R.id.navigation_home);

        bottomMenu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) return true;
            if (id == R.id.navigation_clases) {
                startActivity(new Intent(this, ReservarClasesActivity.class));
                return true;
            }
            if (id == R.id.navigation_productos) {
                startActivity(new Intent(this, ProductoActivity.class));
                return true;
            }

            if (id == R.id.navigation_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            if (id == R.id.navigation_logout) {
                prefs.edit().clear().apply();

                Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
            return false;
        });
    }

    // =========================
    // CREAR USUARIOS Y MONITORES
    // =========================
    private void crearUsuariosYMonitores() {

        // 3 usuarios
        List<Usuario> usuarios = new ArrayList<>(Arrays.asList(
                new Usuario("Usuario1", "user1", "Apellido1", "user1@email.com",
                        null, "612345678", 25, null, "1234"),
                new Usuario("Usuario2", "user2", "Apellido2", "user2@email.com",
                        null, "612345679", 30, null, "1234"),
                new Usuario("Usuario3", "user3", "Apellido3", "user3@email.com",
                        null, "612345680", 28, null, "1234")
        ));

        // 3 monitores
        List<Monitor> monitores = Arrays.asList(
                new Monitor("Monitor1", "monitor1", "ApellidoM1", "monitor1@email.com",
                        null, "698765432", 35, Rol.Monitor, "1234"),
                new Monitor("Monitor2", "monitor2", "ApellidoM2", "monitor2@email.com",
                        null, "698765433", 40, Rol.Monitor, "1234"),
                new Monitor("Monitor3", "monitor3", "ApellidoM3", "monitor3@email.com",
                        null, "698765434", 38, Rol.Monitor, "1234")
        );

        List<Admin> admins = Arrays.asList(
                new Admin("admin", "admin", "ApellidoA1", "admin@email.com",
                        null, "698765432", 35, Rol.Admin
                        , "1234")
        );


        StringBuilder resumen = new StringBuilder("Creación de actores:\n");

        for (Actor a : usuarios) {
            actorService.registrar((Usuario) a).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        resumen.append(a.getUsername()).append(" ✅\n");
                    } else {
                        resumen.append(a.getUsername()).append(" ❌\n");
                    }
                    Toast.makeText(MainActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    resumen.append(a.getUsername()).append(" ⚠\n");
                    Toast.makeText(MainActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

        for (Monitor m : monitores) {
            actorService.registrar(m).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        resumen.append(m.getUsername()).append(" ✅\n");
                    } else {
                        resumen.append(m.getUsername()).append(" ❌\n");
                    }
                    Toast.makeText(MainActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    resumen.append(m.getUsername()).append(" ⚠\n");
                    Toast.makeText(MainActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        //Admin
        for (Admin a : admins) {
            actorService.registrar(a).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        resumen.append(a.getUsername()).append(" ✅\n");
                    } else {
                        resumen.append(a.getUsername()).append(" ❌\n");
                    }
                    Toast.makeText(MainActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    resumen.append(a.getUsername()).append(" ⚠\n");
                    Toast.makeText(MainActivity.this, resumen.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}