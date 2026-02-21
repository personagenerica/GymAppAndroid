package com.gymapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.gymapp.model.Clase;
import com.gymapp.model.Usuario;
import com.gymapp.services.ClaseService;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReservarClasesActivity extends AppCompatActivity {

    private LinearLayout layoutTurnos;
    private TextView tvDiaActual;
    private String uidUsuario;
    private String rolUsuario;
    private Button btnCambiarVista;
    private boolean modoMonitorManual = false;

    private ClaseService claseService;
    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_clases);

        layoutTurnos = findViewById(R.id.layoutTurnos);

        // ===============================
        // OBTENER DATOS DEL USUARIO
        // ===============================
        SharedPreferences prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);

        uidUsuario = prefs.getString("uid_usuario", null);
        if (uidUsuario == null) {
            Toast.makeText(this, "Debe iniciar sesión", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rolUsuario = prefs.getString("role_usuario", "user");

        currentUser = new Usuario(
                uidUsuario,
                prefs.getString("nombre_usuario", ""),
                prefs.getString("apellido_usuario", ""),
                prefs.getString("email_usuario", ""),
                prefs.getString("telefono_usuario", ""),
                prefs.getInt("edad_usuario", 0),
                rolUsuario
        );

        // ===============================
        // RETROFIT
        // ===============================
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tu-api.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        claseService = retrofit.create(ClaseService.class);

        // ===============================
        // FECHA ACTUAL
        // ===============================
        tvDiaActual = new TextView(this);
        tvDiaActual.setTextSize(18f);
        tvDiaActual.setTextColor(Color.BLACK);
        tvDiaActual.setPadding(0,0,0,16);
        layoutTurnos.addView(tvDiaActual);

        // ===============================
        // BOTÓN CREAR CLASE (MONITOR)
        // ===============================
        if (rolUsuario.equals("monitor")) {
            Button btnCrearClase = new Button(this);
            btnCrearClase.setText("➕ Crear nueva clase");
            btnCrearClase.setBackgroundColor(Color.parseColor("#1976D2"));
            btnCrearClase.setTextColor(Color.WHITE);
            btnCrearClase.setOnClickListener(v -> CrearClase());
            layoutTurnos.addView(btnCrearClase);
        }

        // ===============================
        // BOTÓN CAMBIAR VISTA
        // ===============================
        btnCambiarVista = new Button(this);
        btnCambiarVista.setText("Cambiar a vista Monitor");
        btnCambiarVista.setBackgroundColor(Color.parseColor("#1976D2"));
        btnCambiarVista.setTextColor(Color.WHITE);

        btnCambiarVista.setOnClickListener(v -> {
            modoMonitorManual = !modoMonitorManual;

            if (modoMonitorManual) {
                btnCambiarVista.setText("Cambiar a vista Usuario");
            } else {
                btnCambiarVista.setText("Cambiar a vista Monitor");
            }

            mostrarTurnos();
        });

        layoutTurnos.addView(btnCambiarVista);

        mostrarTurnos();
    }

    // =====================================================
    // MOSTRAR CLASES DE HOY Y MAÑANA
    // =====================================================
    private void mostrarTurnos() {

        int fixedViews = rolUsuario.equals("monitor") ? 3 : 2;

        if (layoutTurnos.getChildCount() > fixedViews) {
            layoutTurnos.removeViews(fixedViews,
                    layoutTurnos.getChildCount() - fixedViews);
        }

        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY,0);
        hoy.set(Calendar.MINUTE,0);
        hoy.set(Calendar.SECOND,0);
        hoy.set(Calendar.MILLISECOND,0);

        Calendar manana = (Calendar) hoy.clone();
        manana.add(Calendar.DAY_OF_YEAR,1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDiaActual.setText("Hoy: " + sdf.format(hoy.getTime()));

        claseService.getClases().enqueue(new Callback<List<Clase>>() {

            @Override
            public void onResponse(Call<List<Clase>> call,
                                   Response<List<Clase>> response) {

                if (!response.isSuccessful() || response.body() == null)
                    return;

                for (Clase clase : response.body()) {

                    Calendar fecha = Calendar.getInstance();
                    fecha.setTime(clase.getFechaInicio());

                    Calendar diaClase = (Calendar) fecha.clone();
                    diaClase.set(Calendar.HOUR_OF_DAY,0);
                    diaClase.set(Calendar.MINUTE,0);
                    diaClase.set(Calendar.SECOND,0);
                    diaClase.set(Calendar.MILLISECOND,0);

                    if (!diaClase.equals(hoy) && !diaClase.equals(manana))
                        continue;

                    crearTurnoView(clase);
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                Toast.makeText(ReservarClasesActivity.this,
                        "Error al cargar clases",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =====================================================
    // CREAR CARD DE CADA CLASE
    // =====================================================
    private void crearTurnoView(Clase clase) {

        CardView card = new CardView(this);
        card.setRadius(20);
        card.setCardElevation(8);
        card.setUseCompatPadding(true);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(24,24,24,24);

        // HORA
        TextView tvHora = new TextView(this);
        SimpleDateFormat sdfHora =
                new SimpleDateFormat("HH:mm", Locale.getDefault());

        tvHora.setText("⏰ " +
                sdfHora.format(clase.getFechaInicio()) +
                " - " +
                sdfHora.format(clase.getFechaFin()));
        tvHora.setTextSize(18f);
        tvHora.setTextColor(Color.parseColor("#388E3C"));

        // AFORO
        int ocupadas = clase.getUsuarios() == null ?
                0 : clase.getUsuarios().size();

        int libres = clase.getAforo() - ocupadas;

        TextView tvAforo = new TextView(this);
        tvAforo.setText("Aforo: " + ocupadas +
                "/" + clase.getAforo() +
                "  | Libres: " + libres);

        container.addView(tvHora);
        container.addView(tvAforo);

        // ================================
        // BOTÓN RESERVAR (USUARIO)
        // ================================
        if (!rolUsuario.equals("monitor") && !modoMonitorManual) {

            boolean estaReservado = false;

            if (clase.getUsuarios() != null) {
                for (Usuario u : clase.getUsuarios()) {
                    if (u.getUid().equals(uidUsuario)) {
                        estaReservado = true;
                        break;
                    }
                }
            }

            Button btnReservar = new Button(this);
            btnReservar.setText(
                    estaReservado ? "Cancelar reserva" : "Reservar"
            );

            btnReservar.setBackgroundColor(
                    estaReservado ?
                            Color.parseColor("#E53935") :
                            Color.parseColor("#4CAF50")
            );

            btnReservar.setTextColor(Color.WHITE);

            btnReservar.setOnClickListener(v -> {

                if (clase.getUsuarios() == null)
                    clase.setUsuarios(new ArrayList<>());

                boolean reservado = false;

                for (Usuario u : clase.getUsuarios()) {
                    if (u.getUid().equals(uidUsuario)) {
                        reservado = true;
                        break;
                    }
                }

                if (reservado) {
                    Iterator<Usuario> it =
                            clase.getUsuarios().iterator();

                    while (it.hasNext()) {
                        if (it.next().getUid().equals(uidUsuario)) {
                            it.remove();
                            break;
                        }
                    }
                } else {

                    int plazas = clase.getAforo()
                            - clase.getUsuarios().size();

                    if (plazas <= 0) {
                        Toast.makeText(this,
                                "No hay plazas disponibles",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clase.getUsuarios().add(currentUser);
                }

                claseService.actualizarClase(clase.getId(), clase)
                        .enqueue(new Callback<Clase>() {

                            @Override
                            public void onResponse(Call<Clase> call,
                                                   Response<Clase> response) {
                                mostrarTurnos();
                            }

                            @Override
                            public void onFailure(Call<Clase> call,
                                                  Throwable t) {
                                Toast.makeText(
                                        ReservarClasesActivity.this,
                                        "Error al actualizar",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            container.addView(btnReservar);
        }

        card.addView(container);
        layoutTurnos.addView(card);
    }

    private void CrearClase() {
        Toast.makeText(this,
                "Aquí iría la pantalla de crear clase",
                Toast.LENGTH_SHORT).show();
    }
}