package com.example.musapiapp.network;

import com.example.musapiapp.dto.AlbumDTO;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.InfoAlbumDTO;
import com.example.musapiapp.dto.RespuestaCliente;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicioAlbum {

    @Multipart
    @POST("albumes/crear")
    Call<RespuestaCliente<String>> crearAlbum(
            @PartMap Map<String, RequestBody> campos,
            @Part MultipartBody.Part urlFoto
    );



    @GET("albumes/pendientes")
    Call<RespuestaCliente<List<InfoAlbumDTO>>> obtenerAlbumesPendientes(@Query("idPerfilArtista") int idPerfilArtista);

    @GET("albumes/artista")
    Call<RespuestaCliente<List<BusquedaAlbumDTO>>> obtenerAlbumesPublicos(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    @PUT("albumes/publicar/{idAlbum}")
    Call<RespuestaCliente<String>> publicarAlbum(@Path("idAlbum") int idAlbum);
}
