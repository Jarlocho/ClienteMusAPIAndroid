// src/main/java/com/example/musapiapp/activities/BusquedaActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapter.ContentAdapter;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.model.ContentItem;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioBusqueda;
import com.example.musapiapp.util.Preferencias;
import com.example.musapiapp.util.Reproductor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusquedaActivity extends BaseActivity {
    private EditText etBusqueda;
    private Spinner spinnerTipo;
    private RecyclerView rvResultados;
    private ServicioBusqueda srv;

    // Adapter y lista de items
    private List<ContentItem> items = new ArrayList<>();
    private ContentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        // --- Volver atrás ---
        ImageButton btnVolver = findViewById(R.id.btn_volver_busqueda);
        btnVolver.setOnClickListener(v -> onBackPressed());

        // --- Perfil usuario ---
        ImageButton btnPerfilUsuario = findViewById(R.id.btn_perfil_usuario);
        btnPerfilUsuario.setOnClickListener(v ->
                startActivity(new Intent(BusquedaActivity.this, PerfilUsuarioActivity.class))
        );

        // --- findViewById ---
        etBusqueda   = findViewById(R.id.et_busqueda);
        spinnerTipo  = findViewById(R.id.spinner_tipo);
        rvResultados = findViewById(R.id.rvResultados);

        // --- RecyclerView + Adapter ---
        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter(items, BusquedaActivity.this, new ContentAdapter.Listener() {
            @Override
            public void onDetails(ContentItem item) {
                switch (item.getType()) {
                    case CANCION:
                        BusquedaCancionDTO c = (BusquedaCancionDTO) item.getDto();
                        Intent ci = new Intent(BusquedaActivity.this, CancionActivity.class);
                        ci.putExtra(CancionActivity.EXTRA_CANCION, c);
                        startActivity(ci);
                        break;
                    case ALBUM:
                        BusquedaAlbumDTO a = (BusquedaAlbumDTO) item.getDto();
                        Intent ai = new Intent(BusquedaActivity.this, AlbumActivity.class);
                        ai.putExtra(AlbumActivity.EXTRA_ALBUM, a);
                        startActivity(ai);
                        break;
                    case ARTISTA:
                        BusquedaArtistaDTO ar = (BusquedaArtistaDTO) item.getDto();
                        Intent ari = new Intent(BusquedaActivity.this, PerfilArtistaActivity.class);
                        ari.putExtra(PerfilArtistaActivity.EXTRA_ID_ARTISTA, ar);
                        startActivity(ari);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPlay(ContentItem item) {
                if (item.getType() == ContentItem.Type.CANCION) {
                    BusquedaCancionDTO c = (BusquedaCancionDTO) item.getDto();
                    ArrayList<BusquedaCancionDTO> single = new ArrayList<>();
                    single.add(c);
                    Reproductor.reproducirCancion(single, 0, BusquedaActivity.this);
                    startActivity(new Intent(BusquedaActivity.this, ReproductorActivity.class));
                } else if (item.getType() == ContentItem.Type.ALBUM) {
                    BusquedaAlbumDTO a = (BusquedaAlbumDTO) item.getDto();
                    ArrayList<BusquedaCancionDTO> lista =
                            new ArrayList<>(a.getCanciones());
                    Reproductor.reproducirCancion(lista, 0, BusquedaActivity.this);
                    startActivity(new Intent(BusquedaActivity.this, ReproductorActivity.class));
                }
            }

            @Override
            public void onSave(ContentItem item) {
                int userId = Preferencias.obtenerIdUsuario(BusquedaActivity.this);
                String tipo = item.getType().name();
                int contenidoId = 0;
                switch (item.getType()) {
                    case CANCION:
                        contenidoId = ((BusquedaCancionDTO)item.getDto()).getIdCancion();
                        break;
                    case ALBUM:
                        contenidoId = ((BusquedaAlbumDTO)item.getDto()).getIdAlbum();
                        break;
                    case ARTISTA:
                        contenidoId = ((BusquedaArtistaDTO)item.getDto()).getIdArtista();
                        break;
                    default:
                        break;
                }
                ContenidoGuardadoDTO dto =
                        new ContenidoGuardadoDTO(userId, tipo, contenidoId);
                ApiCliente.getContenidoGuardadoService(BusquedaActivity.this)
                        .guardarContenido(dto)
                        .enqueue(new Callback<RespuestaCliente<String>>() {
                            @Override
                            public void onResponse(Call<RespuestaCliente<String>> call,
                                                   Response<RespuestaCliente<String>> r) {
                                if (r.isSuccessful() && r.body()!=null) {
                                    Toast.makeText(BusquedaActivity.this,
                                            r.body().getMensaje(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<RespuestaCliente<String>> call, Throwable t) {
                                Toast.makeText(BusquedaActivity.this,
                                        R.string.error_conexion,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        rvResultados.setAdapter(adapter);

        // --- Cliente de búsqueda ---
        srv = ApiCliente.getClient(this).create(ServicioBusqueda.class);

        // Si venimos con query precargada
        String q = getIntent().getStringExtra(MenuPrincipalActivity.EXTRA_QUERY);
        if (q!=null && !q.isEmpty()) {
            etBusqueda.setText(q);
            performSearch();
        }

        findViewById(R.id.btn_buscar)
                .setOnClickListener(v -> performSearch());
    }

    // Lanza la búsqueda según el tipo
    private void performSearch() {
        String texto = etBusqueda.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, R.string.error_busqueda_vacia, Toast.LENGTH_SHORT).show();
            return;
        }
        // Limpia la lista y refresca
        items.clear();
        adapter.notifyDataSetChanged();

        int pos = spinnerTipo.getSelectedItemPosition();
        if (pos == 0)       buscarCanciones(texto);
        else if (pos == 1)  buscarAlbumes(texto);
        else                buscarArtistas(texto);
    }

    private void buscarCanciones(String texto) {
        srv.buscarCanciones(texto)
                .enqueue(new Callback<RespuestaCliente<List<BusquedaCancionDTO>>>() {
                    @Override public void onResponse(Call<RespuestaCliente<List<BusquedaCancionDTO>>> c,
                                                     Response<RespuestaCliente<List<BusquedaCancionDTO>>> r) {
                        if (!r.isSuccessful() || r.body()==null || r.body().getDatos().isEmpty()) {
                            Toast.makeText(BusquedaActivity.this,
                                    getString(R.string.no_resultados_cancion, texto),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (BusquedaCancionDTO dto : r.body().getDatos()) {
                            items.add(new ContentItem(
                                    ContentItem.Type.CANCION,
                                    dto,
                                    true
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onFailure(Call<RespuestaCliente<List<BusquedaCancionDTO>>> c, Throwable t) {
                        Toast.makeText(BusquedaActivity.this,
                                R.string.error_conexion,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarAlbumes(String texto) {
        srv.buscarAlbumes(texto)
                .enqueue(new Callback<RespuestaCliente<List<BusquedaAlbumDTO>>>() {
                    @Override public void onResponse(Call<RespuestaCliente<List<BusquedaAlbumDTO>>> c,
                                                     Response<RespuestaCliente<List<BusquedaAlbumDTO>>> r) {
                        if (!r.isSuccessful() || r.body()==null || r.body().getDatos().isEmpty()) {
                            Toast.makeText(BusquedaActivity.this,
                                    getString(R.string.no_resultados_album, texto),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (BusquedaAlbumDTO dto : r.body().getDatos()) {
                            items.add(new ContentItem(
                                    ContentItem.Type.ALBUM,
                                    dto,
                                    true
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onFailure(Call<RespuestaCliente<List<BusquedaAlbumDTO>>> c, Throwable t) {
                        Toast.makeText(BusquedaActivity.this,
                                R.string.error_conexion,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarArtistas(String texto) {
        srv.buscarArtistas(texto)
                .enqueue(new Callback<RespuestaCliente<List<BusquedaArtistaDTO>>>() {
                    @Override public void onResponse(Call<RespuestaCliente<List<BusquedaArtistaDTO>>> c,
                                                     Response<RespuestaCliente<List<BusquedaArtistaDTO>>> r) {
                        if (!r.isSuccessful() || r.body()==null || r.body().getDatos().isEmpty()) {
                            Toast.makeText(BusquedaActivity.this,
                                    getString(R.string.no_resultados_artista, texto),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (BusquedaArtistaDTO dto : r.body().getDatos()) {
                            items.add(new ContentItem(
                                    ContentItem.Type.ARTISTA,
                                    dto,
                                    true
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onFailure(Call<RespuestaCliente<List<BusquedaArtistaDTO>>> c, Throwable t) {
                        Toast.makeText(BusquedaActivity.this,
                                R.string.error_conexion,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
