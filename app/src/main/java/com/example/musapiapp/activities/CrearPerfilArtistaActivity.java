package com.example.musapiapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearPerfilArtistaActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    private ImageButton btnVolver;
    private ImageView ivFoto;
    private Button btnSubirFoto, btnConfirmar;
    private EditText etDescripcion;

    private String fotoPath;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_perfil_artista);

        // Referencias UI
        btnVolver = findViewById(R.id.btnVolver);
        ivFoto = findViewById(R.id.ivFoto);
        btnSubirFoto = findViewById(R.id.btnSubirFoto);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // Cargar usuario de SharedPreferences
        String jsonUser = Preferencias.recuperarUsuarioJson(this);
        if (jsonUser != null) {
            UsuarioDTO u = new Gson().fromJson(jsonUser, UsuarioDTO.class);
            idUsuario = u.getIdUsuario();
        }

        // Volver atrás
        btnVolver.setOnClickListener(v -> finish());

        // Elegir foto de galería
        btnSubirFoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pick.setType("image/*");
            startActivityForResult(pick, PICK_IMAGE);
        });

        // Confirmar creación del perfil
        btnConfirmar.setOnClickListener(v -> {
            if (validarCampos()) {
                crearPerfilArtista();
            } else {
                Toast.makeText(this,
                        "Por favor, completa todos los campos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Resuelvo URI -> ruta real en disco
    private String realPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{MediaStore.Images.Media.DATA},
                null, null, null
        );
        if (cursor == null) return null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(idx);
        cursor.close();
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            ivFoto.setImageURI(uri);
            fotoPath = realPathFromUri(uri);
        }
    }

    private boolean validarCampos() {
        boolean ok = true;

        if (etDescripcion.getText().toString().trim().isEmpty()) {
            etDescripcion.setError("La descripción es requerida");
            ok = false;
        }

        if (fotoPath == null) {
            Toast.makeText(this,
                    "Debes seleccionar una foto de perfil",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        return ok;
    }

    private void crearPerfilArtista() {
        // Construir RequestBody para la descripción
        RequestBody rbDescripcion = RequestBody.create(
                MediaType.parse("text/plain"),
                etDescripcion.getText().toString().trim()
        );

        // Preparar foto multipart
        MultipartBody.Part parteFoto = null;
        if (fotoPath != null) {
            File fotoFile = new File(fotoPath);
            RequestBody rbFoto = RequestBody.create(
                    MediaType.parse("image/*"),
                    fotoFile
            );
            parteFoto = MultipartBody.Part.createFormData(
                    "foto",
                    fotoFile.getName(),
                    rbFoto
            );
        }

        // Obtener token guardado
        String token = Preferencias.obtenerToken(this);
        String bearer = token != null ? "Bearer " + token : "";

        ServicioUsuario srv = ApiCliente.getClient(this)
                .create(ServicioUsuario.class);

        srv.crearPerfilArtista(
                bearer,
                idUsuario,
                rbDescripcion,
                parteFoto
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    // 1) Actualizar SharedPreferences marcando como artista
                    UsuarioDTO u = new Gson().fromJson(
                            Preferencias.recuperarUsuarioJson(CrearPerfilArtistaActivity.this),
                            UsuarioDTO.class
                    );
                    u.setEsArtista(true);
                    Preferencias.guardarUsuarioJson(
                            CrearPerfilArtistaActivity.this,
                            new Gson().toJson(u)
                    );

                    // 2) Mostrar feedback
                    Toast.makeText(CrearPerfilArtistaActivity.this,
                            "Perfil de artista creado exitosamente",
                            Toast.LENGTH_SHORT).show();

                    // 3) Cerrar la actividad
                    finish();
                } else {
                    Toast.makeText(CrearPerfilArtistaActivity.this,
                            "Error al crear el perfil de artista",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CrearPerfilArtistaActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}