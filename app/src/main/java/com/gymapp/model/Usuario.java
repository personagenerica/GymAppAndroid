package com.gymapp.model;

public class Usuario extends Actor {

    public Usuario() { super(); }

    // Constructor para crear usuario sin ID
    public Usuario(String nombre, String username, String apellidos,
                   String email, String fotografia, String telefono,
                   int edad, Rol rol, String password) {
        super(nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }

    // Constructor para recibir usuario del backend con ID
    public Usuario(int id, String nombre, String username, String apellidos,
                   String email, String fotografia, String telefono,
                   int edad, Rol rol, String password) {
        super(id, nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }
}