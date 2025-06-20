// app/src/main/java/com/example/musapiapp/network/ServicioListasDeReproduccion.java
package com.example.musapiapp.network;

import com.example.musapiapp.dto.AgregarCancionDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.ListaReproduccionDTO;
import com.example.musapiapp.dto.RespuestaCliente;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface ServicioListasDeReproduccion {
    @Multipart
    @POST("listasDeReproduccion/crear")
    Call<RespuestaCliente<String>> crearLista(
            @Part("nombre") RequestBody nombre,
            @Part("descripcion") RequestBody descripcion,
            @Part("idUsuario") RequestBody idUsuario,
            @Part MultipartBody.Part foto  // opcional, si van a subir imagen
    );

    @POST("listasDeReproduccion/agregar-cancion")
    Call<RespuestaCliente<String>> agregarCancion(@Body AgregarCancionDTO dto);

    @GET("listasDeReproduccion/usuario/{userId}")
    Call<RespuestaCliente<List<ListaReproduccionDTO>>> getListasUsuario(@Path("userId") int userId);

    @GET("listasDeReproduccion/{listaId}/canciones")
    Call<RespuestaCliente<List<BusquedaCancionDTO>>> getCancionesDeLista(@Path("listaId") int listaId);
}
