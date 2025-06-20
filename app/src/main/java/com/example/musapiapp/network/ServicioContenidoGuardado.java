// app/src/main/java/com/example/musapiapp/network/ServicioContenidoGuardado.java
package com.example.musapiapp.network;

import com.example.musapiapp.dto.ContenidoGuardadoDTO;
import com.example.musapiapp.dto.ListaReproduccionDTO;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.RespuestaCliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServicioContenidoGuardado {
    @POST("contenidoGuardado/guardar")
    Call<RespuestaCliente<String>> guardarContenido(@Body ContenidoGuardadoDTO dto);

    @GET("contenidoGuardado/canciones/{userId}")
    Call<RespuestaCliente<List<BusquedaCancionDTO>>> getCancionesGuardadas(@Path("userId") int userId);

    @GET("contenidoGuardado/albumes/{userId}")
    Call<RespuestaCliente<List<BusquedaAlbumDTO>>> getAlbumesGuardados(@Path("userId") int userId);

    @GET("contenidoGuardado/artistas/{userId}")
    Call<RespuestaCliente<List<BusquedaArtistaDTO>>> getArtistasGuardados(@Path("userId") int userId);

    @GET("contenidoGuardado/listas/{userId}")
    Call<RespuestaCliente<List<ListaReproduccionDTO>>> getListasGuardadas(@Path("userId") int userId);
}
