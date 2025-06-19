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
<<<<<<< HEAD
    private static String BASE_URL = "http://10.0.2.2:8080/api/";
    //private static String BASE_URL = "http://192.168.1.9:8080/api/";

    public static String getUrlArchivos() {
        return URL_ARCHIVOS;
    }

    public static void setUrlArchivos(String urlArchivos) {
        URL_ARCHIVOS = urlArchivos;
    }

    private static String URL_ARCHIVOS = "http://10.0.2.2:8080";
    //private static String URL_ARCHIVOS = "http://192.168.1.9:8080";
=======
    //Ruta Jarly private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static final String BASE_URL = Constantes.URL_API;

>>>>>>> 25e58b2956d22705ecae0b7003a3ea39ee7f7cdd
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // 1) Interceptor de autenticación SOLO para URLs que empiecen por BASE_URL
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String url = original.url().toString();
                    Request.Builder builder = original.newBuilder();

                    // sólo si es petición a tu API (/api/...)
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

            // 4) Retrofit con el cliente personalizado
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
