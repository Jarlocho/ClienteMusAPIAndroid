// src/main/java/com/example/musapiapp/dto/BusquedaCancionDTO.java
package com.example.musapiapp.dto;

import java.io.Serializable;

public class BusquedaCancionDTO implements Serializable {
    private int idCancion;
    private String nombre;
    private String duracion;
    private String urlArchivo;
    private String urlFoto;
    private String nombreArtista;
    private String fechaPublicacion;
    private String nombreAlbum;
    private String categoriaMusical;

    // Constructor vacío (necesario para Gson/Retrofit)
    public BusquedaCancionDTO() {}

    // Constructor con todos los campos
    public BusquedaCancionDTO(int idCancion,
                              String nombre,
                              String duracion,
                              String urlArchivo,
                              String urlFoto,
                              String nombreArtista,
                              String fechaPublicacion,
                              String nombreAlbum,
                              String categoriaMusical) {
        this.idCancion = idCancion;
        this.nombre = nombre;
        this.duracion = duracion;
        this.urlArchivo = urlArchivo;
        this.urlFoto = urlFoto;
        this.nombreArtista = nombreArtista;
        this.fechaPublicacion = fechaPublicacion;
        this.nombreAlbum = nombreAlbum;
        this.categoriaMusical = categoriaMusical;
    }

    // Getters y setters
    public int getIdCancion() {
        return idCancion;
    }
    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDuracion() {
        return duracion;
    }
    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }
    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }
    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getNombreArtista() {
        return nombreArtista;
    }
    public void setNombreArtista(String nombreArtista) {
        this.nombreArtista = nombreArtista;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }
    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getNombreAlbum() {
        return nombreAlbum;
    }
    public void setNombreAlbum(String nombreAlbum) {
        this.nombreAlbum = nombreAlbum;
    }

    public String getCategoriaMusical() {
        return categoriaMusical;
    }
    public void setCategoriaMusical(String categoriaMusical) {
        this.categoriaMusical = categoriaMusical;
    }
}
