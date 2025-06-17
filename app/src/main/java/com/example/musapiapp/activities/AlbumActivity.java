package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    public static final String EXTRA_ALBUM = "EXTRA_ALBUM";

    private ImageButton btnVolver;
    private ImageView ivPortada;
    private TextView tvNombreAlbum, tvArtista, tvFecha, tvDuracion;
    private Button btnSeguir, btnPublicar;
    private LinearLayout llCanciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        btnVolver     = findViewById(R.id.btnVolver);
        ivPortada     = findViewById(R.id.ivPortada);
        tvNombreAlbum = findViewById(R.id.tvNombreAlbum);
        tvArtista     = findViewById(R.id.tvArtista);
        tvFecha       = findViewById(R.id.tvFecha);
        tvDuracion    = findViewById(R.id.tvDuracion);
        btnSeguir     = findViewById(R.id.btnSeguir);
        btnPublicar   = findViewById(R.id.btnPublicar);
        llCanciones   = findViewById(R.id.llCanciones);

        btnVolver.setOnClickListener(v -> onBackPressed());

        // Recuperar y mostrar álbum
        BusquedaAlbumDTO album = (BusquedaAlbumDTO) getIntent()
                .getSerializableExtra(EXTRA_ALBUM);

        tvNombreAlbum.setText(album.getNombreAlbum());
        tvArtista.setText("Artista: " + album.getNombreArtista());
        tvFecha.setText("Fecha: " + album.getFechaPublicacion());

        Glide.with(this)
                .load("https://tu.api.baseurl.com" + album.getUrlFoto())
                .into(ivPortada);

        // Mostrar/ocultar publicar si ya es público
        boolean esPublico = true; // o tu lógica
        btnPublicar.setVisibility(esPublico ? View.GONE : View.VISIBLE);

        btnPublicar.setOnClickListener(v -> {
            // TODO: publicar álbum
        });
        btnSeguir.setOnClickListener(v -> {
            // TODO: seguir artista/álbum
        });

        // Listado de canciones
        List<BusquedaCancionDTO> canciones = album.getCanciones();
        if (canciones != null) {
            llCanciones.removeAllViews();
            for (BusquedaCancionDTO c : canciones) {
                View item = LayoutInflater.from(this)
                        .inflate(R.layout.item_contenido, llCanciones, false);

                ImageView ivFotoCan  = item.findViewById(R.id.imgFoto);
                TextView tvNombreCan = item.findViewById(R.id.tvNombre);
                TextView tvAutorCan  = item.findViewById(R.id.tvAutor);
                Button btnVerDet      = item.findViewById(R.id.btnVerDetalles);
                ImageButton btnPlayCan= item.findViewById(R.id.btnReproducir);
                Button btnSaveCan     = item.findViewById(R.id.btnGuardar);

                tvNombreCan.setText(c.getNombre());
                tvAutorCan.setText(c.getNombreArtista());
                Glide.with(this)
                        .load("https://tu.api.baseurl.com" + c.getUrlFoto())
                        .into(ivFotoCan);

                btnSaveCan.setVisibility(View.GONE);
                btnVerDet.setOnClickListener(v -> {
                    Intent i = new Intent(this, CancionDetallesActivity.class);
                    i.putExtra(CancionDetallesActivity.EXTRA_CANCION, c);
                    startActivity(i);
                });
                btnPlayCan.setOnClickListener(v -> {
                    // TODO: reproducir c.getUrlArchivo()
                });

                llCanciones.addView(item);
            }

            // Calcular duración total
            long totalSeg = canciones.stream()
                    .mapToLong(c -> {
                        String[] p = c.getDuracion().split(":");
                        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
                    })
                    .sum();
            long h = totalSeg / 3600, m = (totalSeg % 3600) / 60, s = totalSeg % 60;
            tvDuracion.setText(String.format("Duración: %02d:%02d:%02d", h, m, s));
        }
    }
}
