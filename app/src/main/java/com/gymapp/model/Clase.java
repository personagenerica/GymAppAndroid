package com.gymapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Clase implements Serializable {

    private int id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int aforo;
    private List<Usuario> usuarios;
    private Monitor monitor; // ahora es el monitor completo

    public Clase() {}

    // --- Getters y setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public int getAforo() { return aforo; }
    public void setAforo(int aforo) { this.aforo = aforo; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public Monitor getMonitor() { return monitor; }
    public void setMonitor(Monitor monitor) { this.monitor = monitor; }

    // ---------------------------
    // MÉTODOS DE UTILIDAD
    // ---------------------------

    /** Comprueba si la clase ha alcanzado su aforo máximo */
    public boolean estaCompleta() {
        return usuarios != null && usuarios.size() >= aforo;
    }

    /** Devuelve el número de plazas disponibles */
    public int plazasDisponibles() {
        return aforo - (usuarios != null ? usuarios.size() : 0);
    }
}