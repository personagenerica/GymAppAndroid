package com.gymapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Clase implements Serializable {

    private int id;
    private Date fechaInicio;
    private Date fechaFin;
    private int aforo;
    private List<Usuario> usuarios;
    private Monitor monitor;


    // 🔹 Constructor completo
    public Clase(int id, Date fechaInicio, Date fechaFin, int aforo,
                 List<Usuario> usuarios, Monitor monitor) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aforo = aforo;
        this.usuarios = usuarios;
        this.monitor = monitor;
    }

    // 🔹 Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
}
