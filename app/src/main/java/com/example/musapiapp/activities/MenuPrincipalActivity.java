package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapter.ContentAdapter;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.model.ContentItem;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioContenidoGuardado;
import com.example.musapiapp.network.ServicioListasDeReproduccion;
import com.example.musapiapp.util.Preferencias;
import com.example.musapiapp.util.Reproductor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuPrincipalActivity extends BaseActivity {
    public static final String EXTRA_QUERY = "extra_query";

    private ImageButton btnCerrarSesion, btnBuscar, btnMenuAdmin, btnPerfilUsuario, btnCrearLista;
    private EditText etBusqueda;
    private RecyclerView rvAlbumes, rvListas, rvArtistas;

    private ContentAdapter adapterAlb, adapterLst, adapterArt;
    private List<ContentItem> listAlb = new ArrayList<>();
    private List<ContentItem> listLst = new ArrayList<>();
    private List<ContentItem> listArt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // --- findViewById ---
        btnCerrarSesion  = findViewById(R.id.btn_cerrar_sesion);
        btnBuscar        = findViewById(R.id.btn_buscar);
        btnMenuAdmin     = findViewById(R.id.btn_menu_admin);
        btnPerfilUsuario = findViewById(R.id.btn_perfil_usuario);
        btnCrearLista    = findViewById(R.id.btn_crear_lista);
        etBusqueda       = findViewById(R.id.et_busqueda);
        rvAlbumes        = findViewById(R.id.rvAlbumes);
        rvListas         = findViewById(R.id.rvListas);
        rvArtistas       = findViewById(R.id.rvArtistas);

        // --- RecyclerViews horizontales ---
        rvAlbumes.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        rvListas.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        rvArtistas.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        adapterAlb = new ContentAdapter(listAlb, albListener);
        adapterLst = new ContentAdapter(listLst, lstListener);
        adapterArt = new ContentAdapter(listArt, artListener);

        rvAlbumes.setAdapter(adapterAlb);
        rvListas.setAdapter(adapterLst);
        rvArtistas.setAdapter(adapterArt);

        // --- Navegación ---
        btnCerrarSesion.setOnClickListener(v -> {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        });
        btnBuscar.setOnClickListener(v -> {
            String q = etBusqueda.getText().toString().trim();
            startActivity(new Intent(this, BusquedaActivity.class)
                    .putExtra(EXTRA_QUERY, q));
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

        // --- Carga contenido guardado de la API ---
        cargarContenidoGuardado();
    }

    // Listener para álbumes
    private final ContentAdapter.Listener albListener = new ContentAdapter.Listener() {
        @Override public void onDetails(ContentItem item) {
            BusquedaAlbumDTO a = (BusquedaAlbumDTO)item.getDto();
            startActivity(new Intent(MenuPrincipalActivity.this, AlbumActivity.class)
                    .putExtra(AlbumActivity.EXTRA_ALBUM, a));
        }
        @Override public void onPlay(ContentItem item) {
            // TODO: reproducir álbum completo
        }
        @Override public void onSave(ContentItem item) { /* no aplica */ }
    };

    // Listener para listas
    private final ContentAdapter.Listener lstListener = new ContentAdapter.Listener() {
        @Override public void onDetails(ContentItem item) {
            ListaReproduccionDTO l = (ListaReproduccionDTO)item.getDto();
            startActivity(new Intent(MenuPrincipalActivity.this,
                    ListaDeReproduccionActivity.class)
                    .putExtra(ListaDeReproduccionActivity.EXTRA_LISTA, l));
        }
        @Override public void onPlay(ContentItem item) {
            // 1) Obtiene DTO
            ListaReproduccionDTO lista = (ListaReproduccionDTO)item.getDto();
            // 2) Llama al servicio para sus canciones
            ServicioListasDeReproduccion svc = ApiCliente
                    .getClient(MenuPrincipalActivity.this)
                    .create(ServicioListasDeReproduccion.class);

            svc.getCancionesDeLista(lista.getIdListaDeReproduccion())
                    .enqueue(new Callback<RespuestaCliente<List<BusquedaCancionDTO>>>() {
                        @Override
                        public void onResponse(Call<RespuestaCliente<List<BusquedaCancionDTO>>> call,
                                               Response<RespuestaCliente<List<BusquedaCancionDTO>>> resp) {
                            if (resp.isSuccessful() && resp.body()!=null) {
                                List<BusquedaCancionDTO> canciones = resp.body().getDatos();
                                // 3) Reproduce
                                ArrayList<BusquedaCancionDTO> listaParaReproducir =
                                        new ArrayList<>(canciones);
                                Reproductor.reproducirCancion(
                                        listaParaReproducir,
                                        0,
                                        MenuPrincipalActivity.this
                                );
                                startActivity(
                                        new Intent(MenuPrincipalActivity.this, ReproductorActivity.class)
                                );
                            } else {
                                Toast.makeText(MenuPrincipalActivity.this,
                                        "No se pudieron cargar las canciones de la lista",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<RespuestaCliente<List<BusquedaCancionDTO>>> call, Throwable t) {
                            Toast.makeText(MenuPrincipalActivity.this,
                                    "Error de red: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
        @Override public void onSave(ContentItem item) { /* no aplica */ }
    };

    // Listener para artistas
    private final ContentAdapter.Listener artListener = new ContentAdapter.Listener() {
        @Override public void onDetails(ContentItem item) {
            BusquedaArtistaDTO ar = (BusquedaArtistaDTO)item.getDto();
            Intent i = new Intent(MenuPrincipalActivity.this, PerfilArtistaActivity.class);
            i.putExtra(PerfilArtistaActivity.EXTRA_ID_ARTISTA,
                    ar.getIdArtista());
            startActivity(i);
        }
        @Override public void onPlay(ContentItem item) { /* no aplica */ }
        @Override public void onSave(ContentItem item) {
            int uid = Preferencias.obtenerIdUsuario(MenuPrincipalActivity.this);
            BusquedaArtistaDTO ar = (BusquedaArtistaDTO)item.getDto();
            ContenidoGuardadoDTO dto = new ContenidoGuardadoDTO(uid, "ARTISTA", ar.getIdArtista());
            ApiCliente.getContenidoGuardadoService(MenuPrincipalActivity.this)
                    .guardarContenido(dto)
                    .enqueue(new Callback<RespuestaCliente<String>>() {
                        @Override public void onResponse(Call<RespuestaCliente<String>> c,
                                                         Response<RespuestaCliente<String>> r) {
                            if (r.isSuccessful() && r.body()!=null) {
                                Toast.makeText(MenuPrincipalActivity.this,
                                        r.body().getMensaje(), Toast.LENGTH_SHORT).show();
                                adapterArt.notifyDataSetChanged();
                            }
                        }
                        @Override public void onFailure(Call<RespuestaCliente<String>> c, Throwable t) {
                            Toast.makeText(MenuPrincipalActivity.this,
                                    "Error al guardar artista: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    };

    private void cargarContenidoGuardado() {
        int uid = Preferencias.obtenerIdUsuario(this);
        if (uid < 0) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_LONG).show();
            return;
        }
        ServicioContenidoGuardado svc = ApiCliente
                .getClient(this)
                .create(ServicioContenidoGuardado.class);

        // Álbumes
        svc.getAlbumesGuardados(uid).enqueue(new Callback<RespuestaCliente<List<BusquedaAlbumDTO>>>() {
            @Override public void onResponse(Call<RespuestaCliente<List<BusquedaAlbumDTO>>> c,
                                             Response<RespuestaCliente<List<BusquedaAlbumDTO>>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    listAlb.clear();
                    for (BusquedaAlbumDTO a : r.body().getDatos()) {
                        listAlb.add(new ContentItem(ContentItem.Type.ALBUM, a, false));
                    }
                    adapterAlb.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<RespuestaCliente<List<BusquedaAlbumDTO>>> c, Throwable t) {
                Toast.makeText(MenuPrincipalActivity.this,
                        "Error al cargar álbumes: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Listas
        svc.getListasGuardadas(uid).enqueue(new Callback<RespuestaCliente<List<ListaReproduccionDTO>>>() {
            @Override public void onResponse(Call<RespuestaCliente<List<ListaReproduccionDTO>>> c,
                                             Response<RespuestaCliente<List<ListaReproduccionDTO>>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    listLst.clear();
                    for (ListaReproduccionDTO l : r.body().getDatos()) {
                        listLst.add(new ContentItem(ContentItem.Type.LISTA, l, false));
                    }
                    adapterLst.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<RespuestaCliente<List<ListaReproduccionDTO>>> c, Throwable t) {
                Toast.makeText(MenuPrincipalActivity.this,
                        "Error al cargar listas: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Artistas
        svc.getArtistasGuardados(uid).enqueue(new Callback<RespuestaCliente<List<BusquedaArtistaDTO>>>() {
            @Override public void onResponse(Call<RespuestaCliente<List<BusquedaArtistaDTO>>> c,
                                             Response<RespuestaCliente<List<BusquedaArtistaDTO>>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    listArt.clear();
                    for (BusquedaArtistaDTO ar : r.body().getDatos()) {
                        listArt.add(new ContentItem(ContentItem.Type.ARTISTA, ar, true));
                    }
                    adapterArt.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<RespuestaCliente<List<BusquedaArtistaDTO>>> c, Throwable t) {
                Toast.makeText(MenuPrincipalActivity.this,
                        "Error al cargar artistas: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
