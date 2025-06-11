package com.example.musapiapp.network;

import com.example.musapiapp.dto.SolicitudInicioSesion;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioRegistro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServicioUsuario {

    @POST("usuarios/registrar")
    Call<RespuestaCliente<UsuarioDTO>> registrarUsuario(@Body UsuarioRegistro usuario);
    @POST("usuarios/login")
    Call<RespuestaCliente<UsuarioDTO>> iniciarSesion(@Body SolicitudInicioSesion solicitud);

}
