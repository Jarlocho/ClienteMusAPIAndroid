package com.example.musapiapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.Preferencias;
import com.example.musapiapp.util.Reproductor;

public class ReproductorActivity extends AppCompatActivity implements Reproductor.ReproductorListener {
    ImageButton btnPlayPause, btnAnterior, btnSiguiente;
    SeekBar seekBar;
    TextView txtCancion, txtArtista;
    ImageView imgPortada;
    TextView txtPosicionAlbum;
    TextView txtTiempoActual, txtDuracionTotal;


    private Handler handler = new Handler();
    private Runnable actualizarSeekBarRunnable;
    private boolean usuarioMoviendoSeekBar = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        Reproductor.removerListener(this); // por si hay alguno viejo
        Reproductor.inicializar(this, this);   // asigna esta nueva instancia

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        seekBar = findViewById(R.id.seekBar);
        txtCancion = findViewById(R.id.txtCancion);
        txtArtista = findViewById(R.id.txtArtista);
        imgPortada = findViewById(R.id.imgPortada);
        txtTiempoActual = findViewById(R.id.txtTiempoActual);
        txtDuracionTotal = findViewById(R.id.txtDuracionTotal);
        txtPosicionAlbum = findViewById(R.id.txtPosicionAlbum);

        refrescarUI();

        if (Reproductor.estaReproduciendo()) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }

        BusquedaCancionDTO cancion = Reproductor.getCancionActual();
        if (cancion != null) {
            txtCancion.setText(cancion.getNombre());
            txtArtista.setText(cancion.getNombreArtista());

            // Si tienes una función para cargar imagen:
            cargarImagen(cancion.getUrlFoto(), imgPortada);
        }

        // Listener para cambios en el estado del reproductor
        Reproductor.inicializar(this, this);

        btnPlayPause.setOnClickListener(v -> Reproductor.pausarReanudar());
        btnAnterior.setOnClickListener(v -> Reproductor.cancionAnterior());
        btnSiguiente.setOnClickListener(v -> Reproductor.siguienteCancion());

        // 🎯 AQUÍ va el listener del SeekBar:
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Reproductor.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                usuarioMoviendoSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                usuarioMoviendoSeekBar = false;
            }
        });

        // 🕒 Y aquí empieza la actualización automática del progreso:
        actualizarSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (Reproductor.estaReproduciendo() && !usuarioMoviendoSeekBar) {
                    int posicion = Reproductor.getPosicion();
                    int duracion = Reproductor.getDuracion();
                    seekBar.setMax(duracion);
                    seekBar.setProgress(posicion);
                    txtTiempoActual.setText(formatearTiempo(posicion));
                    txtDuracionTotal.setText(formatearTiempo(duracion));

                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(actualizarSeekBarRunnable);
    }

    private String formatearTiempo(int milisegundos) {
        int minutos = (milisegundos / 1000) / 60;
        int segundos = (milisegundos / 1000) % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }


    @Override
    public void onReproduccionIniciada() {
        btnPlayPause.setImageResource(R.drawable.ic_pause);
        BusquedaCancionDTO cancion = Reproductor.getCancionActual();
        if (cancion != null) {
            txtCancion.setText(cancion.getNombre());
            txtArtista.setText(cancion.getNombreArtista());
            cargarImagen(cancion.getUrlFoto(), imgPortada);

            // Mostrar posición actual
            int actual = Reproductor.getIndiceActual() + 1;
            int total = Reproductor.getListaCanciones().size();
            txtPosicionAlbum.setText(actual + " / " + total);
        }
    }

    @Override
    public void onReproductorPausadoODetenido() {
        btnPlayPause.setImageResource(R.drawable.ic_play);
    }

    private void cargarImagen(String url, ImageView destino) {
        if (url == null || url.isEmpty()) return;

        String token = Preferencias.obtenerToken(this); // O contexto si estás fuera de actividad
        String bearer = token != null ? "Bearer " + token : "";

        GlideUrl glideUrl = new GlideUrl(
                Constantes.URL_BASE + url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", bearer)
                        .build()
        );

        Glide.with(this)
                .load(glideUrl)
                .into(destino);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Reproductor.removerListener(this); // <- lo implementas tú
    }

    private void refrescarUI() {
        BusquedaCancionDTO cancion = Reproductor.getCancionActual();
        if (cancion != null) {
            txtCancion.setText(cancion.getNombre());
            txtArtista.setText(cancion.getNombreArtista());
            cargarImagen(cancion.getUrlFoto(), imgPortada);

            int actual = Reproductor.getIndiceActual() + 1;
            int total = Reproductor.getListaCanciones().size();
            txtPosicionAlbum.setText(actual + " / " + total);
        }

        if (Reproductor.estaReproduciendo()) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }

        int duracion = Reproductor.getDuracion();
        int posicion = Reproductor.getPosicion();
        seekBar.setMax(duracion);
        seekBar.setProgress(posicion);
        txtDuracionTotal.setText(formatearTiempo(duracion));
        txtTiempoActual.setText(formatearTiempo(posicion));
    }



}
