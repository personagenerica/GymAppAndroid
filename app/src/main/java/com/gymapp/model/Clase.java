package com.gymapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Clase implements Serializable {

    private int id;

    @SerializedName("fecha_inicio")
    private String fechaInicio; // ISO 8601

    @SerializedName("fecha_fin")
    private String fechaFin; // ISO 8601

    @SerializedName("aforo")
    private int aforo; // coincidir con backend

    private List<Usuario> usuarios;

    @SerializedName("monitor")
    private MonitorId monitor; // solo el ID del monitor

    public Clase() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public int getAforo() { return aforo; }
    public void setAforo(int aforo) { this.aforo = aforo; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public MonitorId getMonitor() { return monitor; }

    // ✅ Método correcto para setear solo el ID del monitor
    public void setMonitorId(int monitorId) {
        this.monitor = new MonitorId(monitorId);
    }

    public boolean estaCompleta() {
        return usuarios != null && usuarios.size() >= aforo;
    }

    public int plazasDisponibles() {
        return aforo - (usuarios != null ? usuarios.size() : 0);
    }
}