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
    private static final int REQ_EDITAR_PERFIL = 1001;
    private ImageButton btnVolver;
    private TextView tvNombre, tvUsuario;
    private Button btnCrearPerfilArtista, btnVerPerfilArtista,
            btnVerEstadisticas, btnEditarPerfil;
    private LinearLayout llListas;
    private LinearLayout llAcciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        llAcciones = findViewById(R.id.ll_acciones);
        // Referencias
        btnVolver             = findViewById(R.id.btn_volver);
        tvNombre              = findViewById(R.id.tv_nombre);
        tvUsuario             = findViewById(R.id.tv_usuario);
        btnCrearPerfilArtista = findViewById(R.id.btn_crear_perfil_artista);
        btnVerPerfilArtista   = findViewById(R.id.btn_ver_perfil_artista);
        btnVerEstadisticas    = findViewById(R.id.btn_ver_estadisticas);
        btnEditarPerfil       = findViewById(R.id.btn_editar_perfil);
        llListas              = findViewById(R.id.ll_listas);

        // Botón volver
        btnVolver.setOnClickListener(v -> onBackPressed());

        // Recuperar UsuarioDTO de SharedPreferences
        String jsonUsuario = Preferencias.recuperarUsuarioJson(this);
        if (jsonUsuario != null) {
            UsuarioDTO u = new Gson().fromJson(jsonUsuario, UsuarioDTO.class);
            // Llenar datos
            tvNombre.setText(u.getNombre());
            tvUsuario.setText("@" + u.getNombreUsuario());

            // Mostrar/ocultar acciones
            if (u.isEsArtista()) {
                btnVerPerfilArtista.setVisibility(View.VISIBLE);
                btnCrearPerfilArtista.setVisibility(View.GONE);
            } else {
                btnCrearPerfilArtista.setVisibility(View.VISIBLE);
                btnVerPerfilArtista.setVisibility(View.GONE);
            }
            llAcciones.post(() -> {
                llAcciones.requestLayout();
                llAcciones.invalidate();
            });        }

        // Listeners de acciones
        btnCrearPerfilArtista.setOnClickListener(v ->
                startActivity(new Intent(this, CrearPerfilArtistaActivity.class))
        );
        btnVerPerfilArtista.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilArtistaActivity.class))
        );
        btnVerEstadisticas.setOnClickListener(v -> {
            // TODO: implementar
        });
        btnEditarPerfil.setOnClickListener(v -> {
            Intent i = new Intent(this, EditarPerfilActivity.class);
            startActivityForResult(i, REQ_EDITAR_PERFIL);
        });
        recargarUsuario();
        // TODO: llenar llListas con las listas del usuario
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDITAR_PERFIL && resultCode == RESULT_OK) {
            recargarUsuario();
        }
    }

    private void recargarUsuario() {
        String jsonUsuario = Preferencias.recuperarUsuarioJson(this);
        if (jsonUsuario != null) {
            UsuarioDTO u = new Gson().fromJson(jsonUsuario, UsuarioDTO.class);

            tvNombre.setText(u.getNombre());
            tvUsuario.setText("@" + u.getNombreUsuario());

            // refresca botones
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

    @Override
    protected void onResume() {
        super.onResume();
        recargarUsuario();
    }

}
