package com.gymapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gymapp.R;
import com.gymapp.FormularioProductoActivity;
import com.gymapp.model.Producto;
import com.gymapp.services.ProductoService;
import com.gymapp.database.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoAdapter extends ArrayAdapter<Producto> {

    private ProductoService productoService;

    public ProductoAdapter(Context context, List<Producto> productos) {
        super(context, 0, productos);
        productoService = ApiClient.getClient(context).create(ProductoService.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_producto, parent, false);
        }

        Producto producto = getItem(position);

        TextView tvProducto = convertView.findViewById(R.id.tvProducto);
        Button btnEditar = convertView.findViewById(R.id.btnEditar);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

        if (producto != null) {
            tvProducto.setText(producto.getId() + " - " + producto.getNombre());
        }

        // EDITAR
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FormularioProductoActivity.class);
            intent.putExtra("id", producto.getId());
            intent.putExtra("nombre", producto.getNombre());
            intent.putExtra("tipo", producto.getTipo());
            intent.putExtra("precio", producto.getPrecio());
            intent.putExtra("stock", producto.getStock());
            getContext().startActivity(intent);
        });

        // ELIMINAR con confirmación
        btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar")
                    .setMessage("¿Seguro que quieres eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        productoService.eliminarProducto(producto.getId())
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            remove(producto);
                                            notifyDataSetChanged();
                                            Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return convertView;
    }
}