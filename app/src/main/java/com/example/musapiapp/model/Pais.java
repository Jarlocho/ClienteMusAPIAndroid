package com.example.musapiapp.model;

public class Pais {
    private String codigo;
    private String nombre;

    public Pais(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
