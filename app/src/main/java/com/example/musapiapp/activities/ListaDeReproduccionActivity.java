// src/main/java/com/example/musapiapp/activities/ListaDeReproduccionActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapter.ContentAdapter;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.ListaReproduccionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.model.ContentItem;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioListasDeReproduccion;
import com.example.musapiapp.util.Reproductor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaDeReproduccionActivity extends BaseActivity {
    public static final String EXTRA_LISTA = "extra_lista";

    private ListaReproduccionDTO lista;
    private RecyclerView         rvCanciones;
    private ContentAdapter       adapter;
    private List<ContentItem>    items = new ArrayList<>();
    private ImageButton          btnVolver;
    private TextView             tvTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reproduccion);

        // --- findViewById ---
        btnVolver     = findViewById(R.id.btn_volver_lista);
        tvTitulo      = findViewById(R.id.tvListaNombre);
        rvCanciones   = findViewById(R.id.rvCancionesLista);

        // Recupera el DTO completo
        if (getIntent()!=null && getIntent().hasExtra(EXTRA_LISTA)) {
            lista = (ListaReproduccionDTO)
                    getIntent().getSerializableExtra(EXTRA_LISTA);
        }
        if (lista == null) {
            Toast.makeText(this, "Lista no especificada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ajusta título
        tvTitulo.setText(lista.getNombre());

        // Configura RecyclerView
        rvCanciones.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        adapter = new ContentAdapter(items, ListaDeReproduccionActivity.this, new ContentAdapter.Listener() {
            @Override
            public void onDetails(ContentItem item) {
                BusquedaCancionDTO c = (BusquedaCancionDTO)item.getDto();
                Intent i = new Intent(ListaDeReproduccionActivity.this, CancionActivity.class);
                i.putExtra(CancionActivity.EXTRA_CANCION, c);
                startActivity(i);
            }
            @Override
            public void onPlay(ContentItem item) {
                // Ahora creamos un ArrayList antes de llamar al Reproductor
                ArrayList<BusquedaCancionDTO> listaCanc = new ArrayList<>();
                for (ContentItem ci : items) {
                    listaCanc.add((BusquedaCancionDTO)ci.getDto());
                }
                int idx = items.indexOf(item);
                Reproductor.reproducirCancion(listaCanc, idx, ListaDeReproduccionActivity.this);
                startActivity(new Intent(ListaDeReproduccionActivity.this, ReproductorActivity.class));
            }
            @Override
            public void onSave(ContentItem item) {
                // No aplicamos “guardar” aquí
            }
        });
        rvCanciones.setAdapter(adapter);

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());

        // Carga canciones via API
        cargarCancionesDeLista();
    }

    private void cargarCancionesDeLista() {
        ServicioListasDeReproduccion svc = ApiCliente
                .getClient(this)
                .create(ServicioListasDeReproduccion.class);

        svc.getCancionesDeLista(lista.getIdListaDeReproduccion())
                .enqueue(new Callback<RespuestaCliente<List<BusquedaCancionDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<List<BusquedaCancionDTO>>> call,
                                           Response<RespuestaCliente<List<BusquedaCancionDTO>>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            items.clear();
                            for (BusquedaCancionDTO c : resp.body().getDatos()) {
                                items.add(new ContentItem(
                                        ContentItem.Type.CANCION,
                                        c,
                                        false
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ListaDeReproduccionActivity.this,
                                    "Error al cargar canciones", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<List<BusquedaCancionDTO>>> call, Throwable t) {
                        Toast.makeText(ListaDeReproduccionActivity.this,
                                "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
