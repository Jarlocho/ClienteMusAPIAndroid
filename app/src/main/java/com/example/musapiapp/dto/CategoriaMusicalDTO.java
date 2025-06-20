package com.example.musapiapp.dto;

public class CategoriaMusicalDTO {
    private int idCategoriaMusical;
    private String nombre;
    private String descripcion;

    public CategoriaMusicalDTO(int idCategoriaMusical, String nombre, String descripcion) {
        this.idCategoriaMusical = idCategoriaMusical;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdCategoriaMusical() {
        return idCategoriaMusical;
    }

    public void setIdCategoriaMusical(int idCategoriaMusical) {
        this.idCategoriaMusical = idCategoriaMusical;
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


}
