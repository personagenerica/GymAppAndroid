package com.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.model.Actor;
import com.gymapp.model.Rol;
import com.gymapp.services.ActorService;

import java.util.Arrays;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etUsername, etApellidos,
            etEmail, etTelefono, etEdad, etPassword;
    private Spinner spRol;
    private Button btnRegister;

    private ActorService actorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etUsername = findViewById(R.id.etUsername);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etEdad = findViewById(R.id.etEdad);
        etPassword = findViewById(R.id.etPassword);
        spRol = findViewById(R.id.spRol);
        btnRegister = findViewById(R.id.btnRegister);

        // Spinner roles
        ArrayAdapter<Rol> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Rol.values());

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spRol.setAdapter(adapter);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tu-api.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        actorService = retrofit.create(ActorService.class);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty()) {

            Toast.makeText(this,
                    "Complete los campos obligatorios",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(etEdad.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this,
                    "Edad inválida",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Rol rol = (Rol) spRol.getSelectedItem();

        Actor actor = new Actor(
                nombre,
                username,
                apellidos,
                email,
                "",
                telefono,
                edad,
                rol,
                password
        );

        actorService.registrar(actor).enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call,
                                   Response<Actor> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this,
                            "Registro exitoso",
                            Toast.LENGTH_LONG).show();

                    finish(); // vuelve al login
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Error al registrar",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}