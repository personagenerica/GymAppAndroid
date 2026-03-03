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
import com.gymapp.services.ClaseService;
import com.gymapp.database.ApiClient;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CLASE = 1;

    private List<Object> items;
    private final OnReservarClick listener;
    private final SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());

    public interface OnReservarClick {
        void onReservar(Clase clase);
    }

    public ClaseAdapter(List<Object> items, OnReservarClick listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        return (item instanceof String) ? TYPE_HEADER : TYPE_CLASE;
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
            ((HeaderViewHolder) holder).txtHeader.setText((String) item);

        } else if (holder instanceof ClaseViewHolder && item instanceof Clase) {
            Clase clase = (Clase) item;
            ClaseViewHolder vh = (ClaseViewHolder) holder;

            // Mostrar horario y aforo
            vh.txtHora.setText(getHorarioTexto(clase));
            int ocupadas = clase.getUsuarios() != null ? clase.getUsuarios().size() : 0;
            int aforo = clase.getAforo();
            vh.txtAforo.setText("Plazas: " + ocupadas + "/" + aforo +
                    " (Disponibles: " + (aforo - ocupadas) + ")");

            configurarBotones(vh, clase);
        }
    }

    private String getHorarioTexto(Clase clase) {
        try {
            if (clase.getFechaInicio() != null && clase.getFechaFin() != null) {
                Date inicio = isoFormat.parse(clase.getFechaInicio());
                Date fin = isoFormat.parse(clase.getFechaFin());
                if (inicio != null && fin != null) {
                    return formatoHora.format(inicio) + " - " + formatoHora.format(fin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Hora no disponible";
    }

    private void configurarBotones(ClaseViewHolder vh, Clase clase) {
        SharedPreferences prefs = vh.itemView.getContext().getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        String token = prefs.getString("jwt_token", null);
        String rol = prefs.getString("rol", null);

        boolean usuarioReservado = false;
        if (clase.getUsuarios() != null) {
            for (Actor a : clase.getUsuarios()) {
                if (a.getUsername().equals(username)) {
                    usuarioReservado = true;
                    break;
                }
            }
        }

        // Reset visibilidad
        vh.btnReservar.setVisibility(VISIBLE);
        vh.btnanularReserva.setVisibility(GONE);

        if (rol.equals(Rol.Monitor.toString()) || rol.equals(Rol.Admin.toString())) {
            vh.btnReservar.setVisibility(GONE);
        } else if (usuarioReservado) {
            vh.btnReservar.setVisibility(GONE);
            vh.btnanularReserva.setVisibility(VISIBLE);
        } else if (clase.estaCompleta()) {
            vh.btnReservar.setEnabled(false);
            vh.btnReservar.setText("Clase completa");
        } else {
            vh.btnReservar.setEnabled(true);
            vh.btnReservar.setText("Reservar");
        }

        // Reservar clase
        vh.btnReservar.setOnClickListener(v -> {
            if (listener != null) listener.onReservar(clase);
        });

        // Anular reserva
        vh.btnanularReserva.setOnClickListener(v -> {
            ClaseService claseService = ApiClient.getClient(vh.itemView.getContext()).create(ClaseService.class);
            claseService.anularClase(clase.getId(), "Bearer " + token).enqueue(new Callback<Clase>() {
                @Override
                public void onResponse(Call<Clase> call, Response<Clase> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(vh.itemView.getContext(), "Reserva anulada", Toast.LENGTH_SHORT).show();

                        if (clase.getUsuarios() != null) {
                            clase.getUsuarios().removeIf(u -> u.getUsername().equals(username));
                        }

                        notifyItemChanged(vh.getAdapterPosition());
                    } else {
                        Toast.makeText(vh.itemView.getContext(), "Error al anular reserva", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Clase> call, Throwable t) {
                    Toast.makeText(vh.itemView.getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ViewHolders
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtHeader;
        HeaderViewHolder(View v) {
            super(v);
            txtHeader = v.findViewById(R.id.txtDiaFecha);
        }
    }

    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView txtHora, txtAforo;
        Button btnReservar, btnanularReserva;
        ClaseViewHolder(View v) {
            super(v);
            txtHora = v.findViewById(R.id.txtHora);
            txtAforo = v.findViewById(R.id.txtAforo);
            btnReservar = v.findViewById(R.id.btnReservar);
            btnanularReserva = v.findViewById(R.id.btnanularReserva);
        }
    }
}