package com.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.model.Actor;
import com.gymapp.model.Rol;
import com.gymapp.model.Usuario;
import com.gymapp.services.ActorService;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etUsername, etApellidos,
            etEmail, etTelefono, etEdad, etPassword;
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
        btnRegister = findViewById(R.id.btnRegister);


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

        // Validar campos obligatorio
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


        // Fotografía: si no hay, enviar null
        String foto = null;

        // ====== Crear objeto Actor ======
        Usuario usuario = new Usuario(
                nombre,
                username,
                apellidos,
                email,
                foto,
                telefono,
                edad,
                null,
                password
        );

        // ====== Llamada Retrofit ======
        actorService.registrar(usuario)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Usuario creado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Error al crear usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}