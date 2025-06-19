package com.example.musapiapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioBusqueda;
import com.example.musapiapp.activities.PerfilUsuarioActivity;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.Preferencias;
import com.example.musapiapp.util.Reproductor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusquedaActivity extends BaseActivity {
    private EditText      etBusqueda;
    private Spinner       spinnerTipo;
    private LinearLayout  llResultados;
    private ServicioBusqueda srv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);
        // --- Volver atrás ---
        ImageButton btnVolver = findViewById(R.id.btn_volver_busqueda);
        btnVolver.setOnClickListener(v -> onBackPressed());

        ImageButton btnPerfilUsuario = findViewById(R.id.btn_perfil_usuario);
        btnPerfilUsuario.setOnClickListener(v ->
                startActivity(new Intent(BusquedaActivity.this, PerfilUsuarioActivity.class))
        );

        etBusqueda   = findViewById(R.id.et_busqueda);
        spinnerTipo  = findViewById(R.id.spinner_tipo);
        llResultados = findViewById(R.id.ll_resultados);
        
        srv = ApiCliente.getClient(this)
                .create(ServicioBusqueda.class);

        Intent in = getIntent();
        String q = in.getStringExtra(MenuPrincipalActivity.EXTRA_QUERY);
        if (q != null && !q.isEmpty()) {
            etBusqueda.setText(q);
        }

        findViewById(R.id.btn_buscar)
                .setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String texto = etBusqueda.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, R.string.error_busqueda_vacia, Toast.LENGTH_SHORT).show();
            return;
        }
        llResultados.removeAllViews();

        switch (spinnerTipo.getSelectedItemPosition()) {
            case 0: buscarCanciones(texto); break;
            case 1: buscarAlbumes(texto);  break;
            default: buscarArtistas(texto); break;
        }
    }

    private void buscarCanciones(String texto) {
        srv.buscarCanciones(texto).enqueue(new Callback<>() {
            @Override public void onResponse(@NonNull Call<RespuestaCliente<List<BusquedaCancionDTO>>> c,
                                             @NonNull Response<RespuestaCliente<List<BusquedaCancionDTO>>> r) {
                if (r.code() == 404) {
                    // Si la API devuelve 404 cuando no hay datos
                    Toast.makeText(BusquedaActivity.this,
                            getString(R.string.no_resultados_cancion, texto),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!r.isSuccessful()) {
                    logAndToast("Canciones", r);
                    return;
                }
                var body = r.body();
                if (body == null || body.getDatos() == null || body.getDatos().isEmpty()) {
                    Toast.makeText(BusquedaActivity.this,
                            getString(R.string.no_resultados_cancion, texto),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                for (BusquedaCancionDTO dto : body.getDatos()) {
                    View item = LayoutInflater.from(BusquedaActivity.this)
                            .inflate(R.layout.item_contenido, llResultados, false);
                    ImageView   ivFoto   = item.findViewById(R.id.imgFoto);
                    TextView    tvNombre = item.findViewById(R.id.tvNombre);
                    TextView    tvAutor  = item.findViewById(R.id.tvAutor);
                    ImageButton btnPlay  = item.findViewById(R.id.btnReproducir);
                    TextView    btnDet   = item.findViewById(R.id.btnVerDetalles);
                    View        btnSave  = item.findViewById(R.id.btnGuardar);

                    tvNombre.setText(dto.getNombre());
                    tvAutor .setText(dto.getNombreArtista());
                    btnSave .setVisibility(View.GONE);
                    cargarImagen(dto.getUrlFoto(), ivFoto);
                    btnDet.setOnClickListener(v -> {
                        Intent i = new Intent(BusquedaActivity.this, CancionActivity.class);
                        i.putExtra(CancionActivity.EXTRA_CANCION, dto);
                        startActivity(i);
                    });
                    btnPlay.setOnClickListener(v -> {

                        ArrayList<BusquedaCancionDTO> lista = new ArrayList<>();
                        lista.add(dto);
                        Reproductor.reproducirCancion(lista, 0, BusquedaActivity.this);

                        startActivity(new Intent(BusquedaActivity.this, ReproductorActivity.class));
                    });



                    llResultados.addView(item);
                }
            }
            @Override public void onFailure(@NonNull Call<RespuestaCliente<List<BusquedaCancionDTO>>> c,
                                            @NonNull Throwable t) {
                Log.e("Canciones", "onFailure", t);
                Toast.makeText(BusquedaActivity.this,
                        R.string.error_conexion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarAlbumes(String texto) {
        srv.buscarAlbumes(texto).enqueue(new Callback<>() {
            @Override public void onResponse(@NonNull Call<RespuestaCliente<List<BusquedaAlbumDTO>>> c,
                                             @NonNull Response<RespuestaCliente<List<BusquedaAlbumDTO>>> r) {
                if (r.code() == 404) {
                    Toast.makeText(BusquedaActivity.this,
                            getString(R.string.no_resultados_album, texto),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!r.isSuccessful()) {
                    logAndToast("Álbumes", r);
                    return;
                }
                var body = r.body();
                if (body == null || body.getDatos() == null || body.getDatos().isEmpty()) {
                    Toast.makeText(BusquedaActivity.this,
                            getString(R.string.no_resultados_album, texto),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                for (BusquedaAlbumDTO dto : body.getDatos()) {
                    View item = LayoutInflater.from(BusquedaActivity.this)
                            .inflate(R.layout.item_contenido, llResultados, false);
                    ImageView ivFoto   = item.findViewById(R.id.imgFoto);
                    TextView  tvNombre = item.findViewById(R.id.tvNombre);
                    TextView  tvAutor  = item.findViewById(R.id.tvAutor);
                    TextView  btnDet   = item.findViewById(R.id.btnVerDetalles);
                    View      btnSave  = item.findViewById(R.id.btnGuardar);
                    ImageButton btnPlay  = item.findViewById(R.id.btnReproducir);

                    tvNombre.setText(dto.getNombreAlbum());
                    tvAutor .setText(dto.getNombreArtista());
                    btnSave .setVisibility(View.GONE);
                    cargarImagen(dto.getUrlFoto(), ivFoto);
                    btnDet.setOnClickListener(v -> {
                        Intent i = new Intent(BusquedaActivity.this, AlbumActivity.class);
                        i.putExtra(AlbumActivity.EXTRA_ALBUM, dto);
                        startActivity(i);
                    });
                    btnPlay.setOnClickListener(v -> {
                        ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(dto.getCanciones());
                        Reproductor.reproducirCancion(canciones, 0, BusquedaActivity.this);

                        startActivity(new Intent(BusquedaActivity.this, ReproductorActivity.class));
                    });


                    llResultados.addView(item);
                }
            }
            @Override public void onFailure(@NonNull Call<RespuestaCliente<List<BusquedaAlbumDTO>>> c,
                                            @NonNull Throwable t) {
                Log.e("Álbumes","onFailure",t);
                Toast.makeText(BusquedaActivity.this,
                        R.string.error_conexion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarArtistas(String texto) {
        srv.buscarArtistas(texto).enqueue(new Callback<>() {
            @Override public void onResponse(@NonNull Call<RespuestaCliente<List<BusquedaArtistaDTO>>> c,
                                             @NonNull Response<RespuestaCliente<List<BusquedaArtistaDTO>>> r) {
                if (r.code() == 404) {
                    Toast.makeText(BusquedaActivity.this,
                            getString(R.string.no_resultados_artista, texto),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!r.isSuccessful()) {
                    Toast.makeText(BusquedaActivity.this,
                            R.string.error_servidor, Toast.LENGTH_SHORT).show();
                    return;
                }
                var datos = r.body().getDatos();
                if (datos == null || datos.isEmpty()) {
                    Toast.makeText(BusquedaActivity.this,
                            getString(R.string.no_resultados_artista, texto),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                for (BusquedaArtistaDTO dto : datos) {
                    View item = LayoutInflater.from(BusquedaActivity.this)
                            .inflate(R.layout.item_contenido, llResultados, false);
                    ImageView ivFoto   = item.findViewById(R.id.imgFoto);
                    TextView  tvNombre = item.findViewById(R.id.tvNombre);
                    TextView  tvAutor  = item.findViewById(R.id.tvAutor);
                    TextView  btnDet   = item.findViewById(R.id.btnVerDetalles);
                    TextView  btnSave  = item.findViewById(R.id.btnGuardar);

                    tvNombre.setText(dto.getNombre());
                    tvAutor .setText("@" + dto.getNombreUsuario());
                    btnSave .setText(R.string.seguir);
                    cargarImagen(dto.getUrlFoto(), ivFoto);

                    btnDet.setOnClickListener(v -> {
                        Intent i = new Intent(BusquedaActivity.this, PerfilArtistaActivity.class);
                        i.putExtra(PerfilArtistaActivity.EXTRA_ID_ARTISTA, dto.getIdArtista());
                        startActivity(i);
                    });

                    llResultados.addView(item);
                }
            }
            @Override public void onFailure(@NonNull Call<RespuestaCliente<List<BusquedaArtistaDTO>>> c,
                                            @NonNull Throwable t) {
                Toast.makeText(BusquedaActivity.this,
                        R.string.error_conexion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void cargarImagen(String urlImagen, ImageView imageView) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            return;
        }

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    URL url = new URL(  Constantes.URL_BASE +urlImagen);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String token = Preferencias.obtenerToken(BusquedaActivity.this);
                    String bearer = token != null ? "Bearer " + token : "";
                    connection.setRequestProperty("Authorization", bearer);
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();

                    return bitmap;
                } catch (Exception e) {
                    //ivFoto.setImageResource(R.drawable.musapi_logo);
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    private <T> void logAndToast(String tag, Response<RespuestaCliente<T>> r) {
        try { Log.e(tag, "Error " + r.code() + ": " + r.errorBody().string()); }
        catch (IOException ignored) {}
        Toast.makeText(this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
    }
}
