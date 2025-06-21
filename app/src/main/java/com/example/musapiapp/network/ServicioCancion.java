package com.example.musapiapp.network;

import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.CancionDTO;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ServicioCancion {
    // ¡OJO! plural “categoriasMusicales” para que coincida con tu @RequestMapping
    @GET("categoriasMusicales")
    Call<RespuestaCliente<List<CategoriaMusicalDTO>>> obtenerCategorias();

    // si luego necesitas obtener una por id:
    @GET("categoriasMusicales/{id}")
    Call<CategoriaMusicalDTO> obtenerCategoriaPorId(@Path("id") int id);

    @Multipart
    @POST("canciones/subir")
    Call<RespuestaCliente<String>> subirCancion(
            @Part("nombre") RequestBody nombre,
            @Part("idCategoriaMusical") RequestBody idCategoriaMusical,
            @Part MultipartBody.Part archivoCancion,
            @Part MultipartBody.Part urlFoto,
            @Part("duracionStr") RequestBody duracionStr,
            @Part("idAlbum") RequestBody idAlbum,
            @Part("posicionEnAlbum") RequestBody posicionEnAlbum,
            @Part("idPerfilArtistas") List<RequestBody> idPerfilArtistas
    );

    @GET("canciones/album/{idAlbum}/canciones")
    Call<RespuestaCliente<List<BusquedaCancionDTO>>> getCancionesPorAlbum(
            @Path("idAlbum") int idAlbum
    );

}

