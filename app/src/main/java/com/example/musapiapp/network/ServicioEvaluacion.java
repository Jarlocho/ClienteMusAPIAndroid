package com.example.musapiapp.network;

import com.example.musapiapp.dto.EvaluacionDTO;
import com.example.musapiapp.dto.RespuestaCliente;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServicioEvaluacion {
    /**
     * Registra una evaluación para un artista.
     * Mapea a POST http://<host>:<puerto>/api/evaluaciones/registrar
     */
    @POST("evaluaciones/registrar")
    Call<RespuestaCliente<String>> registrar(
            @Header("Authorization") String bearerToken,
            @Body EvaluacionDTO evaluacion
    );
}
