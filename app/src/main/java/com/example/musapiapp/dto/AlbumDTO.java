// src/main/java/com/example/musapiapp/dto/AlbumDTO.java
package com.example.musapiapp.dto;

import java.io.Serializable;

public class AlbumDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private int idUsuario;
    private String fotoPath;

    // Constructor vacío (necesario para Gson/Retrofit y Serialización)
    public AlbumDTO() { }

    // Constructor con todos los campos
    public AlbumDTO(String nombre, int idUsuario, String fotoPath) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.fotoPath = fotoPath;
    }

    // Getters & Setters
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFotoPath() {
        return fotoPath;
    }
    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }
}
