package com.gymapp.model;

public class Monitor extends Actor {

    public Monitor() {
        super();
    }

    // Constructor completo que llama al padre
    public Monitor(int id, String nombre, String username, String apellidos,
                   String email, String fotografia, String telefono,
                   int edad, Rol rol, String password) {
        super(id, nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }

    // Si quieres, también puedes mantener el constructor sin id
    public Monitor(String nombre, String username, String apellidos,
                   String email, String fotografia, String telefono,
                   int edad, Rol rol, String password) {
        super(0, nombre, username, apellidos, email, fotografia, telefono, edad, rol, password);
    }
}