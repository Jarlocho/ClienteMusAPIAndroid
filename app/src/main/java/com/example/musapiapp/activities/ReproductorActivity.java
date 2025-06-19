package com.example.musapiapp.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.util.Reproductor;

public class ReproductorActivity extends AppCompatActivity implements Reproductor.ReproductorListener {
    ImageButton btnPlayPause, btnAnterior, btnSiguiente;
    SeekBar seekBar;
    TextView txtCancion, txtArtista;
    ImageView imgPortada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        seekBar = findViewById(R.id.seekBar);
        txtCancion = findViewById(R.id.txtCancion);
        txtArtista = findViewById(R.id.txtArtista);
        imgPortada = findViewById(R.id.imgPortada);

        Reproductor.inicializar(this, new Reproductor.ReproductorListener() {
            @Override
            public void onReproduccionIniciada() {
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }

            @Override
            public void onReproductorPausadoODetenido() {
                btnPlayPause.setImageResource(R.drawable.ic_play);
            }
        });

        btnPlayPause.setOnClickListener(v -> Reproductor.pausarReanudar());
        btnAnterior.setOnClickListener(v -> Reproductor.cancionAnterior());
        btnSiguiente.setOnClickListener(v -> Reproductor.siguienteCancion());
    }

    @Override
    public void onReproduccionIniciada() {
        btnPlayPause.setImageResource(R.drawable.ic_pause);
    }

    @Override
    public void onReproductorPausadoODetenido() {
        btnPlayPause.setImageResource(R.drawable.ic_play);
    }


}
