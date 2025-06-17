// src/main/java/com/example/musapiapp/dto/BusquedaAlbumDTO.java
package com.example.musapiapp.dto;

import java.io.Serializable;
import java.util.List;

public class BusquedaAlbumDTO implements Serializable {
    private static final long serialVersionUID = 1L;    private int idAlbum;
    private String nombreAlbum;
    private String nombreArtista;
    private String fechaPublicacion;
    private String urlFoto;
    private List<BusquedaCancionDTO> canciones;

    public BusquedaAlbumDTO() {}

    public BusquedaAlbumDTO(int idAlbum, String nombreAlbum, String nombreArtista,
                            String fechaPublicacion, String urlFoto,
                            List<BusquedaCancionDTO> canciones) {
        this.idAlbum = idAlbum;
        this.nombreAlbum = nombreAlbum;
        this.nombreArtista = nombreArtista;
        this.fechaPublicacion = fechaPublicacion;
        this.urlFoto = urlFoto;
        this.canciones = canciones;
    }

    public int getIdAlbum() {
        return idAlbum;
    }
    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getNombreAlbum() {
        return nombreAlbum;
    }
    public void setNombreAlbum(String nombreAlbum) {
        this.nombreAlbum = nombreAlbum;
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
