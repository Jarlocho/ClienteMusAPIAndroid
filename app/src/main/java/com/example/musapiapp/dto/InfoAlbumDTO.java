package com.example.musapiapp.dto;

import java.io.Serializable;

public class InfoAlbumDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idAlbum;
    private String nombreArtista;
    private String nombre;
    private String urlFoto;
    private String fechaPublicacion;

    public InfoAlbumDTO() {}

    public InfoAlbumDTO(int idAlbum, String nombreArtista, String nombre,
                        String urlFoto, String fechaPublicacion) {
        this.idAlbum = idAlbum;
        this.nombreArtista = nombreArtista;
        this.nombre = nombre;
        this.urlFoto = urlFoto;
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getNombreArtista() {
        return nombreArtista;
    }

    public void setNombreArtista(String nombreArtista) {
        this.nombreArtista = nombreArtista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
