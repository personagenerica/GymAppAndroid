package com.gymapp.model;

import java.io.Serializable;

public class Monitor implements Serializable {

    private String uid;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private int edad;
    private String role;


    // 🔹 Constructor completo
    public Monitor(String uid, String nombre, String apellido,
                   String email, String telefono, int edad, String role) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.edad = edad;
        this.role = role;
    }

    // 🔹 Getters y Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
