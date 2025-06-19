package com.example.musapiapp.dto;

public class MensajesChatDTO {
    private int idPerfilArtista;
    private String nombreUsuario;
    private String mensaje;

    public MensajesChatDTO(int idPerfilArtista, String nombreUsuario, String mensaje) {
        this.idPerfilArtista = idPerfilArtista;
        this.nombreUsuario   = nombreUsuario;
        this.mensaje         = mensaje;
    }

    // Getters & setters
    public int getIdPerfilArtista() { return idPerfilArtista; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getMensaje()       { return mensaje; }

    public void setIdPerfilArtista(int v){ idPerfilArtista = v; }
    public void setNombreUsuario(String v){ nombreUsuario = v; }
    public void setMensaje(String v){ mensaje = v; }
}
