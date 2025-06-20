package com.example.musapiapp.network;

import com.example.musapiapp.dto.RespuestaCliente;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServicioContenido {

    @Multipart
    @POST("albums")
    Call<RespuestaCliente<Void>> crearAlbum(
            @Header("Authorization") String token,
            @Part("nombre") RequestBody nombre,
            @Part MultipartBody.Part foto
    );
}
