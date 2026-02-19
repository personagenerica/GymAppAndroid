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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
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

        // Obtener usuario y rol
        SharedPreferences prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
        uidUsuario = prefs.getString("uid_usuario", null);
        if (uidUsuario == null) {
            Toast.makeText(this, "Error: Debe iniciar sesión", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        rolUsuario = prefs.getString("role_usuario", "user");
        currentUser = new Usuario(
                uidUsuario,
                prefs.getString("nombre_usuario", "Usuario Desconocido"),
                prefs.getString("apellido_usuario",""),
                prefs.getString("email_usuario",""),
                prefs.getString("telefono_usuario",""),
                prefs.getInt("edad_usuario",0),
                rolUsuario
        );

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tu-api.com/api/") // Cambia a tu URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        claseService = retrofit.create(ClaseService.class);

        // Fecha actual
        tvDiaActual = new TextView(this);
        tvDiaActual.setTextSize(18f);
        tvDiaActual.setTextColor(Color.BLACK);
        tvDiaActual.setPadding(0,0,0,16);
        layoutTurnos.addView(tvDiaActual);


        //Creacion de clases
        if (rolUsuario.equals("monitor")) {
            Button btnCrearClase = new Button(this);
            btnCrearClase.setText("➕ Crear nueva clase");
            btnCrearClase.setBackgroundColor(Color.parseColor("#1976D2"));
            btnCrearClase.setTextColor(Color.WHITE);
            btnCrearClase.setPadding(0,16,0,16);

            btnCrearClase.setOnClickListener(v -> CrearClase());

            layoutTurnos.addView(btnCrearClase);
        }
        // Botón cambiar vista
        btnCambiarVista = new Button(this);
        btnCambiarVista.setText("Cambiar a vista Monitor");
        btnCambiarVista.setBackgroundColor(Color.parseColor("#1976D2"));
        btnCambiarVista.setTextColor(Color.WHITE);
        btnCambiarVista.setOnClickListener(v -> {
            modoMonitorManual = !modoMonitorManual;
            if (modoMonitorManual) {
                btnCambiarVista.setText("Cambiar a vista Usuario");
                Toast.makeText(this, "Modo monitor activado", Toast.LENGTH_SHORT).show();
            } else {
                btnCambiarVista.setText("Cambiar a vista Monitor");
                Toast.makeText(this, "Modo usuario activado", Toast.LENGTH_SHORT).show();
            }
            mostrarTurnos();
        });
        layoutTurnos.addView(btnCambiarVista);

        mostrarTurnos();
    }

    private void mostrarTurnos() {
        if (layoutTurnos.getChildCount() > 2) {
            layoutTurnos.removeViews(2, layoutTurnos.getChildCount() - 2);
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDiaActual.setText("Hoy: " + sdfHoy.format(calendar.getTime()));

        // Obtener todas las clases desde la API
        claseService.getClases().enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                List<Clase> clases = response.body();

                for (Clase clase : clases) {
                    Calendar fechaClase = Calendar.getInstance();
                    fechaClase.setTime(clase.getFechaInicio());

                    // Mostrar solo hoy y mañana
                    Calendar hoy = Calendar.getInstance();
                    hoy.set(Calendar.HOUR_OF_DAY,0); hoy.set(Calendar.MINUTE,0); hoy.set(Calendar.SECOND,0); hoy.set(Calendar.MILLISECOND,0);
                    Calendar manana = (Calendar) hoy.clone();
                    manana.add(Calendar.DAY_OF_YEAR, 1);

                    Calendar claseDia = (Calendar) fechaClase.clone();
                    claseDia.set(Calendar.HOUR_OF_DAY,0); claseDia.set(Calendar.MINUTE,0); claseDia.set(Calendar.SECOND,0); claseDia.set(Calendar.MILLISECOND,0);

                    if (!claseDia.equals(hoy) && !claseDia.equals(manana)) continue;

                    crearTurnoView(clase);
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                Toast.makeText(ReservarClasesActivity.this, "Error al cargar clases", Toast.LENGTH_SHORT).show();
            }
        });
    }

    

    private void crearTurnoView(Clase clase) {
        CardView card = new CardView(this);
        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setPadding(24,24,24,24);

        // Hora
        TextView tvHora = new TextView(this);
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvHora.setText("⏰ " + sdfHora.format(clase.getFechaInicio()) + " - " + sdfHora.format(clase.getFechaFin()));
        tvHora.setTextSize(20f);
        tvHora.setTextColor(Color.parseColor("#388E3C"));

        // Aforo
        TextView tvAforo = new TextView(this);
        int plazasOcupadas = clase.getUsuarios() != null ? clase.getUsuarios().size() : 0;
        int plazasLibres = clase.getAforo() - plazasOcupadas;
        tvAforo.setText("Aforo Máx: " + clase.getAforo() + " | Ocupadas: " + plazasOcupadas + " | Libres: " + plazasLibres);
        tvAforo.setTextColor(Color.DKGRAY);

        linear.addView(tvHora);
        linear.addView(tvAforo);

        // Botón reservar/cancelar
        if (!rolUsuario.equals("monitor") && !modoMonitorManual) {
            Button btnReservar = new Button(this);
            boolean estaReservado = clase.getUsuarios() != null &&
                    clase.getUsuarios().stream().anyMatch(u -> u.getUid().equals(uidUsuario));
            btnReservar.setText(estaReservado ? "Cancelar reserva" : "Reservar");
            btnReservar.setBackgroundColor(estaReservado ? Color.parseColor("#E53935") : Color.parseColor("#4CAF50"));
            btnReservar.setTextColor(Color.WHITE);

            btnReservar.setOnClickListener(v -> {
                if (clase.getUsuarios() == null) clase.setUsuarios(new ArrayList<>());
                if (estaReservado) {
                    clase.getUsuarios().removeIf(u -> u.getUid().equals(uidUsuario));
                } else if (plazasLibres > 0) {
                    clase.getUsuarios().add(currentUser);
                }

                claseService.actualizarClase(clase.getId(), clase).enqueue(new Callback<Clase>() {
                    @Override
                    public void onResponse(Call<Clase> call, Response<Clase> response) {
                        mostrarTurnos();
                    }
                    @Override
                    public void onFailure(Call<Clase> call, Throwable t) {
                        Toast.makeText(ReservarClasesActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            linear.addView(btnReservar);
        }

        // Vista monitor
        if (rolUsuario.equals("monitor") || modoMonitorManual) {
            LinearLayout linearNames = new LinearLayout(this);
            linearNames.setOrientation(LinearLayout.VERTICAL);
            linearNames.setPadding(0,8,0,8);

            if (clase.getUsuarios() != null && !clase.getUsuarios().isEmpty()) {
                for (Usuario u : clase.getUsuarios()) {
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER_VERTICAL);

                    TextView tvUser = new TextView(this);
                    tvUser.setText(" • " + u.getNombre());
                    tvUser.setTextColor(Color.BLACK);
                    tvUser.setTextSize(14f);
                    tvUser.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    Button btnEliminar = new Button(this);
                    btnEliminar.setText("❌");
                    btnEliminar.setTextSize(12f);
                    btnEliminar.setBackgroundColor(Color.TRANSPARENT);
                    btnEliminar.setTextColor(Color.RED);

                    btnEliminar.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Eliminar reserva")
                                .setMessage("¿Desea eliminar la reserva de " + u.getNombre() + "?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    clase.getUsuarios().removeIf(user -> user.getUid().equals(u.getUid()));
                                    claseService.actualizarClase(clase.getId(), clase).enqueue(new Callback<Clase>() {
                                        @Override
                                        public void onResponse(Call<Clase> call, Response<Clase> response) {
                                            mostrarTurnos();
                                        }
                                        @Override
                                        public void onFailure(Call<Clase> call, Throwable t) {
                                            Toast.makeText(ReservarClasesActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    });

                    row.addView(tvUser);
                    row.addView(btnEliminar);
                    linearNames.addView(row);
                }

                // Botón Finalizar clase / Limpiar reservas
                Button btnLimpiar = new Button(this);
                btnLimpiar.setText("Finalizar Clase / Limpiar reservas");
                btnLimpiar.setBackgroundColor(Color.parseColor("#E53935"));
                btnLimpiar.setTextColor(Color.WHITE);
                btnLimpiar.setPadding(0,16,0,16);
                btnLimpiar.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Finalizar clase")
                            .setMessage("¿Desea eliminar todas las reservas? Esta acción no se puede deshacer.")
                            .setPositiveButton("Sí, limpiar", (dialog, which) -> {
                                clase.setUsuarios(new ArrayList<>());
                                claseService.actualizarClase(clase.getId(), clase).enqueue(new Callback<Clase>() {
                                    @Override
                                    public void onResponse(Call<Clase> call, Response<Clase> response) {
                                        mostrarTurnos();
                                    }
                                    @Override
                                    public void onFailure(Call<Clase> call, Throwable t) {
                                        Toast.makeText(ReservarClasesActivity.this, "Error al limpiar reservas", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                });
                linearNames.addView(btnLimpiar);

            } else {
                TextView tvVacio = new TextView(this);
                tvVacio.setText("No hay reservas aún. 😔");
                linearNames.addView(tvVacio);
            }

            linear.addView(linearNames);
        }

        card.addView(linear);
        layoutTurnos.addView(card);
    }
}
