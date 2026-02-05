package com.gymapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gymapp.R;
import com.gymapp.model.Producto;

import java.util.List;
//Para crear ListView y Spinner es necesario utilizar un adapter, que actúa como puente entre los componentes y
//los datos, definiendo el diseño mediante un layout XML y asignando los valores a las vistas.
public class ProductoAdapter extends ArrayAdapter<Producto> {
    public ProductoAdapter(@NonNull Context context, List<Producto> productos) {
        super(context, 0, productos); // Se usa 0 como resource porque se inflará la vista manualmente
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.item_selected);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item_drop);
    }
    private View createViewFromResource(int position, View convertView, ViewGroup parent, int layoutResource) {
// 1. Obtener el objeto de datos para esta posición
        Producto producto = getItem(position);
        // 2. Inflar la vista si no se está reutilizando
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResource, parent, false); // Parent es el contenedor del ListView/Spinner
        }
// 3. Busca el TextView por el ID y asigna un texto
        TextView textView = convertView.findViewById(R.id.textProducto);
        if (producto != null && textView != null) {
            textView.setText(producto.getId() + " - " + producto.getNombre());
        }
        return convertView;
    }
}

