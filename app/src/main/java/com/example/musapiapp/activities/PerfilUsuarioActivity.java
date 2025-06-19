// src/main/java/com/example/musapiapp/activities/PerfilUsuarioActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

public class PerfilUsuarioActivity extends AppCompatActivity {
    private static final int REQ_EDITAR_PERFIL     = 1001;
    private static final int REQ_CREAR_ARTISTA     = 1002; // nueva constante
    public  static final String EXTRA_ID_ARTISTA   = "EXTRA_ID_ARTISTA";

    private ImageButton btnVolver;
    private TextView    tvNombre, tvUsuario;
    private Button      btnCrearPerfilArtista,
            btnVerPerfilArtista,
            btnVerEstadisticas,
            btnEditarPerfil;
    private LinearLayout llAcciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        llAcciones           = findViewById(R.id.ll_acciones);
        btnVolver            = findViewById(R.id.btn_volver);
        tvNombre             = findViewById(R.id.tv_nombre);
        tvUsuario            = findViewById(R.id.tv_usuario);
        btnCrearPerfilArtista= findViewById(R.id.btn_crear_perfil_artista);
        btnVerPerfilArtista  = findViewById(R.id.btn_ver_perfil_artista);
        btnVerEstadisticas   = findViewById(R.id.btn_ver_estadisticas);
        btnEditarPerfil      = findViewById(R.id.btn_editar_perfil);

        btnVolver.setOnClickListener(v -> onBackPressed());

        // Acción CREAR perfil artista con callback
        btnCrearPerfilArtista.setOnClickListener(v -> {
            Intent i = new Intent(this, CrearPerfilArtistaActivity.class);
            startActivityForResult(i, REQ_CREAR_ARTISTA);
        });

        // Acción VER perfil artista, enviamos el ID desde el UsuarioDTO
        btnVerPerfilArtista.setOnClickListener(v -> {
            String json = Preferencias.recuperarUsuarioJson(this);
            if (json != null) {
                UsuarioDTO u = new Gson().fromJson(json, UsuarioDTO.class);
                Intent i = new Intent(this, PerfilArtistaActivity.class);
                i.putExtra(EXTRA_ID_ARTISTA, u.getIdUsuario());
                startActivity(i);
            }
        });

        btnVerEstadisticas.setOnClickListener(v -> {
            // TODO: implementar
        });
        btnEditarPerfil.setOnClickListener(v -> {
            Intent i = new Intent(this, EditarPerfilActivity.class);
            startActivityForResult(i, REQ_EDITAR_PERFIL);
        });

        recargarUsuario();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si acabamos de crear perfil de artista, recargamos para actualizar botones
        if (requestCode == REQ_CREAR_ARTISTA && resultCode == RESULT_OK) {
            recargarUsuario();
        }

        // Si editamos datos de usuario
        if (requestCode == REQ_EDITAR_PERFIL && resultCode == RESULT_OK) {
            recargarUsuario();
        }
    }

    private void recargarUsuario() {
        String jsonUsuario = Preferencias.recuperarUsuarioJson(this);
        if (jsonUsuario != null) {
            UsuarioDTO u = new Gson().fromJson(jsonUsuario, UsuarioDTO.class);
            tvNombre .setText(u.getNombre());
            tvUsuario.setText("@" + u.getNombreUsuario());

            if (u.isEsArtista()) {
                btnVerPerfilArtista.setVisibility(View.VISIBLE);
                btnCrearPerfilArtista.setVisibility(View.GONE);
            } else {
                btnCrearPerfilArtista.setVisibility(View.VISIBLE);
                btnVerPerfilArtista.setVisibility(View.GONE);
            }
            llAcciones.requestLayout();
        }
    }
}
