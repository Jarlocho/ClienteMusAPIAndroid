// src/main/java/com/example/musapiapp/network/ServicioBusqueda.java
package com.example.musapiapp.network;

import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.RespuestaCliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServicioBusqueda {
    @GET("usuarios/artistas/buscar")
    Call<RespuestaCliente<List<BusquedaArtistaDTO>>> buscarArtistas(@Query("texto") String texto);

    @GET("canciones/buscar")
    Call<RespuestaCliente<List<BusquedaCancionDTO>>> buscarCanciones(@Query("texto") String texto);

    @GET("albumes/buscar")
    Call<RespuestaCliente<List<BusquedaAlbumDTO>>> buscarAlbumes(@Query("texto") String texto);
}
