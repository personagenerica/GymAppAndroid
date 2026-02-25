package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.database.ApiClient;
import com.gymapp.model.Actor;
import com.gymapp.model.ActorLogin;
import com.gymapp.services.ActorLoginService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ActorLoginService actorLoginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        actorLoginService = ApiClient.getClient(this).create(ActorLoginService.class);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ActorLogin actorLogin = new ActorLogin(username, password);

        actorLoginService.login(actorLogin).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String token = response.body().string();

                        // Guardar token
                        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                        prefs.edit().putString("jwt_token", token).apply();

                        // Obtener datos completos del actor
                        userLogin();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void userLogin() {
        actorLoginService.userLogin().enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Actor a = response.body();

                    // Guardar todos los campos en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putInt("actor_id", a.getId())
                            .putString("nombre", a.getNombre())
                            .putString("apellidos", a.getApellidos())
                            .putString("username", a.getUsername())
                            .putString("email", a.getEmail())
                            .putString("fotografia", a.getFotografia())
                            .putString("telefono", a.getTelefono())
                            .putInt("edad", a.getEdad())
                            .putString("rol", a.getRol().toString())
                            .putString("password", a.getPassword())
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();

                    // Ir a MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Error cargando usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}