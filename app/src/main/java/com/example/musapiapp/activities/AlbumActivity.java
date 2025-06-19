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
import com.example.musapiapp.network.ApiCliente;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    public static final String EXTRA_ALBUM = "EXTRA_ALBUM";
    String baseUrl = ApiCliente.getUrlArchivos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        ImageButton  btnVolver     = findViewById(R.id.btnVolver);
        ImageView    ivPortada     = findViewById(R.id.ivPortada);
        TextView     tvNombreAlbum = findViewById(R.id.tvNombreAlbum);
        TextView     tvArtista     = findViewById(R.id.tvArtista);
        TextView     tvFecha       = findViewById(R.id.tvFecha);
        TextView     tvDuracion    = findViewById(R.id.tvDuracion);
        Button       btnSeguir     = findViewById(R.id.btnSeguir);
        LinearLayout llCanciones   = findViewById(R.id.llCanciones);

        btnVolver.setOnClickListener(v -> onBackPressed());

        BusquedaAlbumDTO album = (BusquedaAlbumDTO)
                getIntent().getSerializableExtra(EXTRA_ALBUM);

        if (album != null) {
            tvNombreAlbum.setText(album.getNombreAlbum());
            tvArtista    .setText(album.getNombreArtista());
            tvFecha      .setText(album.getFechaPublicacion());
            Glide.with(this)
                    .load(baseUrl + album.getUrlFoto())
                    .into(ivPortada);

            // Inflar canciones
            List<BusquedaCancionDTO> songs = album.getCanciones();
            if (songs != null) {
                long totalSeg = 0;
                llCanciones.removeAllViews();
                for (BusquedaCancionDTO c : songs) {
                    View item = LayoutInflater.from(this)
                            .inflate(R.layout.item_contenido, llCanciones, false);
                    TextView    tvN = item.findViewById(R.id.tvNombre);
                    TextView    tvA = item.findViewById(R.id.tvAutor);
                    ImageView   iv = item.findViewById(R.id.imgFoto);
                    Button      bd = item.findViewById(R.id.btnVerDetalles);
                    ImageButton bp = item.findViewById(R.id.btnReproducir);

                    tvN.setText(c.getNombre());
                    tvA.setText(c.getNombreArtista());
                    Glide.with(this)
                            .load(baseUrl + c.getUrlFoto())
                            .into(iv);

                    bd.setOnClickListener(v -> {
                        Intent i = new Intent(this, CancionActivity.class);
                        i.putExtra(CancionActivity.EXTRA_CANCION, c);
                        startActivity(i);
                    });
                    // TODO: bp.setOnClickListener → reproducir fichero

                    llCanciones.addView(item);

                    // sumar duración
                    String[] p = c.getDuracion().split(":");
                    long seg = Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
                    totalSeg += seg;
                }
                long h = totalSeg / 3600,
                        m = (totalSeg % 3600) / 60,
                        s = totalSeg % 60;
                tvDuracion.setText(String.format("Duración total %02d:%02d:%02d", h, m, s));
            }

            btnSeguir.setOnClickListener(v -> {
                // TODO: lógica de seguir artista
            });
        }
    }
}
