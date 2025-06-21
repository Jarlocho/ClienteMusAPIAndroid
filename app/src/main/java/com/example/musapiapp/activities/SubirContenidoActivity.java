// src/main/java/com/example/musapiapp/activities/SubirContenidoActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.InfoAlbumDTO;

public class SubirContenidoActivity extends AppCompatActivity {
    public static final String EXTRA_ID_ARTISTA = "EXTRA_ID_ARTISTA";
    private static final int REQ_CREAR_ALBUM = 2001;


    private int idArtista;
    private Button btnSubirCancion;
    private Button btnCrearAlbum;


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

        btnCrearAlbum = findViewById(R.id.btnCrearAlbum);

        btnCrearAlbum.setOnClickListener(v -> {
            Intent intent = new Intent(SubirContenidoActivity.this, CrearAlbumActivity.class);
            intent.putExtra(EXTRA_ID_ARTISTA, idArtista);
            startActivityForResult(intent, REQ_CREAR_ALBUM);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CREAR_ALBUM && resultCode == RESULT_OK && data != null) {
            InfoAlbumDTO album = (InfoAlbumDTO) data.getSerializableExtra(AlbumActivity.EXTRA_ALBUM_PENDIENTE);
            if (album != null) {
                Intent i = new Intent(this, AlbumActivity.class);
                i.putExtra(AlbumActivity.EXTRA_ALBUM_PENDIENTE, album);
                startActivity(i);
            }
        }
    }


}
