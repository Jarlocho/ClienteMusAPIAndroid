package com.example.musapiapp.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioContenido;
import com.example.musapiapp.util.Preferencias;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearAlbumActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1001;
    private ImageView ivPortada;
    private Uri portadaUri = null;
    private EditText etNombre;
    private Button btnSubirFoto, btnGuardar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_album);

        ivPortada    = findViewById(R.id.ivPortada);
        etNombre     = findViewById(R.id.etNombreAlbum);
        btnSubirFoto = findViewById(R.id.btnSubirFoto);
        btnGuardar   = findViewById(R.id.btnGuardarAlbum);
        btnCancelar  = findViewById(R.id.btnCancelarAlbum);
        ImageButton btnVolver = findViewById(R.id.btnVolverCrear);

        btnVolver.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());

        btnSubirFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnGuardar.setOnClickListener(v -> crearAlbum());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            portadaUri = data.getData();
            ivPortada.setImageURI(portadaUri);
        }
    }

    private void crearAlbum() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("Requerido");
            return;
        }

        // 1) Part para el nombre
        RequestBody nombrePart =
                RequestBody.create(MediaType.parse("text/plain"), nombre);

        // 2) Part para la foto (si la hay)
        MultipartBody.Part fotoPart = null;
        if (portadaUri != null) {
            try {
                File file = copyUriToFile(portadaUri);
                RequestBody reqFile =
                        RequestBody.create(MediaType.parse("image/*"), file);
                fotoPart = MultipartBody.Part.createFormData(
                        "foto", file.getName(), reqFile);
            } catch (Exception e) {
                Toast.makeText(this,
                        "No se pudo procesar la imagen", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 3) Llamada Retrofit
        ServicioContenido srv = ApiCliente
                .getClient(this)
                .create(ServicioContenido.class);

        srv.crearAlbum(
                "Bearer " + Preferencias.obtenerToken(this),
                nombrePart, fotoPart
        ).enqueue(new Callback<RespuestaCliente<Void>>() {
            @Override
            public void onResponse(Call<RespuestaCliente<Void>> call,
                                   Response<RespuestaCliente<Void>> resp) {
                if (resp.isSuccessful()) {
                    Toast.makeText(CrearAlbumActivity.this,
                            "Álbum creado!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearAlbumActivity.this,
                            "Error: " + resp.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RespuestaCliente<Void>> call, Throwable t) {
                Toast.makeText(CrearAlbumActivity.this,
                        "Fallo de red: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Copia el contenido de la Uri a un archivo en cache y lo devuelve.
     */
    private File copyUriToFile(Uri uri) throws Exception {
        ContentResolver cr = getContentResolver();
        String filename = queryFileName(uri);
        File outFile = new File(getCacheDir(), filename);
        try (InputStream in = cr.openInputStream(uri);
             FileOutputStream out = new FileOutputStream(outFile)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        return outFile;
    }

    /**
     * Obtiene el nombre original del fichero de la Uri, o un fallback.
     */
    private String queryFileName(Uri uri) {
        String result = "tempfile";
        try (android.database.Cursor cursor =
                     getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) result = cursor.getString(idx);
            }
        }
        return result;
    }
}
