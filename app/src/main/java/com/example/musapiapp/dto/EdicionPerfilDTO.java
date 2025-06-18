package com.example.musapiapp.dto;

import java.io.Serializable;

public class EdicionPerfilDTO implements Serializable {
    private String nombre;
    private String pais;
    private String descripcion;
    // nuevo campo para la ruta de la foto local
    private String fotoPath;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoPath() {
        return fotoPath;
    }
    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }
}
