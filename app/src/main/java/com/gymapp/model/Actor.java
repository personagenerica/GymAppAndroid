package com.gymapp.model;

public class Actor {
    private String nombre;
    private String username;

        private String apellidos;


        private String email;


        private String fotografia;

        private String telefono;

        private int edad;

        private Rol rol;

        private String password;

        private String firebaseId;

        public Actor() {
            super();
        }

        public Actor(String nombre,String username, String apellidos,
                      String email,  String fotografia,
                  String telefono,
                     int edad, Rol rol,
                   String password) {
            super();
            this.nombre = nombre;
            this.username = username;
            this.apellidos = apellidos;
            this.email = email;
            this.fotografia = fotografia;
            this.telefono = telefono;
            this.edad = edad;
            this.rol = rol;
            this.password = password;
        }

        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFotografia() { return fotografia; }
        public void setFotografia(String fotografia) { this.fotografia = fotografia; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }

        public int getEdad() { return edad; }
        public void setEdad(int edad) { this.edad = edad; }

        public Rol getRol() { return rol; }
        public void setRol(Rol rol) { this.rol = rol; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFirebaseId() { return firebaseId; }
        public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }

    }


