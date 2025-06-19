package com.example.musapiapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.network.ApiCliente;

public class PerfilArtistaActivity extends AppCompatActivity {
    public static final String EXTRA_ARTISTA = "EXTRA_ARTISTA";

    private ImageView ivFotoArtista;
    private TextView  tvNombreArtista;
    private TextView  tvHandleArtista;
    private TextView  tvDescripcionArtista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_artista);

        // Referencias a las vistas
        ivFotoArtista        = findViewById(R.id.ivFotoArtista);
        tvNombreArtista      = findViewById(R.id.tvNombreArtista);
        tvHandleArtista      = findViewById(R.id.tvHandleArtista);
        tvDescripcionArtista = findViewById(R.id.tvDescripcionArtista);

        // Recuperamos el DTO (asegúrate de que BusquedaArtistaDTO implemente Serializable)
        BusquedaArtistaDTO dto = (BusquedaArtistaDTO) getIntent()
                .getSerializableExtra(EXTRA_ARTISTA);
        if (dto != null) {
            tvNombreArtista     .setText(dto.getNombre());
            tvHandleArtista     .setText("@" + dto.getNombreUsuario());
            tvDescripcionArtista.setText(dto.getDescripcion());

            // Cargamos la imagen con Glide (ajusta la URL base si es necesario)
            Glide.with(this)
                    .load(ApiCliente.getUrlArchivos() + dto.getUrlFoto())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivFotoArtista);
        }
    }
}
