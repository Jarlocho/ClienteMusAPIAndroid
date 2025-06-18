package com.example.musapiapp.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaCancionDTO;

public class CancionActivity extends AppCompatActivity {
    public static final String EXTRA_CANCION = "extra_cancion";

    private TextView tvNombreCancion;
    private TextView tvArtistaCancion;
    private TextView tvDuracionCancion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancion);

        // Referencias a las vistas
        tvNombreCancion   = findViewById(R.id.tvNombreCancion);
        tvArtistaCancion  = findViewById(R.id.tvArtistaCancion);
        tvDuracionCancion = findViewById(R.id.tvDuracionCancion);

        // Recuperamos el DTO (asegúrate de que BusquedaCancionDTO implemente Serializable)
        BusquedaCancionDTO dto = (BusquedaCancionDTO) getIntent()
                .getSerializableExtra(EXTRA_CANCION);
        if (dto != null) {
            tvNombreCancion  .setText(dto.getNombre());
            tvArtistaCancion .setText(dto.getNombreArtista());
            tvDuracionCancion.setText(dto.getDuracion());
        }
    }
}
