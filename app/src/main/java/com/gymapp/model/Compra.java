package com.gymapp.model;

import java.io.Serializable;
import java.util.List;

public class Compra implements Serializable {

    private int id;
    private String ticket;
    private int cantidad;
    private Producto producto;
    private List<Usuario> usuarios;



    // 🔹 Constructor completo
    public Compra(int id, String ticket, int cantidad,
                  Producto producto, List<Usuario> usuarios) {
        this.id = id;
        this.ticket = ticket;
        this.cantidad = cantidad;
        this.producto = producto;
        this.usuarios = usuarios;
    }

    // 🔹 Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
