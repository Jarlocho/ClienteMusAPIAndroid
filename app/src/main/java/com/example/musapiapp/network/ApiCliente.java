// src/main/java/com/example/musapiapp/network/ApiCliente.java
package com.example.musapiapp.network;

import android.content.Context;

import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.Preferencias;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCliente {
    /** BaseURL para Retrofit (apunta a http://10.0.2.2:8080/api/) */
    private static final String BASE_URL = Constantes.URL_API;

    /** URL sin “/api” para cargar imágenes, ficheros, etc. */
    private static       String URL_ARCHIVOS = Constantes.URL_BASE;

    private static Retrofit retrofit = null;

    /** Getter/Setter para URL_ARCHIVOS en caso de cambiar dinámicamente **/
    public static String getUrlArchivos() {
        return URL_ARCHIVOS;
    }
    public static void setUrlArchivos(String urlArchivos) {
        URL_ARCHIVOS = urlArchivos;
    }

    /** Crea (o devuelve) la instancia singleton de Retrofit **/
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // 1) Interceptor de autenticación sólo para peticiones a BASE_URL
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    String url = original.url().toString();

                    if (url.startsWith(BASE_URL)) {
                        String token = Preferencias.obtenerToken(context);
                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                    }
                    return chain.proceed(builder.build());
                }
            };

            // 2) Interceptor de logging
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 3) Cliente OkHttp con ambos interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logInterceptor)
                    .build();

            // 4) Instancia Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static ServicioContenidoGuardado getContenidoGuardadoService(Context context) {
        return getClient(context).create(ServicioContenidoGuardado.class);
    }
    public static ServicioListasDeReproduccion getListasDeReproduccionService(Context context) {
        return getClient(context).create(ServicioListasDeReproduccion.class);
    }


}
