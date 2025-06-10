package com.example.musapiapp.dto;

public class SolicitudInicioSesion {
    private String correo;
    private String contrasenia;

    public SolicitudInicioSesion(String correo, String contrasenia) {
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }
}
