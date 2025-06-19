package com.example.musapiapp.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.util.Reproductor;

public abstract class BaseActivity extends AppCompatActivity {
    protected LinearLayout barraReproductor;
    protected TextView txtMiniTitulo;
    protected ImageButton btnMiniPlay;

    @Override
    protected void onStart() {
        super.onStart();
        setupBarraReproductor();
    }

    private void setupBarraReproductor() {
        barraReproductor = findViewById(R.id.barraReproductor);
        txtMiniTitulo = findViewById(R.id.txtMiniTitulo);
        btnMiniPlay = findViewById(R.id.btnMiniPlayPause);

        if (barraReproductor == null || txtMiniTitulo == null || btnMiniPlay == null) {
            // La barra no existe en este layout
            return;
        }

        BusquedaCancionDTO actual = Reproductor.getCancionActual();
        if (actual != null) {
            txtMiniTitulo.setText(actual.getNombre());
            btnMiniPlay.setImageResource(Reproductor.estaReproduciendo()
                    ? R.drawable.ic_pause : R.drawable.ic_play);

            barraReproductor.setOnClickListener(v -> {
                startActivity(new Intent(this, ReproductorActivity.class));
            });

            btnMiniPlay.setOnClickListener(v -> {
                Reproductor.pausarReanudar();
                btnMiniPlay.setImageResource(Reproductor.estaReproduciendo()
                        ? R.drawable.ic_pause : R.drawable.ic_play);
            });

            barraReproductor.setVisibility(View.VISIBLE);
        } else {
            barraReproductor.setVisibility(View.GONE);
        }
    }
}
