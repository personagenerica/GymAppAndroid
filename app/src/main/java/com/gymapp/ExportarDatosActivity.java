package com.gymapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gymapp.model.Actor;
import com.gymapp.services.ActorService;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExportarDatosActivity extends AppCompatActivity {

    private Button btnExportar;
    private ActorService actorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_datos);

        btnExportar = findViewById(R.id.btnExportar);

        // 🔹 Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        actorService = retrofit.create(ActorService.class);

        btnExportar.setOnClickListener(v -> obtenerDatosAPI());
    }

    private void obtenerDatosAPI() {

        actorService.obtenerActores().enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    generarCSV(response.body());
                } else {
                    Toast.makeText(ExportarDatosActivity.this,
                            "Error al obtener datos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(ExportarDatosActivity.this,
                        "Fallo conexión API",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generarCSV(List<Actor> lista) {
        try {

            StringBuilder contenido = new StringBuilder();
            contenido.append("ID,Nombre,Username,Email,Telefono,Rol\n");

            for (Actor a : lista) {
                contenido.append(a.getId()).append(",");
                contenido.append(a.getNombre()).append(",");
                contenido.append(a.getUsername()).append(",");
                contenido.append(a.getEmail()).append(",");
                contenido.append(a.getTelefono()).append(",");
                contenido.append(a.getRol()).append("\n");
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "datos_gym.csv");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(
                    MediaStore.Files.getContentUri("external"), values);

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write(contenido.toString());
            writer.flush();
            writer.close();

            Toast.makeText(this,
                    "Archivo exportado en Descargas",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error al exportar",
                    Toast.LENGTH_LONG).show();
        }
    }
}