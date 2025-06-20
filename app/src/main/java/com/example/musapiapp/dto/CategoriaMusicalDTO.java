package com.example.musapiapp.dto;

import com.example.musapiapp.activities.CategoriaMusicalActivity;

public class CategoriaMusicalDTO {
    private Integer idCategoriaMusical;
    private String nombre;
    private String descripcion;

    public CategoriaMusicalDTO(){}
    public CategoriaMusicalDTO(int idCategoriaMusical, String nombre, String descripcion) {
        this.idCategoriaMusical = idCategoriaMusical;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getIdCategoriaMusical() {
        return idCategoriaMusical;
    }

    public void setIdCategoriaMusical(Integer idCategoriaMusical) {
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
