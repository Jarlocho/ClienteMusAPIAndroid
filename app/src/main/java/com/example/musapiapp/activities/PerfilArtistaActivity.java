package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilArtistaActivity extends BaseActivity {
    public static final String EXTRA_ID_ARTISTA = "EXTRA_ID_ARTISTA";

    private ImageButton btnVolver,btnChat;
    private ImageView   ivFotoArtista;
    private TextView    tvNombreArtista,
            tvHandleArtista,
            tvDescripcionArtista;

    private int idArtista = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_artista);

        btnVolver            = findViewById(R.id.btn_volver_artista);
        btnChat              = findViewById(R.id.btn_chat);
        ivFotoArtista        = findViewById(R.id.ivFotoArtista);
        tvNombreArtista      = findViewById(R.id.tvNombreArtista);
        tvHandleArtista      = findViewById(R.id.tvHandleArtista);
        tvDescripcionArtista = findViewById(R.id.tvDescripcionArtista);

        btnVolver.setOnClickListener(v -> finish());

        // Botón Chat
        btnChat.setOnClickListener(v -> {
            // Lanzamos la pantalla de chat, pasando el ID y el nombre de usuario actual
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra(ChatActivity.EXTRA_ID_ARTISTA, idArtista);

            String usuarioJson = Preferencias.recuperarUsuarioJson(this);
            String handle = "";
            if (usuarioJson != null) {
                UsuarioDTO u = new Gson().fromJson(usuarioJson, UsuarioDTO.class);
                handle = u.getNombreUsuario();
            }
            i.putExtra(ChatActivity.EXTRA_NOMBRE_USUARIO, handle);
            startActivity(i);
        });

        // obtenemos el ID
        if (getIntent().hasExtra(EXTRA_ID_ARTISTA)) {
            idArtista = getIntent().getIntExtra(EXTRA_ID_ARTISTA, -1);
        }
        if (idArtista == -1) {
            Toast.makeText(this, "Artista no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // llamamos a la API
        bajarPerfilDesdeAPI(idArtista);
    }

    private void bajarPerfilDesdeAPI(int id) {
        String token  = Preferencias.obtenerToken(this);
        String bearer = token != null ? "Bearer " + token : "";

        ServicioUsuario srv = ApiCliente
                .getClient(this)
                .create(ServicioUsuario.class);

        srv.obtenerPerfilArtista(bearer, id)
                .enqueue(new Callback<RespuestaCliente<BusquedaArtistaDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<BusquedaArtistaDTO>> call,
                                           Response<RespuestaCliente<BusquedaArtistaDTO>> resp) {
                        if (resp.isSuccessful()
                                && resp.body() != null
                                && resp.body().getDatos() != null) {
                            poblarUI(resp.body().getDatos());
                        } else {
                            Toast.makeText(PerfilArtistaActivity.this,
                                    "Error al cargar perfil",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<BusquedaArtistaDTO>> call,
                                          Throwable t) {
                        Toast.makeText(PerfilArtistaActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void poblarUI(BusquedaArtistaDTO dto) {
        tvNombreArtista     .setText(dto.getNombre());
        tvHandleArtista     .setText("@" + dto.getNombreUsuario());
        tvDescripcionArtista.setText(dto.getDescripcion());

        String token = Preferencias.obtenerToken(this);
        String url   = ApiCliente.getUrlArchivos() + dto.getUrlFoto();

        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", token != null ? "Bearer " + token : "")
                .build());

        Glide.with(this)
                .load(glideUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivFotoArtista);
    }
}
