package com.gymapp.adapter;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gymapp.R;
import com.gymapp.model.Actor;
import com.gymapp.model.Clase;
import com.gymapp.model.Rol;

import java.text.SimpleDateFormat;
import java.util.*;

public class ClaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CLASE = 1;

    private List<Object> items;
    private OnReservarClick listener;

    // Formato de hora para mostrar en UI
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

    // Formato ISO 8601 que envía el backend
    private SimpleDateFormat isoFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());

    public interface OnReservarClick {
        void onReservar(Clase clase);
    }

    public ClaseAdapter(List<Object> items, OnReservarClick listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Object o = items.get(position);
        if (o instanceof String) return TYPE_HEADER;
        if (o instanceof Clase) return TYPE_CLASE;
        return TYPE_HEADER;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_dia_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_clase, parent, false);
            return new ClaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            String header = item instanceof String ? (String) item : "Sin día";
            ((HeaderViewHolder) holder).txtHeader.setText(header);

        } else if (holder instanceof ClaseViewHolder) {
            if (!(item instanceof Clase)) return;

            Clase clase = (Clase) item;
            ClaseViewHolder vh = (ClaseViewHolder) holder;

            // 🕒 Mostrar horario
            String horaTexto = "Hora no disponible";
            try {
                if (clase.getFechaInicio() != null && clase.getFechaFin() != null) {
                    Date inicio = isoFormat.parse(clase.getFechaInicio());
                    Date fin = isoFormat.parse(clase.getFechaFin());

                    if (inicio != null && fin != null) {
                        String hInicio = formatoHora.format(inicio);
                        String hFin = formatoHora.format(fin);
                        horaTexto = hInicio + " - " + hFin;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            vh.txtHora.setText(horaTexto);

            // 👥 Mostrar aforo
            int ocupadas = clase.getUsuarios() != null ? clase.getUsuarios().size() : 0;
            int aforo = clase.getAforo();
            int disponibles = aforo - ocupadas;
            vh.txtAforo.setText("Plazas: " + ocupadas + "/" + aforo +
                    " (Disponibles: " + disponibles + ")");

            // 🔥 Botón reservar
            if (clase.estaCompleta()) {
                vh.btnReservar.setEnabled(false);
                vh.btnReservar.setText("Clase completa");
            } else {
                vh.btnReservar.setEnabled(true);

                // Si el usuario ya está inscrito, mostrar "Reservado"
                if (clase.getUsuarios() != null && !clase.getUsuarios().isEmpty()) {
                    // Aquí puedes comprobar el ID del usuario actual si lo tienes
                    // Por ejemplo:
                    // boolean yaReservado = clase.getUsuarios().stream().anyMatch(u -> u.getId() == usuarioId);
                    // Si yaReservado: vh.btnReservar.setText("Reservado");
                }

                vh.btnReservar.setText("Reservar");
                vh.btnanularReserva.setText("Anular Reserva");
                vh.btnanularReserva.setVisibility(GONE);

                SharedPreferences prefs = vh.itemView.getContext().getSharedPreferences("auth_prefs", MODE_PRIVATE);
                String usernameUserLogin = prefs.getString("username", null);
                String rolUserLogin = prefs.getString("rol", null);

                for (Actor a: clase.getUsuarios()) {
                    if(a.getUsername().equals(usernameUserLogin)) {
                        vh.btnReservar.setVisibility(GONE);
                        vh.btnanularReserva.setVisibility(VISIBLE);

                        break;
                    }
                }

                if(rolUserLogin.equals(Rol.Monitor.toString()) ||  rolUserLogin.equals(Rol.Admin.toString())) {
                    vh.btnReservar.setVisibility(GONE);
                }

                vh.btnReservar.setOnClickListener(v -> {
                    if (listener != null) listener.onReservar(clase);
                });
            }
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtHeader;

        HeaderViewHolder(View v) {
            super(v);
            txtHeader = v.findViewById(R.id.txtDiaFecha);
        }
    }

    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView txtHora, txtAforo;
        Button btnReservar,btnanularReserva;

        ClaseViewHolder(View v) {
            super(v);
            txtHora = v.findViewById(R.id.txtHora);
            txtAforo = v.findViewById(R.id.txtAforo);
            btnReservar = v.findViewById(R.id.btnReservar);
            btnanularReserva = v.findViewById(R.id.btnanularReserva);
        }
    }

    public void actualizarItems(List<Object> nuevosItems) {
        items.clear();
        if (nuevosItems != null) items.addAll(nuevosItems);
        notifyDataSetChanged();
    }
}