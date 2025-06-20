// src/main/java/com/example/musapiapp/dto/BusquedaArtistaDTO.java
package com.example.musapiapp.dto;

import java.io.Serializable;
import java.util.List;

public class BusquedaArtistaDTO implements Serializable {
    private int idArtista;
    private int idUsuario;
    private String nombre;
    private String nombreUsuario;
    private String descripcion;
    private String urlFoto;
    private List<BusquedaCancionDTO> canciones;

    // Constructor vacío (necesario para Gson/Retrofit)
    public BusquedaArtistaDTO() {}

    // Constructor con todos los campos
    public BusquedaArtistaDTO(int idArtista, String nombre, String nombreUsuario,
                              String descripcion, String urlFoto,
                              List<BusquedaCancionDTO> canciones) {
        this.idArtista = idArtista;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.descripcion = descripcion;
        this.urlFoto = urlFoto;
        this.canciones = canciones;
    }

    // Getters y setters
    public int getIdArtista() {
        return idArtista;
    }
    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlFoto() {
        return urlFoto;
    }
    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public List<BusquedaCancionDTO> getCanciones() {
        return canciones;
    }
    public void setCanciones(List<BusquedaCancionDTO> canciones) {
        this.canciones = canciones;
    }
}
