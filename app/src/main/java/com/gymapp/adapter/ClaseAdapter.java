package com.gymapp.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gymapp.R;
import com.gymapp.model.Clase;

import java.text.SimpleDateFormat;
import java.util.*;

public class ClaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CLASE = 1;

    private List<Object> items;
    private OnReservarClick listener;
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public interface OnReservarClick {
        void onReservar(Clase clase);
    }

    public ClaseAdapter(List<Object> items, OnReservarClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_CLASE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dia_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_clase, parent, false);
            return new ClaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).txtHeader.setText((String) items.get(position));
        } else {

            Clase clase = (Clase) items.get(position);
            ClaseViewHolder vh = (ClaseViewHolder) holder;

            // 🕒 Mostrar horario
            String horaInicio = formatoHora.format(clase.getFechaInicio());
            String horaFin = formatoHora.format(clase.getFechaFin());
            vh.txtHora.setText(horaInicio + " - " + horaFin);

            // 👥 Mostrar aforo
            vh.txtAforo.setText(
                    "Plazas: " + clase.getPlazasOcupadas() +
                            "/" + clase.getAforoMaximo() +
                            " (Disponibles: " + clase.plazasDisponibles() + ")"
            );

            // 🔥 Estado del botón
            if (clase.estaCompleta()) {
                vh.btnReservar.setEnabled(false);
                vh.btnReservar.setText("Clase completa");
            } else {
                vh.btnReservar.setEnabled(true);
                vh.btnReservar.setText("Reservar");
                vh.btnReservar.setOnClickListener(v -> listener.onReservar(clase));
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
        Button btnReservar;

        ClaseViewHolder(View v) {
            super(v);
            txtHora = v.findViewById(R.id.txtHora);
            txtAforo = v.findViewById(R.id.txtAforo);
            btnReservar = v.findViewById(R.id.btnReservar);
        }
    }
}