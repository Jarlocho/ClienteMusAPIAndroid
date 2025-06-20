// src/main/java/com/example/musapiapp/activities/PerfilArtistaActivity.java
package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.dialogs.EvaluarArtistaDialog;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilArtistaActivity extends AppCompatActivity {
    /** Clave para pasar sólo el ID del artista */
    public static final String EXTRA_ID_ARTISTA = "EXTRA_ID_ARTISTA";

    private int idArtista;
    private BusquedaArtistaDTO artista;

    private ImageButton btnVolver, btnChat, btnEvaluar;
    private ImageView   ivFotoArtista;
    private TextView    tvNombreArtista, tvHandleArtista, tvDescripcionArtista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_artista);

        // 1) Leer siempre el ID que viene por EXTRA_ID_ARTISTA
        if (!getIntent().hasExtra(EXTRA_ID_ARTISTA)) {
            Toast.makeText(this, "Artista no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        idArtista = getIntent().getIntExtra(EXTRA_ID_ARTISTA, -1);
        if (idArtista < 0) {
            Toast.makeText(this, "Artista no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Bind de vistas
        btnVolver            = findViewById(R.id.btn_volver_artista);
        btnChat              = findViewById(R.id.btn_chat);
        ivFotoArtista        = findViewById(R.id.ivFotoArtista);
        tvNombreArtista      = findViewById(R.id.tvNombreArtista);
        tvHandleArtista      = findViewById(R.id.tvHandleArtista);
        tvDescripcionArtista = findViewById(R.id.tvDescripcionArtista);
        btnEvaluar           = findViewById(R.id.btnEvaluar);

        btnVolver.setOnClickListener(v -> finish());
        btnChat.setOnClickListener(v -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra(ChatActivity.EXTRA_ID_ARTISTA, idArtista);
            // aquí no cambió
            String ujson = Preferencias.recuperarUsuarioJson(this);
            if (ujson != null) {
                UsuarioDTO yo = new Gson().fromJson(ujson, UsuarioDTO.class);
                i.putExtra(ChatActivity.EXTRA_NOMBRE_USUARIO, yo.getNombreUsuario());
            }
            startActivity(i);
        });

        // 3) Bajamos el perfil desde la API
        bajarPerfilDesdeAPI();
    }

    private void bajarPerfilDesdeAPI() {
        String token  = Preferencias.obtenerToken(this);
        String bearer = token != null ? "Bearer " + token : "";

        ServicioUsuario srv = ApiCliente
                .getClient(this)
                .create(ServicioUsuario.class);

        srv.obtenerPerfilArtista(bearer, idArtista)
                .enqueue(new Callback<RespuestaCliente<BusquedaArtistaDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<BusquedaArtistaDTO>> call,
                                           Response<RespuestaCliente<BusquedaArtistaDTO>> resp) {
                        if (resp.isSuccessful() && resp.body() != null
                                && resp.body().getDatos() != null) {
                            artista = resp.body().getDatos();
                            poblarUI();
                        } else {
                            String err = "HTTP " + resp.code();
                            try {
                                if (resp.errorBody() != null)
                                    err += " — " + resp.errorBody().string();
                            } catch (IOException ignored) {}
                            Log.e("PerfilArtista", err);
                            Toast.makeText(PerfilArtistaActivity.this,
                                    "Error al cargar perfil: " + resp.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<BusquedaArtistaDTO>> call,
                                          Throwable t) {
                        Log.e("PerfilArtista", "Fallo de red", t);
                        Toast.makeText(PerfilArtistaActivity.this,
                                "Fallo de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void poblarUI() {
        // datos básicos
        tvNombreArtista     .setText(artista.getNombre());
        tvHandleArtista     .setText("@" + artista.getNombreUsuario());
        tvDescripcionArtista.setText(artista.getDescripcion());

        // foto con Glide + header auth
        String token = Preferencias.obtenerToken(this);
        String url   = ApiCliente.getUrlArchivos() + artista.getUrlFoto();
        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", token != null ? "Bearer " + token : "")
                .build());
        Glide.with(this)
                .load(glideUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivFotoArtista);

        // botón “Evaluar” sólo si no eres tú
        String ujson = Preferencias.recuperarUsuarioJson(this);
        String actualHandle = "";
        if (ujson != null) {
            actualHandle = new Gson().fromJson(ujson, UsuarioDTO.class)
                    .getNombreUsuario();
        }
        if (!artista.getNombreUsuario().equals(actualHandle)) {
            btnEvaluar.setVisibility(View.VISIBLE);
            btnEvaluar.setOnClickListener(v ->
                    EvaluarArtistaDialog.newInstance(idArtista)
                            .show(getSupportFragmentManager(),"eval_dialog")
            );
        } else {
            btnEvaluar.setVisibility(View.GONE);
        }
    }
}
