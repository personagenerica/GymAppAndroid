package com.gymapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Clase implements Serializable {

    private int id;
    @SerializedName("fecha_inicio")
    private Date fechaInicio;

    @SerializedName("fecha_fin")
    private Date fechaFin;

    @SerializedName("aforo")
    private int aforoMaximo;

    private int plazasOcupadas;   // Mejor que calcular siempre usuarios.size()

    private List<Usuario> usuarios;
    private Monitor monitor;

    // Constructor vacío (IMPORTANTE para Retrofit)
    public Clase() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public int getAforoMaximo() { return aforoMaximo; }
    public void setAforoMaximo(int aforoMaximo) { this.aforoMaximo = aforoMaximo; }

    public int getPlazasOcupadas() { return plazasOcupadas; }
    public void setPlazasOcupadas(int plazasOcupadas) { this.plazasOcupadas = plazasOcupadas; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public Monitor getMonitor() { return monitor; }
    public void setMonitor(Monitor monitor) { this.monitor = monitor; }

    // 🔥 Método útil
    public boolean estaCompleta() {
        return plazasOcupadas >= aforoMaximo;
    }

    public int plazasDisponibles() {
        return aforoMaximo - plazasOcupadas;
    }
}