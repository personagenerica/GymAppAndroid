package com.gymapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class ExportarDatosActivity extends AppCompatActivity {

    private Button btnExportar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_datos);

        btnExportar = findViewById(R.id.btnExportar);

        btnExportar.setOnClickListener(v -> exportarCSV());
    }

    private void exportarCSV() {
        try {

            // 🔥 Simulación de datos (aquí deberías llamar a tu API)
            String contenido = "ID,Nombre,Email\n";
            contenido += "1,Juan,juan@gmail.com\n";
            contenido += "2,Ana,ana@gmail.com\n";

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "datos_gym.csv");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(
                    MediaStore.Files.getContentUri("external"), values);

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write(contenido);
            writer.flush();
            writer.close();

            Toast.makeText(this, "Archivo exportado en Descargas", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al exportar", Toast.LENGTH_LONG).show();
        }
    }
}