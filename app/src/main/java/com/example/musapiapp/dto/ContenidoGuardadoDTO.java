package com.example.musapiapp.dto;

public class ContenidoGuardadoDTO {
    private int idUsuario;
    private String tipoContenido;
    private int idContenidoGuardado;

    public ContenidoGuardadoDTO() {}

    public ContenidoGuardadoDTO(int idUsuario, String tipoContenido, int idContenidoGuardado) {
        this.idUsuario = idUsuario;
        this.tipoContenido = tipoContenido;
        this.idContenidoGuardado = idContenidoGuardado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getTipoContenido() { return tipoContenido; }
    public void setTipoContenido(String tipoContenido) { this.tipoContenido = tipoContenido; }

    public int getIdContenidoGuardado() { return idContenidoGuardado; }
    public void setIdContenidoGuardado(int idContenidoGuardado) { this.idContenidoGuardado = idContenidoGuardado; }
}
