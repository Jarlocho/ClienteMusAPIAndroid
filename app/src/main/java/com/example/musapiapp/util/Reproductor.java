package com.example.musapiapp.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.musapiapp.dto.BusquedaCancionDTO;

import java.io.IOException;
import java.util.ArrayList;

public class Reproductor {
    private static MediaPlayer mediaPlayer;
    private static ArrayList<BusquedaCancionDTO> listaCanciones;
    private static int indiceActual = 0;
    private static float volumenActual = 1.0f;
    private static Context contexto;

    public interface ReproductorListener {
        void onReproduccionIniciada();
        void onReproductorPausadoODetenido();
    }

    private static ReproductorListener listener;

    public static void inicializar(Context ctx, ReproductorListener l) {
        contexto = ctx;
        listener = l;
    }

    // Métodos por implementar...

    public static void reproducirCancion(ArrayList<BusquedaCancionDTO> canciones, int indice) {
        if (canciones == null || canciones.size() == 0 || indice < 0 || indice >= canciones.size())
            return;

        detener();

        listaCanciones = canciones;
        indiceActual = indice;

        try {
            BusquedaCancionDTO cancion = canciones.get(indice);
            String urlCompleta = Constantes.URL_BASE + cancion.getUrlArchivo();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(urlCompleta);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(volumenActual, volumenActual);

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (listener != null) listener.onReproduccionIniciada();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (listener != null) listener.onReproductorPausadoODetenido();

                // Avanzar automáticamente
                if (indiceActual + 1 < listaCanciones.size()) {
                    indiceActual++;
                    reproducirCancion(listaCanciones, indiceActual);
                }
            });

            mediaPlayer.prepareAsync(); // Carga en background
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pausarReanudar() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (listener != null) listener.onReproductorPausadoODetenido();
        } else {
            mediaPlayer.start();
            if (listener != null) listener.onReproduccionIniciada();
        }
    }

    public static void detener() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            if (listener != null) listener.onReproductorPausadoODetenido();
        }
    }

    public static void siguienteCancion() {
        if (indiceActual + 1 < listaCanciones.size()) {
            indiceActual++;
            reproducirCancion(listaCanciones, indiceActual);
        }
    }

    public static void cancionAnterior() {
        if (indiceActual > 0) {
            indiceActual--;
            reproducirCancion(listaCanciones, indiceActual);
        }
    }

    public static void setVolumen(float volumen) {
        volumenActual = Math.max(0f, Math.min(1f, volumen));
        if (mediaPlayer != null)
            mediaPlayer.setVolume(volumenActual, volumenActual);
    }

}

