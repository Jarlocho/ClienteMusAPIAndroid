package com.example.musapiapp.dto;

public class UsuarioRegistro {
    private String nombre;
    private String nombreUsuario;
    private String correo;
    private String pais;
    private String contrasenia;

    public UsuarioRegistro(String nombre, String nombreUsuario, String correo, String pais, String contrasenia) {
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.pais = pais;
        this.contrasenia = contrasenia;
    }

    public String getNombre() { return nombre; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreo() { return correo; }
    public String getPais() { return pais; }
    public String getContrasenia() { return contrasenia; }
}
