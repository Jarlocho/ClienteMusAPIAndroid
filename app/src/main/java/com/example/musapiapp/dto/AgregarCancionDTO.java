package com.example.musapiapp.dto;

public class AgregarCancionDTO {
    private int playlistId;
    private int idCancion;

    public AgregarCancionDTO() {}

    public AgregarCancionDTO(int playlistId, int idCancion) {
        this.playlistId = playlistId;
        this.idCancion = idCancion;
    }

    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }

    public int getIdCancion() { return idCancion; }
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }
}
