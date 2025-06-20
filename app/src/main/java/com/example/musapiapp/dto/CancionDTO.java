package com.example.musapiapp.dto;

import java.util.List;

public class CancionDTO {
    private String nombre;
    private Integer idCategoriaMusical;
    // antes: private MultipartFile archivoCancion;
    private String archivoCancion;
    // antes: private MultipartFile urlFoto;
    private String urlFoto;
    private String duracionStr;
    private Integer idAlbum;
    private Integer posicionEnAlbum;
    private List<Integer> idPerfilArtistas;

    // getters y setters para todos los campos…
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getIdCategoriaMusical() { return idCategoriaMusical; }
    public void setIdCategoriaMusical(Integer idCategoriaMusical) {
        this.idCategoriaMusical = idCategoriaMusical;
    }

    public String getArchivoCancion() { return archivoCancion; }
    public void setArchivoCancion(String archivoCancion) {
        this.archivoCancion = archivoCancion;
    }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public String getDuracionStr() { return duracionStr; }
    public void setDuracionStr(String duracionStr) {
        this.duracionStr = duracionStr;
    }

    public Integer getIdAlbum() { return idAlbum; }
    public void setIdAlbum(Integer idAlbum) { this.idAlbum = idAlbum; }

    public Integer getPosicionEnAlbum() { return posicionEnAlbum; }
    public void setPosicionEnAlbum(Integer posicionEnAlbum) {
        this.posicionEnAlbum = posicionEnAlbum;
    }

    public List<Integer> getIdPerfilArtistas() { return idPerfilArtistas; }
    public void setIdPerfilArtistas(List<Integer> idPerfilArtistas) {
        this.idPerfilArtistas = idPerfilArtistas;
    }
}
