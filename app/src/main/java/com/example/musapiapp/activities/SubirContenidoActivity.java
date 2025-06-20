// src/main/java/com/example/musapiapp/activities/SubirContenidoActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;

public class SubirContenidoActivity extends AppCompatActivity {
    public static final String EXTRA_ID_ARTISTA = "EXTRA_ID_ARTISTA";

    private int idArtista;
    private Button btnSubirCancion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_contenido);

        // Leer ID de artista
        idArtista = getIntent().getIntExtra(EXTRA_ID_ARTISTA, -1);
        if (idArtista < 0) {
            Toast.makeText(this, "Artista no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind del botón Subir Canción
        btnSubirCancion = findViewById(R.id.btnSubirCancion);

        // Acción del botón: navegar a SubirCancionActivity
        btnSubirCancion.setOnClickListener(v -> {
            Intent intent = new Intent(SubirContenidoActivity.this, SubirCancionActivity.class);
            intent.putExtra(SubirContenidoActivity.EXTRA_ID_ARTISTA, idArtista);
            startActivity(intent);
        });
    }
}
