package com.example.musapiapp.dto;

public class RespuestaCliente<T> {
    private String mensaje;
    private T datos;

    public String getMensaje() {
        return mensaje;
    }

    public T getDatos() {
        return datos;
    }
}
