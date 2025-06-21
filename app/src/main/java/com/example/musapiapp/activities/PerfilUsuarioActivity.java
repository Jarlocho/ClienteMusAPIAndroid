// src/main/java/com/example/musapiapp/activities/PerfilUsuarioActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapter.ContentAdapter;
import com.example.musapiapp.dto.ListaReproduccionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.model.ContentItem;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioListasDeReproduccion;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilUsuarioActivity extends BaseActivity {
    private static final int REQ_EDITAR_PERFIL = 1001;
    private static final int REQ_CREAR_ARTISTA = 1002;
    public static final String EXTRA_ID_ARTISTA = "EXTRA_ID_ARTISTA";

    private ImageButton btnVolver;
    private TextView tvNombre, tvUsuario;
    private Button btnCrearPerfilArtista, btnVerPerfilArtista, btnVerEstadisticas, btnEditarPerfil;
    private RecyclerView rvListasUsuario;
    private ContentAdapter adapter;
    private List<ContentItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PerfilUsuario", "onCreate: antes de setContentView");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        Log.d("PerfilUsuario", "onCreate: después de setContentView");


        // --- findViewById ---
        btnVolver             = findViewById(R.id.btn_volver);
        tvNombre              = findViewById(R.id.tv_nombre);
        tvUsuario             = findViewById(R.id.tv_usuario);
        btnCrearPerfilArtista = findViewById(R.id.btn_crear_perfil_artista);
        btnVerPerfilArtista   = findViewById(R.id.btn_ver_perfil_artista);
        btnVerEstadisticas    = findViewById(R.id.btn_ver_estadisticas);
        btnEditarPerfil       = findViewById(R.id.btn_editar_perfil);
        rvListasUsuario       = findViewById(R.id.rvListasUsuario);

        // Acción volver
        btnVolver.setOnClickListener(v -> onBackPressed());

        // Crear perfil artista
        btnCrearPerfilArtista.setOnClickListener(v -> {
            UsuarioDTO u = new Gson().fromJson(
                    Preferencias.recuperarUsuarioJson(this),
                    UsuarioDTO.class
            );
            int usuarioId = u.getIdUsuario();
            Log.d("PerfilUsuario", "Click CrearPerfilArtista, userId=" + usuarioId);

            Intent i = new Intent(this, CrearPerfilArtistaActivity.class);
            // Usa tu propia constante de clave (puedes definir EXTRA_ID_USUARIO o así)
            i.putExtra(EXTRA_ID_ARTISTA, usuarioId);
            startActivityForResult(i, REQ_CREAR_ARTISTA);
        });



        // Ver perfil artista
        btnVerPerfilArtista.setOnClickListener(v -> {
            UsuarioDTO u = new Gson().fromJson(
                    Preferencias.recuperarUsuarioJson(this),
                    UsuarioDTO.class
            );
            Intent i = new Intent(this, PerfilArtistaActivity.class);
            i.putExtra(EXTRA_ID_ARTISTA, u.getIdUsuario());
            startActivity(i);
        });

        // Editar perfil
        btnEditarPerfil.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(this, EditarPerfilActivity.class),
                        REQ_EDITAR_PERFIL
                )
        );

        // --- Inicializa RecyclerView para listas de usuario ---
        rvListasUsuario.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        adapter = new ContentAdapter(items, this, new ContentAdapter.Listener() {
            @Override
            public void onDetails(ContentItem item) {
                ListaReproduccionDTO lst = (ListaReproduccionDTO) item.getDto();
                Intent i = new Intent(PerfilUsuarioActivity.this,
                        ListaDeReproduccionActivity.class);
                i.putExtra(ListaDeReproduccionActivity.EXTRA_LISTA, lst);
                startActivity(i);
            }
            @Override public void onPlay(ContentItem item) {
                // no aplica
            }
            @Override public void onSave(ContentItem item) {
                // no aplica
            }
        });
        rvListasUsuario.setAdapter(adapter);

        recargarUsuario();
        cargarListasUsuario();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQ_CREAR_ARTISTA || requestCode == REQ_EDITAR_PERFIL)
                && resultCode == RESULT_OK) {
            recargarUsuario();
        }
    }

    private void recargarUsuario() {
        UsuarioDTO u = new Gson().fromJson(
                Preferencias.recuperarUsuarioJson(this),
                UsuarioDTO.class
        );
        tvNombre .setText(u.getNombre());
        tvUsuario.setText("@" + u.getNombreUsuario());

        btnVerPerfilArtista.setVisibility(u.isEsArtista()  ? View.VISIBLE : View.GONE);
        btnCrearPerfilArtista.setVisibility(u.isEsArtista() ? View.GONE   : View.VISIBLE);
    }

    private void cargarListasUsuario() {
        UsuarioDTO u = new Gson().fromJson(
                Preferencias.recuperarUsuarioJson(this),
                UsuarioDTO.class
        );
        ServicioListasDeReproduccion svc = ApiCliente
                .getClient(this)
                .create(ServicioListasDeReproduccion.class);

        svc.getListasUsuario(u.getIdUsuario())
                .enqueue(new Callback<RespuestaCliente<List<ListaReproduccionDTO>>>() {
                    @Override
                    public void onResponse(
                            Call<RespuestaCliente<List<ListaReproduccionDTO>>> call,
                            Response<RespuestaCliente<List<ListaReproduccionDTO>>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            items.clear();
                            for (ListaReproduccionDTO lst : resp.body().getDatos()) {
                                items.add(new ContentItem(
                                        ContentItem.Type.LISTA,
                                        lst,
                                        false
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(
                            Call<RespuestaCliente<List<ListaReproduccionDTO>>> call,
                            Throwable t
                    ) {
                        Toast.makeText(PerfilUsuarioActivity.this,
                                "Error al cargar listas: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
