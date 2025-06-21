package com.example.musapiapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.InfoAlbumDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioAlbum;
import com.example.musapiapp.util.Preferencias;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearAlbumActivity extends AppCompatActivity {

    private static final int REQ_IMG = 1001;

    private EditText etNombreAlbum;
    private ImageView ivPortadaAlbum;
    private Button btnSeleccionarPortada, btnCrearAlbum;

    private Uri uriPortada;

    private int idArtista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_album);

        etNombreAlbum        = findViewById(R.id.etNombreAlbum);
        ivPortadaAlbum       = findViewById(R.id.ivPortadaAlbum);
        btnSeleccionarPortada = findViewById(R.id.btnSeleccionarPortada);
        btnCrearAlbum         = findViewById(R.id.btnCrearAlbum);

        idArtista = getIntent().getIntExtra(SubirContenidoActivity.EXTRA_ID_ARTISTA, -1);
        if (idArtista < 0) {
            Toast.makeText(this, "ID de artista no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSeleccionarPortada.setOnClickListener(v -> seleccionarPortada());
        btnCrearAlbum.setOnClickListener(v -> crearAlbum());
    }

    private void seleccionarPortada() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, REQ_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == REQ_IMG && resultCode == RESULT_OK && data != null) {
            uriPortada = data.getData();
            getContentResolver().takePersistableUriPermission(
                    uriPortada, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Glide.with(this).load(uriPortada).into(ivPortadaAlbum);
        }
    }

    private void crearAlbum() {
        String nombre = etNombreAlbum.getText().toString().trim();

        if (nombre.isEmpty() || uriPortada == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] fotoBytes = readAllBytes(uriPortada);

            Map<String, RequestBody> campos = new HashMap<>();
            campos.put("nombre", RequestBody.create(MediaType.parse("text/plain"), nombre));
            campos.put(
                    "idUsuario",
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            String.valueOf(Preferencias.obtenerIdUsuario(this))
                    )
            );

            MultipartBody.Part partFoto = MultipartBody.Part.createFormData(
                    "foto",
                    obtenerNombreArchivo(uriPortada),
                    RequestBody.create(MediaType.parse("image/jpeg"), fotoBytes)
            );

            ServicioAlbum svc = ApiCliente.getClient(this).create(ServicioAlbum.class);
            svc.crearAlbum(campos, partFoto).enqueue(new Callback<RespuestaCliente<String>>() {
                @Override
                public void onResponse(Call<RespuestaCliente<String>> call,
                                       Response<RespuestaCliente<String>> resp) {
                    if (resp.isSuccessful()) {
                        buscarAlbumRecienCreado(nombre);
                    } else {
                        Toast.makeText(CrearAlbumActivity.this,
                                "Error HTTP " + resp.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaCliente<String>> call, Throwable t) {
                    Toast.makeText(CrearAlbumActivity.this,
                            "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
        }
    }


    private void buscarAlbumRecienCreado(String nombreEsperado) {
        ServicioAlbum svc = ApiCliente.getClient(this).create(ServicioAlbum.class);
        svc.obtenerAlbumesPendientes(idArtista)
                .enqueue(new Callback<RespuestaCliente<List<InfoAlbumDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<List<InfoAlbumDTO>>> call,
                                           Response<RespuestaCliente<List<InfoAlbumDTO>>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            List<InfoAlbumDTO> lista = resp.body().getDatos();
                            for (InfoAlbumDTO album : lista) {
                                if (album.getNombre().equalsIgnoreCase(nombreEsperado)) {
                                    // Devolverlo al padre
                                    Intent result = new Intent();
                                    result.putExtra(AlbumActivity.EXTRA_ALBUM_PENDIENTE, album);
                                    setResult(RESULT_OK, result);
                                    finish();
                                    return;
                                }
                            }
                            Toast.makeText(CrearAlbumActivity.this,
                                    "Álbum creado, pero no encontrado para mostrar", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(CrearAlbumActivity.this,
                                    "No se pudo verificar el álbum creado", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaCliente<List<InfoAlbumDTO>>> call, Throwable t) {
                        Toast.makeText(CrearAlbumActivity.this,
                                "Error de red al recuperar álbum", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private byte[] readAllBytes(Uri uri) throws Exception {
        try (InputStream in = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            return out.toByteArray();
        }
    }

    private String obtenerNombreArchivo(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception ignored) {}
        return "portada.jpg";
    }


}
