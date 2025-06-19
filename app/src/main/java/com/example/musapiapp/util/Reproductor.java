package com.example.musapiapp.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.musapiapp.dto.BusquedaCancionDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public static BusquedaCancionDTO getCancionActual() {
        if (listaCanciones != null && indiceActual >= 0 && indiceActual < listaCanciones.size()) {
            return listaCanciones.get(indiceActual);
        }
        return null;
    }


    public static void inicializar(Context ctx, ReproductorListener l) {
        contexto = ctx;
        listener = l;
    }

    // Métodos por implementar...

    public static void reproducirCancion(ArrayList<BusquedaCancionDTO> canciones, int indice, Context ctx) {
        contexto = ctx;

        // Si el contexto también es un listener, lo usamos automáticamente
        if (ctx instanceof ReproductorListener) {
            listener = (ReproductorListener) ctx;
        }

        reproducirCancion(canciones, indice);
    }
    public static void reproducirCancion(ArrayList<BusquedaCancionDTO> canciones, int indice) {
        if (canciones == null || canciones.size() == 0 || indice < 0 || indice >= canciones.size()) return;

        listaCanciones = canciones;
        indiceActual = indice;

        new Thread(() -> {
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    detener(); // 🔒 asegurar que no se solapen
                }

                String token = Preferencias.obtenerToken(contexto);
                String bearer = token != null ? "Bearer " + token : "";

                BusquedaCancionDTO cancion = canciones.get(indice);
                String urlCompleta = Constantes.URL_BASE + cancion.getUrlArchivo();

                URL cancionUrl = new URL(urlCompleta);
                HttpURLConnection connection = (HttpURLConnection) cancionUrl.openConnection();
                connection.setRequestProperty("Authorization", bearer);
                connection.setRequestMethod("GET");
                connection.connect();

                File archivoTemp = File.createTempFile("cancion_temp", ".mp3", contexto.getCacheDir());
                archivoTemp.deleteOnExit();

                try (InputStream input = connection.getInputStream();
                     FileOutputStream output = new FileOutputStream(archivoTemp)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(archivoTemp.getAbsolutePath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVolume(volumenActual, volumenActual);

                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    if (listener != null) listener.onReproduccionIniciada();
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    if (listener != null) listener.onReproductorPausadoODetenido();

                    if (indiceActual + 1 < listaCanciones.size()) {
                        indiceActual++;
                        reproducirCancion(listaCanciones, indiceActual);
                    }
                });

                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    public static boolean estaReproduciendo() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static int getDuracion() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public static int getPosicion() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public static void seekTo(int milisegundos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(milisegundos);
        }
    }

    public static int getIndiceActual() {
        return indiceActual;
    }

    public static List<BusquedaCancionDTO> getListaCanciones() {
        return listaCanciones;
    }

    public static void removerListener(ReproductorListener l) {
        if (listener == l) {
            listener = null;
        }
    }



}

