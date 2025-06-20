package com.example.musapiapp.network;

import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.UsuarioRegistro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServicioRegistros {

    @POST("categoriasMusicales/registrar")
    Call<CategoriaMusicalDTO> registrarCategoriaMusical(
            @Body CategoriaMusicalDTO categoriaMusical
    );

    @PUT("categoriasMusicales/{id}")
    Call<CategoriaMusicalDTO> actualizarCategoria(@Path("id") int id, @Body CategoriaMusicalDTO categoria);


}
