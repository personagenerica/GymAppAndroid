package com.gymapp.model;

public class Monitor extends Actor {

    public Monitor() {
        super();
    }

    public Monitor(String nombre, String username, String apellidos,
                   String email, String fotografia, String telefono,
                   int edad, Rol rol, String password) {
        super(nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }
}