package com.gymapp;

import static com.gymapp.database.ApiClient.getClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gymapp.model.Actor;
import com.gymapp.model.Admin;
import com.gymapp.model.Monitor;
import com.gymapp.model.Rol;
import com.gymapp.model.Usuario;
import com.gymapp.services.ActorService;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

        ImageView imgHome = findViewById(R.id.imgHome);

        Picasso.get()
                .load("https://p.turbosquid.com/ts-thumb/bC/eqDLdW/CX/gym1/png/1626180632/600x600/fit_q87/f92c2643c008ad3ff5e65e80700d1e0975200641/gym1.jpg")
                .into(imgHome);
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

            if (id == R.id.navigation_home) {
                return true;
            }

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
                return true;
            }

            return false;
        });

    }


}