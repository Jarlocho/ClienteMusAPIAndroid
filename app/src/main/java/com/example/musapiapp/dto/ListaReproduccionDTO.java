// src/main/java/com/example/musapiapp/dto/ListaReproduccionDTO.java
package com.example.musapiapp.dto;

import java.io.Serializable;

public class ListaReproduccionDTO implements Serializable {
    private int    idListaDeReproduccion;
    private String nombre;
    private String descripcion;
    private int    idUsuario;
    private String fotoPath;

    public ListaReproduccionDTO() {}

    public ListaReproduccionDTO(int idListaDeReproduccion, String nombre, String descripcion, int idUsuario, String fotoPath) {
        this.idListaDeReproduccion = idListaDeReproduccion;
        this.nombre                = nombre;
        this.descripcion           = descripcion;
        this.idUsuario             = idUsuario;
        this.fotoPath              = fotoPath;
    }

    public int getIdListaDeReproduccion() {
        return idListaDeReproduccion;
    }
    public void setIdListaDeReproduccion(int id) {
        this.idListaDeReproduccion = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
