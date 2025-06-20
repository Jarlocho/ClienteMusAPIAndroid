package com.example.musapiapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioCancion;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubirCancionActivity extends AppCompatActivity {
    private static final int REQ_AUDIO = 1001, REQ_IMAGE = 1002;
    private int idArtista;
    private EditText etNombre;
    private Spinner spCategoria;
    private ImageView ivFoto;
    private TextView tvArchivo, tvDuracion;
    private Uri uriAudio, uriFoto;
    private String duracionStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_cancion);

        idArtista = getIntent().getIntExtra("EXTRA_ID_ARTISTA", -1);
        etNombre    = findViewById(R.id.etNombreCancion);
        spCategoria = findViewById(R.id.spCategoria);
        ivFoto      = findViewById(R.id.ivPortada);
        tvArchivo   = findViewById(R.id.tvArchivoSeleccionado);
        tvDuracion  = findViewById(R.id.tvDuracion);

        findViewById(R.id.btnSeleccionarFoto).setOnClickListener(v -> pickImage());
        findViewById(R.id.btnSeleccionarAudio).setOnClickListener(v -> pickAudio());
        findViewById(R.id.btnConfirmar).setOnClickListener(v -> subir());

        cargarCategorias();
    }

    private void cargarCategorias() {
        ApiCliente.getClient(this)
                .create(ServicioCancion.class)
                .obtenerCategorias()
                .enqueue(new Callback<RespuestaCliente<List<CategoriaMusicalDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<List<CategoriaMusicalDTO>>> call,
                                           Response<RespuestaCliente<List<CategoriaMusicalDTO>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<CategoriaMusicalDTO> list = response.body().getDatos();
                            spCategoria.setAdapter(new ArrayAdapter<>(
                                    SubirCancionActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    list
                            ));
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<List<CategoriaMusicalDTO>>> call, Throwable t) {
                        Toast.makeText(SubirCancionActivity.this,
                                "Error de red cargando categorías",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_IMAGE);
    }

    private void pickAudio() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, REQ_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        getContentResolver().takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );
        if (requestCode == REQ_IMAGE) {
            uriFoto = uri;
            ivFoto.setImageURI(uri);
        } else {
            uriAudio = uri;
            try (Cursor c = getContentResolver().query(uri,
                    new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE},
                    null, null, null)) {
                if (c != null && c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                    long size = c.getLong(c.getColumnIndexOrThrow(OpenableColumns.SIZE));
                    if (size > 20 * 1024 * 1024) {
                        Toast.makeText(this, ">20MB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tvArchivo.setText(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long ms = Long.parseLong(duration);
            duracionStr = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(ms),
                    TimeUnit.MILLISECONDS.toSeconds(ms) % 60);
            tvDuracion.setText(duracionStr);
        }
    }

    private void subir() {
        String nombre = etNombre.getText().toString().trim();
        CategoriaMusicalDTO cat = (CategoriaMusicalDTO) spCategoria.getSelectedItem();
        if (nombre.isEmpty() || uriAudio == null || uriFoto == null || cat == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] audioBytes = readAllBytes(uriAudio);
            byte[] fotoBytes = readAllBytes(uriFoto);

            RequestBody rbNombre = RequestBody.create(
                    MediaType.parse("text/plain"), nombre);
            RequestBody rbCatId = RequestBody.create(
                    MediaType.parse("text/plain"),
                    String.valueOf(cat.getIdCategoriaMusical()));
            RequestBody rbDur = RequestBody.create(
                    MediaType.parse("text/plain"), duracionStr);
            RequestBody rbAlbum = RequestBody.create(
                    MediaType.parse("text/plain"), "0");
            RequestBody rbPos = RequestBody.create(
                    MediaType.parse("text/plain"), "0");

            List<RequestBody> rbIds = new ArrayList<>();
            for (Integer art : Collections.singletonList(idArtista)) {
                rbIds.add(RequestBody.create(
                        MediaType.parse("text/plain"), art.toString()));
            }

            MultipartBody.Part partAudio = MultipartBody.Part.createFormData(
                    "archivoCancion",
                    "cancion.mp3",
                    RequestBody.create(
                            MediaType.parse("audio/mpeg"), audioBytes
                    )
            );
            MultipartBody.Part partFoto = MultipartBody.Part.createFormData(
                    "urlFoto",
                    "portada.jpg",
                    RequestBody.create(
                            MediaType.parse("image/jpeg"), fotoBytes
                    )
            );

            ServicioCancion srv = ApiCliente.getClient(this)
                    .create(ServicioCancion.class);
            srv.subirCancion(
                    rbNombre, rbCatId,
                    partAudio, partFoto,
                    rbDur, rbAlbum, rbPos,
                    rbIds
            ).enqueue(new Callback<RespuestaCliente<String>>() {
                @Override
                public void onResponse(Call<RespuestaCliente<String>> call,
                                       Response<RespuestaCliente<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SubirCancionActivity.this,
                                "OK: " + response.body().getMensaje(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SubirCancionActivity.this,
                                "Error HTTP " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaCliente<String>> call, Throwable t) {
                    Toast.makeText(SubirCancionActivity.this,
                            "Fallo red", Toast.LENGTH_SHORT).show();
                    Log.e("SubirCancion", t.toString());
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error preparando archivo", Toast.LENGTH_SHORT).show();
        }
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
}
