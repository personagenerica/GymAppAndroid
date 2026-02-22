package com.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.model.Actor;
import com.gymapp.model.Rol;
import com.gymapp.services.ActorService;

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

        // ====== Vincular vistas ======
        etNombre = findViewById(R.id.etNombre);
        etUsername = findViewById(R.id.etUsername);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etEdad = findViewById(R.id.etEdad);
        etPassword = findViewById(R.id.etPassword);
        spRol = findViewById(R.id.spRol);
        btnRegister = findViewById(R.id.btnRegister);

        // ====== Spinner de roles ======
        ArrayAdapter<Rol> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Rol.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRol.setAdapter(adapter);

        // ====== Configurar Retrofit ======
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Asegúrate que coincide con tu backend
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        actorService = retrofit.create(ActorService.class);

        // ====== Click del botón ======
        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // ====== Obtener valores ======
        String nombre = etNombre.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos obligatorios
        if (nombre.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar edad
        int edad;
        try {
            edad = Integer.parseInt(etEdad.getText().toString().trim());
            if (edad < 0 || edad > 120) {
                Toast.makeText(this, "Ingrese una edad válida", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener rol seleccionado
        Rol rol = (Rol) spRol.getSelectedItem();

        // Fotografía: si no hay, enviar null
        String foto = null;

        // ====== Crear objeto Actor ======
        Actor actor = new Actor(
                nombre,
                username,
                apellidos,
                email,
                foto,
                telefono,
                edad,
                rol,      // Enviar el rol correcto
                password
        );

        // ====== Llamada Retrofit ======
        actorService.registrar(actor).enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this,
                            "Registro exitoso",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Volver al login
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(RegisterActivity.this,
                                "Error " + response.code() + "\n" + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this,
                                "Error desconocido",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}