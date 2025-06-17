package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.ListaReproduccionDTO;

import java.util.Arrays;
import java.util.List;

public class MenuPrincipalActivity extends AppCompatActivity {
    private ImageButton btnCerrarSesion, btnBuscar, btnMenuAdmin, btnPerfilUsuario, btnCrearLista;
    private EditText etBusqueda;
    private LinearLayout llAlbumes, llListas, llArtistas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        btnCerrarSesion  = findViewById(R.id.btn_cerrar_sesion);
        btnBuscar        = findViewById(R.id.btn_buscar);
        btnMenuAdmin     = findViewById(R.id.btn_menu_admin);
        btnPerfilUsuario = findViewById(R.id.btn_perfil_usuario);
        btnCrearLista    = findViewById(R.id.btn_crear_lista);
        etBusqueda       = findViewById(R.id.et_busqueda);
        llAlbumes        = findViewById(R.id.ll_albumes);
        llListas         = findViewById(R.id.ll_listas);
        llArtistas       = findViewById(R.id.ll_artistas);

        // Navegación estática
        btnCerrarSesion.setOnClickListener(v -> {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        });
        btnBuscar.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusquedaActivity.class);
            intent.putExtra("QUERY", etBusqueda.getText().toString());
            startActivity(intent);
        });
        btnMenuAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, MenuAdminActivity.class))
        );
        btnPerfilUsuario.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilUsuarioActivity.class))
        );
        btnCrearLista.setOnClickListener(v ->
                startActivity(new Intent(this, CrearListaDeReproduccionActivity.class))
        );

        // Datos de prueba (luego reemplaza por tu llamada real a la API)
        List<BusquedaAlbumDTO> albumes = Arrays.asList(
                new BusquedaAlbumDTO(1, "Trench", "Twenty One Pilots", "2021-04-16", "/fotos/trench.jpg", null),
                new BusquedaAlbumDTO(2, "Blurryface", "Twenty One Pilots", "2015-05-12", "/fotos/blurryface.jpg", null)
        );
        List<ListaReproduccionDTO> listas = Arrays.asList(
                new ListaReproduccionDTO("Favoritas", "Mis top tracks", 123, "/fotos/favoritas.jpg"),
                new ListaReproduccionDTO("Reciente",  "Reproducciones recientes", 123, "/fotos/reciente.jpg")
        );
        List<BusquedaArtistaDTO> artistas = Arrays.asList(
                new BusquedaArtistaDTO(1, "Muse",            "muse_oficial",   "Desc", "/fotos/muse.jpg",   null),
                new BusquedaArtistaDTO(2, "Imagine Dragons", "imagine_dragons","Desc", "/fotos/id.jpg",     null)
        );

        // Inflar sección Álbumes
        for (BusquedaAlbumDTO alb : albumes) {
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.item_contenido, llAlbumes, false);

            ImageView ivFoto    = item.findViewById(R.id.imgFoto);
            TextView tvNombre   = item.findViewById(R.id.tvNombre);
            TextView tvAutor    = item.findViewById(R.id.tvAutor);
            Button btnGuardar   = item.findViewById(R.id.btnGuardar);
            Button btnDetalles  = item.findViewById(R.id.btnVerDetalles);
            ImageButton btnPlay = item.findViewById(R.id.btnReproducir);

            tvNombre.setText(alb.getNombreAlbum());
            tvAutor.setText(alb.getNombreArtista());
            Glide.with(this)
                    .load("https://api.tuapp.com" + alb.getUrlFoto())
                    .into(ivFoto);

            btnGuardar.setVisibility(View.GONE);
            btnDetalles.setOnClickListener(v -> {
                Intent intent = new Intent(this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.EXTRA_ALBUM, alb); // pasamos alb, no AlbumDTO
                startActivity(intent);
            });
            btnPlay.setOnClickListener(v -> {
                // TODO: lógica de reproducción
            });

            llAlbumes.addView(item);
        }

        // Inflar sección Listas
        for (ListaReproduccionDTO lst : listas) {
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.item_contenido, llListas, false);

            TextView tvNombre  = item.findViewById(R.id.tvNombre);
            TextView tvAutor   = item.findViewById(R.id.tvAutor);
            Button btnGuardar  = item.findViewById(R.id.btnGuardar);
            Button btnDetalles = item.findViewById(R.id.btnVerDetalles);
            ImageButton btnPlay= item.findViewById(R.id.btnReproducir);

            tvNombre.setText(lst.getNombre());
            tvAutor.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);

            btnDetalles.setOnClickListener(v -> {
                // TODO: navegar a pantalla de lista
            });

            llListas.addView(item);
        }

        // Inflar sección Artistas
        for (BusquedaArtistaDTO art : artistas) {
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.item_contenido, llArtistas, false);

            TextView tvNombre  = item.findViewById(R.id.tvNombre);
            TextView tvAutor   = item.findViewById(R.id.tvAutor);
            Button btnGuardar  = item.findViewById(R.id.btnGuardar);
            ImageButton btnPlay= item.findViewById(R.id.btnReproducir);

            tvNombre.setText(art.getNombre());
            tvAutor.setText("@" + art.getNombreUsuario());
            btnPlay.setVisibility(View.GONE);
            btnGuardar.setText("Seguir");
            btnGuardar.setOnClickListener(v -> {
                // TODO: lógica de seguir
            });

            llArtistas.addView(item);
        }
    }
}
