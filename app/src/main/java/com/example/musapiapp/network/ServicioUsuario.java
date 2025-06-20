// src/main/java/com/example/musapiapp/network/ServicioUsuario.java
package com.example.musapiapp.network;

import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.EdicionPerfilDTO;
import com.example.musapiapp.dto.EvaluacionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.SolicitudInicioSesion;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.UsuarioRegistro;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Interfaz Retrofit para gestionar las llamadas a los endpoints de usuario.
 */
public interface ServicioUsuario {

    @POST("usuarios/registrar")
    Call<RespuestaCliente<UsuarioDTO>> registrarUsuario(@Body UsuarioRegistro usuario);

    @POST("usuarios/login")
    Call<RespuestaCliente<UsuarioDTO>> iniciarSesion(@Body SolicitudInicioSesion solicitud);

    @Multipart
    @PUT("usuarios/{id}/editar-perfil")
    Call<Void> editarPerfil(
            @Header("Authorization") String bearerToken,
            @Path("id") int idUsuario,
            @Part("nombre") RequestBody nombre,
            @Part("nombreUsuario") RequestBody nombreUsuario,
            @Part("pais") RequestBody pais,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part foto    // puede ser null si no hay foto
    );

    @Multipart
    @POST("usuarios/crear-perfilArtista")
    Call<Void> crearPerfilArtista(
            @Header("Authorization") String bearerToken,
            @Part("idUsuario") int idUsuario,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part foto    // puede ser null si no hay foto
    );

    /**
     * Obtiene el perfil de artista por su ID y lo devuelve ya envuelto en el wrapper JSON.
     * Respuesta esperada: { "datos": { ... } }
     */
    @GET("usuarios/artista/{id}")
    Call<RespuestaCliente<BusquedaArtistaDTO>> obtenerPerfilArtista(
            @Header("Authorization") String bearerToken,
            @Path("id") int idArtista
    );
}
