package com.example.musapiapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.EdicionPerfilDTO;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.model.Pais;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    private ImageButton btnVolver;
    private ImageView  ivFoto;
    private Button     btnSubirFoto, btnConfirmar;
    private EditText   etNombre, etDescripcion;
    private Spinner    spinnerPais;
    private TextView   labelDescripcion;

    private EdicionPerfilDTO edicion = new EdicionPerfilDTO();
    private boolean          esArtista;
    private int              idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // referencias UI
        btnVolver        = findViewById(R.id.btnVolver);
        ivFoto           = findViewById(R.id.ivFoto);
        btnSubirFoto     = findViewById(R.id.btnSubirFoto);
        etNombre         = findViewById(R.id.etNombre);
        labelDescripcion = findViewById(R.id.labelDescripcion);
        etDescripcion    = findViewById(R.id.etDescripcion);
        spinnerPais      = findViewById(R.id.spinnerPais);
        btnConfirmar     = findViewById(R.id.btnConfirmar);

        // vuelve atrás
        btnVolver.setOnClickListener(v -> finish());

        // cargo usuario de SharedPreferences
        String jsonUser = Preferencias.recuperarUsuarioJson(this);
        if (jsonUser != null) {
            UsuarioDTO u = new Gson().fromJson(jsonUser, UsuarioDTO.class);
            idUsuario    = u.getIdUsuario();
            etNombre.setText(u.getNombre());
            edicion.setNombre(u.getNombre());
            esArtista = u.isEsArtista();
            if (!esArtista) {
                labelDescripcion.setVisibility(View.GONE);
                etDescripcion.setVisibility(View.GONE);
                ivFoto.setVisibility(View.GONE);
                btnSubirFoto.setVisibility(View.GONE);
            } else {
                labelDescripcion.setVisibility(View.VISIBLE);
                etDescripcion.setVisibility(View.VISIBLE);
                ivFoto.setVisibility(View.VISIBLE);
                btnSubirFoto.setVisibility(View.VISIBLE);

                // Obtener token guardado
                String token = Preferencias.obtenerToken(this);
                String bearer = token != null ? "Bearer " + token : "";

                ServicioUsuario srv = ApiCliente.getClient(this)
                        .create(ServicioUsuario.class);

                srv.obtenerArtistaPorId(
                        bearer,
                        idUsuario  // <- ID del artista a buscar
                ).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                // Parsear respuesta manualmente
                                String responseBody = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseBody);
                                JSONObject datos = jsonObject.getJSONObject("datos");

                                // Mapear a BusquedaArtistaDTO
                                BusquedaArtistaDTO artista = new BusquedaArtistaDTO();
                                artista.setIdArtista(datos.getInt("idArtista"));
                                artista.setNombre(datos.getString("nombre"));
                                artista.setNombreUsuario(datos.getString("nombreUsuario"));
                                artista.setDescripcion(datos.getString("descripcion"));
                                artista.setUrlFoto(datos.getString("urlFoto"));

                                // Mostrar datos en UI
                                runOnUiThread(() -> {
                                    cargarImagen(artista.getUrlFoto(), ivFoto);

                                    etDescripcion.setText(artista.getDescripcion());
                                });

                            }catch (Exception e) {
                                runOnUiThread(() ->
                                        Toast.makeText(EditarPerfilActivity.this,
                                                "Error al parsear respuesta",
                                                Toast.LENGTH_SHORT).show()
                                );
                                Log.e("API", "Parse error: " + e.getMessage());
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                runOnUiThread(() ->
                                        Toast.makeText(EditarPerfilActivity.this,
                                                "Error: " + response.code() + " - " + errorBody,
                                                Toast.LENGTH_SHORT).show()
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        runOnUiThread(() ->
                                Toast.makeText(EditarPerfilActivity.this,
                                        "Error de conexión: " + t.getMessage(),
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                });

            }
        }

        // lista completa de países (igual que en registro), ordenada
        List<Pais> paises = Arrays.asList(
                new Pais("AF", "Afganistán"),
                new Pais("AL", "Albania"),
                new Pais("DE", "Alemania"),
                new Pais("AD", "Andorra"),
                new Pais("AO", "Angola"),
                new Pais("AG", "Antigua y Barbuda"),
                new Pais("SA", "Arabia Saudita"),
                new Pais("DZ", "Argelia"),
                new Pais("AR", "Argentina"),
                new Pais("AM", "Armenia"),
                new Pais("AU", "Australia"),
                new Pais("AT", "Austria"),
                new Pais("AZ", "Azerbaiyán"),
                new Pais("BS", "Bahamas"),
                new Pais("BD", "Bangladés"),
                new Pais("BB", "Barbados"),
                new Pais("BH", "Baréin"),
                new Pais("BE", "Bélgica"),
                new Pais("BZ", "Belice"),
                new Pais("BJ", "Benín"),
                new Pais("BY", "Bielorrusia"),
                new Pais("BO", "Bolivia"),
                new Pais("BA", "Bosnia y Herzegovina"),
                new Pais("BW", "Botsuana"),
                new Pais("BR", "Brasil"),
                new Pais("BN", "Brunéi"),
                new Pais("BG", "Bulgaria"),
                new Pais("BF", "Burkina Faso"),
                new Pais("BI", "Burundi"),
                new Pais("BT", "Bután"),
                new Pais("CV", "Cabo Verde"),
                new Pais("KH", "Camboya"),
                new Pais("CM", "Camerún"),
                new Pais("CA", "Canadá"),
                new Pais("QA", "Catar"),
                new Pais("TD", "Chad"),
                new Pais("CL", "Chile"),
                new Pais("CN", "China"),
                new Pais("CY", "Chipre"),
                new Pais("CO", "Colombia"),
                new Pais("KM", "Comoras"),
                new Pais("KP", "Corea del Norte"),
                new Pais("KR", "Corea del Sur"),
                new Pais("CI", "Costa de Marfil"),
                new Pais("CR", "Costa Rica"),
                new Pais("HR", "Croacia"),
                new Pais("CU", "Cuba"),
                new Pais("DK", "Dinamarca"),
                new Pais("DM", "Dominica"),
                new Pais("EC", "Ecuador"),
                new Pais("EG", "Egipto"),
                new Pais("SV", "El Salvador"),
                new Pais("AE", "Emiratos Árabes Unidos"),
                new Pais("ER", "Eritrea"),
                new Pais("SK", "Eslovaquia"),
                new Pais("SI", "Eslovenia"),
                new Pais("ES", "España"),
                new Pais("US", "Estados Unidos"),
                new Pais("EE", "Estonia"),
                new Pais("SZ", "Esuatini"),
                new Pais("ET", "Etiopía"),
                new Pais("PH", "Filipinas"),
                new Pais("FI", "Finlandia"),
                new Pais("FJ", "Fiyi"),
                new Pais("FR", "Francia"),
                new Pais("GA", "Gabón"),
                new Pais("GM", "Gambia"),
                new Pais("GE", "Georgia"),
                new Pais("GH", "Ghana"),
                new Pais("GD", "Granada"),
                new Pais("GR", "Grecia"),
                new Pais("GT", "Guatemala"),
                new Pais("GN", "Guinea"),
                new Pais("GW", "Guinea-Bisáu"),
                new Pais("GQ", "Guinea Ecuatorial"),
                new Pais("GY", "Guyana"),
                new Pais("HT", "Haití"),
                new Pais("HN", "Honduras"),
                new Pais("HU", "Hungría"),
                new Pais("IN", "India"),
                new Pais("ID", "Indonesia"),
                new Pais("IQ", "Irak"),
                new Pais("IR", "Irán"),
                new Pais("IE", "Irlanda"),
                new Pais("IS", "Islandia"),
                new Pais("IL", "Israel"),
                new Pais("IT", "Italia"),
                new Pais("JM", "Jamaica"),
                new Pais("JP", "Japón"),
                new Pais("JO", "Jordania"),
                new Pais("KZ", "Kazajistán"),
                new Pais("KE", "Kenia"),
                new Pais("KG", "Kirguistán"),
                new Pais("KI", "Kiribati"),
                new Pais("KW", "Kuwait"),
                new Pais("LA", "Laos"),
                new Pais("LS", "Lesoto"),
                new Pais("LV", "Letonia"),
                new Pais("LB", "Líbano"),
                new Pais("LR", "Liberia"),
                new Pais("LY", "Libia"),
                new Pais("LI", "Liechtenstein"),
                new Pais("LT", "Lituania"),
                new Pais("LU", "Luxemburgo"),
                new Pais("MK", "Macedonia del Norte"),
                new Pais("MG", "Madagascar"),
                new Pais("MY", "Malasia"),
                new Pais("MW", "Malaui"),
                new Pais("MV", "Maldivas"),
                new Pais("ML", "Mali"),
                new Pais("MT", "Malta"),
                new Pais("MA", "Marruecos"),
                new Pais("MU", "Mauricio"),
                new Pais("MR", "Mauritania"),
                new Pais("MX", "México"),
                new Pais("FM", "Micronesia"),
                new Pais("MD", "Moldavia"),
                new Pais("MC", "Mónaco"),
                new Pais("MN", "Mongolia"),
                new Pais("ME", "Montenegro"),
                new Pais("MZ", "Mozambique"),
                new Pais("MM", "Birmania"),
                new Pais("NA", "Namibia"),
                new Pais("NR", "Nauru"),
                new Pais("NP", "Nepal"),
                new Pais("NI", "Nicaragua"),
                new Pais("NE", "Níger"),
                new Pais("NG", "Nigeria"),
                new Pais("NO", "Noruega"),
                new Pais("NZ", "Nueva Zelanda"),
                new Pais("OM", "Omán"),
                new Pais("NL", "Países Bajos"),
                new Pais("PK", "Pakistán"),
                new Pais("PW", "Palaos"),
                new Pais("PA", "Panamá"),
                new Pais("PG", "Papúa Nueva Guinea"),
                new Pais("PY", "Paraguay"),
                new Pais("PE", "Perú"),
                new Pais("PL", "Polonia"),
                new Pais("PT", "Portugal"),
                new Pais("GB", "Reino Unido"),
                new Pais("CF", "República Centroafricana"),
                new Pais("CZ", "República Checa"),
                new Pais("CG", "República del Congo"),
                new Pais("CD", "República Democrática del Congo"),
                new Pais("DO", "República Dominicana"),
                new Pais("RW", "Ruanda"),
                new Pais("RO", "Rumania"),
                new Pais("RU", "Rusia"),
                new Pais("WS", "Samoa"),
                new Pais("KN", "San Cristóbal y Nieves"),
                new Pais("SM", "San Marino"),
                new Pais("VC", "San Vicente y las Granadinas"),
                new Pais("LC", "Santa Lucía"),
                new Pais("ST", "Santo Tomé y Príncipe"),
                new Pais("SN", "Senegal"),
                new Pais("RS", "Serbia"),
                new Pais("SC", "Seychelles"),
                new Pais("SL", "Sierra Leona"),
                new Pais("SG", "Singapur"),
                new Pais("SY", "Siria"),
                new Pais("SO", "Somalia"),
                new Pais("LK", "Sri Lanka"),
                new Pais("ZA", "Sudáfrica"),
                new Pais("SD", "Sudán"),
                new Pais("SS", "Sudán del Sur"),
                new Pais("SE", "Suecia"),
                new Pais("CH", "Suiza"),
                new Pais("SR", "Surinam"),
                new Pais("TH", "Tailandia"),
                new Pais("TZ", "Tanzania"),
                new Pais("TJ", "Tayikistán"),
                new Pais("TL", "Timor Oriental"),
                new Pais("TG", "Togo"),
                new Pais("TO", "Tonga"),
                new Pais("TT", "Trinidad y Tobago"),
                new Pais("TN", "Túnez"),
                new Pais("TM", "Turkmenistán"),
                new Pais("TR", "Turquía"),
                new Pais("TV", "Tuvalu"),
                new Pais("UA", "Ucrania"),
                new Pais("UG", "Uganda"),
                new Pais("UY", "Uruguay"),
                new Pais("UZ", "Uzbekistán"),
                new Pais("VU", "Vanuatu"),
                new Pais("VE", "Venezuela"),
                new Pais("VN", "Vietnam"),
                new Pais("YE", "Yemen"),
                new Pais("DJ", "Yibuti"),
                new Pais("ZM", "Zambia"),
                new Pais("ZW", "Zimbabue")        );
        Collator coll = Collator.getInstance(new Locale("es","ES"));
        coll.setStrength(Collator.PRIMARY);
        Collections.sort(paises, (p1,p2)-> coll.compare(p1.getNombre(), p2.getNombre()));
        ArrayAdapter<Pais> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, paises
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);

        // elegir foto de galería
        btnSubirFoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pick.setType("image/*");
            startActivityForResult(pick, PICK_IMAGE);
        });

        // confirmar edición
        btnConfirmar.setOnClickListener(v -> {
            if (validarCampos()) {
                enviarEdicionAlServidor();
            } else {
                Toast.makeText(this,
                        R.string.error_campos_invalidos,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // resuelvo URI -> ruta real en disco
    private String realPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{MediaStore.Images.Media.DATA},
                null,null,null
        );
        if (cursor == null) return null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(idx);
        cursor.close();
        return path;
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
                    ApiCliente apiCliente = new ApiCliente();
                    URL url = new URL( apiCliente.getUrlArchivos()+ urlImagen);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String token = Preferencias.obtenerToken(EditarPerfilActivity.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==PICK_IMAGE && resultCode==Activity.RESULT_OK && data!=null && data.getData()!=null) {
            Uri uri = data.getData();
            ivFoto.setImageURI(uri);
            edicion.setFotoPath(realPathFromUri(uri));
        }
    }

    private boolean validarCampos() {
        boolean ok = true;
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError(getString(R.string.error_nombre_vacio));
            ok = false;
        } else {
            edicion.setNombre(nombre);
        }

        Pais sel = (Pais)spinnerPais.getSelectedItem();
        if (sel==null) {
            Toast.makeText(this,
                    R.string.error_pais_requerido,
                    Toast.LENGTH_SHORT).show();
            ok = false;
        } else {
            edicion.setPais(sel.getCodigo());
        }

        if (esArtista) {
            edicion.setDescripcion(etDescripcion.getText().toString().trim());
        }
        return ok;
    }

    private void enviarEdicionAlServidor() {
        // construyo los RequestBody para texto
        RequestBody rbNombre = RequestBody.create(
                MediaType.parse("text/plain"), edicion.getNombre()
        );
        RequestBody rbNombreUsuario = RequestBody.create(
                MediaType.parse("text/plain"), /* si no cambiaslo o mismo preferencia */
                new Gson().fromJson(Preferencias.recuperarUsuarioJson(this), UsuarioDTO.class).getNombreUsuario()
        );
        RequestBody rbPais = RequestBody.create(
                MediaType.parse("text/plain"), edicion.getPais()
        );
        RequestBody rbDesc = RequestBody.create(
                MediaType.parse("text/plain"),
                edicion.getDescripcion() == null ? "" : edicion.getDescripcion()
        );

        // preparo foto multipart
        MultipartBody.Part parteFoto = null;
        if (edicion.getFotoPath() != null) {
            File fotoFile = new File(edicion.getFotoPath());
            RequestBody rbFoto = RequestBody.create(
                    MediaType.parse("image/*"), fotoFile
            );
            parteFoto = MultipartBody.Part.createFormData(
                    "foto", fotoFile.getName(), rbFoto
            );
        }

        // obtengo token guardado
        String token = Preferencias.obtenerToken(this);
        String bearer = token != null ? "Bearer " + token : "";

        ServicioUsuario srv = ApiCliente.getClient(this)
                .create(ServicioUsuario.class);

        srv.editarPerfil(
                bearer,
                idUsuario,
                rbNombre,
                rbNombreUsuario,
                rbPais,
                rbDesc,
                parteFoto
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    // 1) Actualizar SharedPreferences con los nuevos datos
                    UsuarioDTO u = new Gson().fromJson(
                            Preferencias.recuperarUsuarioJson(EditarPerfilActivity.this),
                            UsuarioDTO.class
                    );
                    u.setNombre(edicion.getNombre());
                    u.setPais(edicion.getPais());
                    Preferencias.guardarUsuarioJson(
                            EditarPerfilActivity.this,
                            new Gson().toJson(u)
                    );

                    // 2) Mostrar feedback
                    Toast.makeText(EditarPerfilActivity.this,
                            R.string.perfil_editado_ok,
                            Toast.LENGTH_SHORT).show();

                    // 3) Avisar a la actividad que lanzó esta (PerfilUsuarioActivity)
                    setResult(RESULT_OK);

                    // 4) Cerrar tras breve delay para que se vea el Toast
                    btnConfirmar.postDelayed(() -> finish(), 500);
                } else {
                    Toast.makeText(EditarPerfilActivity.this,
                            R.string.error_servidor,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditarPerfilActivity.this,
                        R.string.error_conexion,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
