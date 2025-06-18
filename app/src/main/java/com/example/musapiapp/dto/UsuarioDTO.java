package com.example.musapiapp.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
    private int idUsuario;
    private String correo;
    private String pais;
    private String nombre;
    private String nombreUsuario;
    private boolean esArtista;
    private boolean esAdmin;
    private String token;
    public int getIdUsuario() { return idUsuario; }
    public String getCorreo() { return correo; }
    public String getPais() { return pais; }
    public String getNombre() { return nombre; }
    public String getNombreUsuario() { return nombreUsuario; }
    public boolean isEsArtista() { return esArtista; }
    public boolean isEsAdmin() { return esAdmin; }
    public String getToken() { return token; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
