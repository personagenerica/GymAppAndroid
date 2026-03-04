package com.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre, etUsername, etEmail;
    private TextView tvRol;
    private Button btnGuardar, btnCerrarSesion, btnCrearUsuarios;
    private ImageView imgPerfil;

    private final String FOTO_DEFAULT =
            "https://cdn-icons-png.flaticon.com/512/12225/12225881.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etNombre = findViewById(R.id.etNombre);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        tvRol = findViewById(R.id.tvRol);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCrearUsuarios = findViewById(R.id.btnCrearUsuarios);
        imgPerfil = findViewById(R.id.imgPerfil);

        cargarDatos();
        mostrarBotonSiEsAdmin();

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        BottomNavigationView bottomMenu = findViewById(R.id.navigation_menu);
        bottomMenu.setSelectedItemId(R.id.navigation_perfil);

        bottomMenu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
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

            if (id == R.id.navigation_logout) {
                cerrarSesion();
                return true;
            }

            return true;
        });
    }

    private void cargarDatos() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);

        String nombre = prefs.getString("nombre", "");
        String username = prefs.getString("username", "");
        String email = prefs.getString("email", "");
        String rol = prefs.getString("rol", "");

        etNombre.setText(nombre);
        etUsername.setText(username);
        etEmail.setText(email);
        tvRol.setText("Rol: " + rol);

        // SIEMPRE misma foto para todos
        cargarImagen(FOTO_DEFAULT);
    }

    private void guardarCambios() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);

        prefs.edit()
                .putString("nombre", etNombre.getText().toString())
                .putString("username", etUsername.getText().toString())
                .putString("email", etEmail.getText().toString())
                .apply();

        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
    }

    private void cargarImagen(String url) {
        Picasso.get()
                .load(url)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .fit()
                .centerCrop()
                .transform(new CircleTransform())
                .into(imgPerfil);
    }

    private void mostrarBotonSiEsAdmin() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        if ("Admin".equalsIgnoreCase(rol)) {
            btnCrearUsuarios.setVisibility(View.VISIBLE);
        } else {
            btnCrearUsuarios.setVisibility(View.GONE);
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

    public static class CircleTransform implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}