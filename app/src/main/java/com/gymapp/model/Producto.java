package com.gymapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Producto implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("precio")
    private double precio;

    @SerializedName("stock")
    private int stock;

    @SerializedName("version")
    private int version;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
