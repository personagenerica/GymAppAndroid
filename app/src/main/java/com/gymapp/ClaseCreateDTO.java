package com.gymapp;

import java.io.Serializable;

public class ClaseCreateDTO implements Serializable {

    private String fecha_inicio;
    private String fecha_fin;
    private int aforo;
    private int monitorId; // solo el ID

    public ClaseCreateDTO(String fecha_inicio, String fecha_fin, int aforo, int monitorId) {
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.aforo = aforo;
        this.monitorId = monitorId;
    }

    // Getters y setters
    public String getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(String fecha_inicio) { this.fecha_inicio = fecha_inicio; }

    public String getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(String fecha_fin) { this.fecha_fin = fecha_fin; }

    public int getAforo() { return aforo; }
    public void setAforo(int aforo) { this.aforo = aforo; }

    public int getMonitorId() { return monitorId; }
    public void setMonitorId(int monitorId) { this.monitorId = monitorId; }
}