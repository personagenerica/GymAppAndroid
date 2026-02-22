package com.gymapp.model;

public class Usuario extends Actor {

    public Usuario() {
        super();
    }

    public Usuario(String nombre, String username, String apellidos,
                   String email, String fotografia, String telefono,
                   int edad, Rol rol, String password) {
        super(nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }
}