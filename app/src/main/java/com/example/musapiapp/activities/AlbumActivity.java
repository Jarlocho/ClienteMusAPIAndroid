package com.example.musapiapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioAlbum;
import com.example.musapiapp.network.ServicioCancion;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;
import androidx.appcompat.app.AlertDialog;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM = "EXTRA_ALBUM";
    public static final String EXTRA_ALBUM_PENDIENTE = "EXTRA_ALBUM_PENDIENTE";

    private ImageView ivPortada;
    private TextView tvNombreAlbum, tvArtista, tvFecha, tvDuracion;
    private LinearLayout llCanciones;
    private Button btnSubirCancion, btnPublicar;

    private InfoAlbumDTO albumPendiente;
    private BusquedaAlbumDTO albumPublicado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Vistas
        ivPortada = findViewById(R.id.ivPortada);
        tvNombreAlbum = findViewById(R.id.tvNombreAlbum);
        tvArtista = findViewById(R.id.tvArtista);
        tvFecha = findViewById(R.id.tvFecha);
        tvDuracion = findViewById(R.id.tvDuracion);
        llCanciones = findViewById(R.id.llCanciones);
        btnSubirCancion = findViewById(R.id.btnSubirCancion);
        btnPublicar = findViewById(R.id.btnPublicar);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // ¿Es álbum pendiente o publicado?
        if (getIntent().hasExtra(EXTRA_ALBUM_PENDIENTE)) {
            albumPendiente = (InfoAlbumDTO) getIntent().getSerializableExtra(EXTRA_ALBUM_PENDIENTE);
            cargarDatosPendiente();
        } else if (getIntent().hasExtra(EXTRA_ALBUM)) {
            albumPublicado = (BusquedaAlbumDTO) getIntent().getSerializableExtra(EXTRA_ALBUM);
            cargarDatosPublicado();
        }
    }

    private void cargarDatosPublicado() {
        btnSubirCancion.setVisibility(View.GONE);
        btnPublicar.setVisibility(View.GONE);

        tvNombreAlbum.setText(albumPublicado.getNombreAlbum());
        tvArtista.setText("Artista: " + albumPublicado.getNombreArtista());
        tvFecha.setText("Fecha publicación: " + albumPublicado.getFechaPublicacion());

        Glide.with(this)
                .load(ApiCliente.getUrlArchivos() + albumPublicado.getUrlFoto())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivPortada);

        mostrarCanciones(albumPublicado.getCanciones());
    }

    private void cargarDatosPendiente() {
        tvNombreAlbum.setText(albumPendiente.getNombre());
        tvArtista.setText("Artista: " + albumPendiente.getNombreArtista());
        tvFecha.setText("Fecha publicación: (pendiente)");

        Glide.with(this)
                .load(ApiCliente.getUrlArchivos() + albumPendiente.getUrlFoto())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivPortada);
        Log.d("AlbumDebug", "URL foto recibida: " + albumPendiente.getUrlFoto());

        cargarCancionesPendientes();

        btnSubirCancion.setOnClickListener(v -> {
            Intent i = new Intent(this, SubirCancionActivity.class);
            i.putExtra("EXTRA_ID_ARTISTA", Preferencias.obtenerIdUsuario(this));
            i.putExtra("EXTRA_ID_ALBUM", albumPendiente.getIdAlbum());
            startActivity(i);
        });

        btnPublicar.setOnClickListener(v -> publicarAlbum());
    }

    private void cargarCancionesPendientes() {
        ServicioCancion svc = ApiCliente.getClient(this).create(ServicioCancion.class);
        svc.getCancionesPorAlbum(albumPendiente.getIdAlbum())
                .enqueue(new Callback<RespuestaCliente<List<BusquedaCancionDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<List<BusquedaCancionDTO>>> call,
                                           Response<RespuestaCliente<List<BusquedaCancionDTO>>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            mostrarCanciones(resp.body().getDatos());
                        } else {
                            Toast.makeText(AlbumActivity.this, "No se pudo cargar canciones", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaCliente<List<BusquedaCancionDTO>>> call, Throwable t) {
                        Toast.makeText(AlbumActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarCanciones(List<BusquedaCancionDTO> canciones) {
        llCanciones.removeAllViews();
        long totalMs = 0;

        for (BusquedaCancionDTO c : canciones) {
            TextView t = new TextView(this);
            t.setText("• " + c.getNombre());
            llCanciones.addView(t);

            try {
                String[] partes = c.getDuracion().split(":");
                int min = Integer.parseInt(partes[0]);
                int sec = Integer.parseInt(partes[1]);
                totalMs += (min * 60 + sec) * 1000L;
            } catch (Exception ignored) {}
        }

        String durStr = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(totalMs),
                TimeUnit.MILLISECONDS.toMinutes(totalMs) % 60,
                TimeUnit.MILLISECONDS.toSeconds(totalMs) % 60);
        tvDuracion.setText("Duración total: " + durStr);
    }

    private void publicarAlbum() {
        int numCanciones = llCanciones.getChildCount();
        if (numCanciones < 2) {
            Toast.makeText(this, "Debes tener al menos 2 canciones para publicar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Publicar álbum")
                .setMessage("¿Estás seguro de que deseas publicar este álbum?")
                .setPositiveButton("Sí", (d, w) -> {
                    ServicioAlbum svc = ApiCliente.getClient(this).create(ServicioAlbum.class);
                    svc.publicarAlbum(albumPendiente.getIdAlbum())
                            .enqueue(new Callback<RespuestaCliente<String>>() {
                                @Override
                                public void onResponse(Call<RespuestaCliente<String>> call,
                                                       Response<RespuestaCliente<String>> resp) {
                                    if (resp.isSuccessful()) {
                                        Toast.makeText(AlbumActivity.this,
                                                "Álbum publicado", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(AlbumActivity.this,
                                                "Error al publicar", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<RespuestaCliente<String>> call, Throwable t) {
                                    Toast.makeText(AlbumActivity.this,
                                            "Fallo de red", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
