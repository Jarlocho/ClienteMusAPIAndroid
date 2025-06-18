package com.example.musapiapp.logicaServicios;

import android.content.Context;

import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LogicaServicioUsuario {/*
    private final ServicioUsuario servicioUsuario;
    private final Context context;

    public ApiArtistaService(Context context) {
        this.context = context;
        this.servicioUsuario = ApiCliente.getClient(context).create(ServicioUsuario.class);
    }

    public interface ArtistaCallback {
        void onSuccess(BusquedaArtistaDTO artista);
        void onError(String mensajeError);
    }

    public void obtenerArtista(int idArtista, ArtistaCallback callback) {
        String token = Preferencias.obtenerToken(context);
        String bearer = token != null ? "Bearer " + token : "";

        servicioUsuario.obtenerArtistaPorId(bearer, idArtista).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject datos = jsonObject.getJSONObject("datos");

                        BusquedaArtistaDTO artista = parsearArtista(datos);
                        callback.onSuccess(artista);
                    } else {
                        String errorMsg = "Error: " + response.code();
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                        callback.onError(errorMsg);
                    }
                } catch (Exception e) {
                    callback.onError("Excepción al procesar respuesta: " + e.getMessage());
                    Log.e("API_ARTISTA", "Error: ", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    // Método síncrono (para usar con AsyncTask/RxJava)
    public BusquedaArtistaDTO obtenerArtistaSincrono(int idArtista) throws IOException {
        String token = Preferencias.obtenerToken(context);
        String bearer = token != null ? "Bearer " + token : "";

        Response<ResponseBody> response = servicioUsuario.obtenerArtistaPorId(bearer, idArtista).execute();
        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            JSONObject datos = new JSONObject(responseBody).getJSONObject("datos");
            return parsearArtista(datos);
        } else {
            throw new IOException(response.errorBody() != null ?
                    response.errorBody().string() : "Error desconocido");
        }
    }

    private BusquedaArtistaDTO parsearArtista(JSONObject datos) throws Exception {
        BusquedaArtistaDTO artista = new BusquedaArtistaDTO();
        artista.setIdArtista(datos.getInt("idArtista"));
        artista.setNombre(datos.getString("nombre"));
        artista.setNombreUsuario(datos.getString("nombreUsuario"));
        artista.setDescripcion(datos.getString("descripcion"));
        artista.setUrlFoto(datos.getString("urlFoto"));

        if (datos.has("canciones")) {
            JSONArray cancionesArray = datos.getJSONArray("canciones");
            List<BusquedaCancionDTO> canciones = new ArrayList<>();
            for (int i = 0; i < cancionesArray.length(); i++) {
                canciones.add(parsearCancion(cancionesArray.getJSONObject(i)));
            }
            artista.setCanciones(canciones);
        }
        return artista;
    }

    private BusquedaCancionDTO parsearCancion(JSONObject cancionJson) throws Exception {
        BusquedaCancionDTO cancion = new BusquedaCancionDTO();
        cancion.setIdCancion(cancionJson.getInt("idCancion"));
        cancion.setTitulo(cancionJson.getString("titulo"));
        // ... otros campos
        return cancion;
    }*/
}
