package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;

public class MenuPrincipalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        findViewById(R.id.btnMenuAdmin).setOnClickListener(v ->
                startActivity(new Intent(this, MenuAdminActivity.class))
        );

        findViewById(R.id.btnBuscar).setOnClickListener(v ->
                startActivity(new Intent(this, BusquedaActivity.class))
        );

        findViewById(R.id.btnPerfilArtista).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilArtistaActivity.class))
        );

        findViewById(R.id.btnPerfilUsuario).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilUsuarioActivity.class))
        );

        findViewById(R.id.btnCrearAlbum).setOnClickListener(v ->
                startActivity(new Intent(this, CrearAlbumActivity.class))
        );

        findViewById(R.id.btnCrearLista).setOnClickListener(v ->
                startActivity(new Intent(this, CrearListaDeReproduccionActivity.class))
        );

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            // borra token si usas preferencias...
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        });

        findViewById(R.id.btnSalirApp).setOnClickListener(v ->
                finishAffinity()
        );


    }



}
