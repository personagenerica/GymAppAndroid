package com.gymapp.model;

public class Admin extends Actor {

    // Constructor vacío obligatorio para Gson
    public Admin() {
        super();
    }

    // Constructor completo
    public Admin(String nombre, String username, String apellidos,
                 String email, String fotografia, String telefono,
                 int edad, Rol rol, String password) {
        super(nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }
}