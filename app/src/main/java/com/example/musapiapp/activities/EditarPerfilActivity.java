// src/main/java/com/example/musapiapp/activities/EditarPerfilActivity.java
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
import com.example.musapiapp.dto.EdicionPerfilDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.model.Pais;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

        btnVolver.setOnClickListener(v -> finish());

        // Cargo datos del usuario de prefs
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
                // Sólo para artista: bajamos su perfil completo vía API
                cargarDatosArtista();
            }
        }

        // Spinner de países (igual que en registro)…
        setupSpinnerPaises();

        // Elegir foto de galería
        btnSubirFoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pick.setType("image/*");
            startActivityForResult(pick, PICK_IMAGE);
        });

        // Confirmar edición
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

    private void cargarDatosArtista() {
        String token  = Preferencias.obtenerToken(this);
        String bearer = token != null ? "Bearer " + token : "";

        ServicioUsuario srv = ApiCliente.getClient(this)
                .create(ServicioUsuario.class);

        srv.obtenerPerfilArtista(bearer, idUsuario)
                .enqueue(new Callback<RespuestaCliente<BusquedaArtistaDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<BusquedaArtistaDTO>> call,
                                           Response<RespuestaCliente<BusquedaArtistaDTO>> resp) {
                        if (resp.isSuccessful()
                                && resp.body() != null
                                && resp.body().getDatos() != null) {
                            BusquedaArtistaDTO artista = resp.body().getDatos();
                            // mostramos descripción e imagen
                            etDescripcion.setText(artista.getDescripcion());
                            cargarImagen(artista.getUrlFoto(), ivFoto);
                            edicion.setDescripcion(artista.getDescripcion());
                            edicion.setFotoPath(null); // se mantiene la original hasta cambiar
                        } else {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Error al cargar perfil artista",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<BusquedaArtistaDTO>> call,
                                          Throwable t) {
                        Toast.makeText(EditarPerfilActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSpinnerPaises() {
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
    }

    @SuppressLint("StaticFieldLeak")
    private void cargarImagen(String urlImagen, ImageView imageView) {
        if (urlImagen==null || urlImagen.isEmpty()) return;
        new AsyncTask<Void,Void,Bitmap>() {
            @Override protected Bitmap doInBackground(Void... voids) {
                try {
                    URL url = new URL(Constantes.URL_BASE + urlImagen);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    String token = Preferencias.obtenerToken(EditarPerfilActivity.this);
                    if (token!=null) conn.setRequestProperty("Authorization","Bearer "+token);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    in.close();
                    return bmp;
                } catch (Exception e) {
                    Log.e("EditarPerfil","cargarImagen",e);
                    return null;
                }
            }
            @Override protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) imageView.setImageBitmap(bmp);
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req,res,data);
        if (req==PICK_IMAGE && res==Activity.RESULT_OK && data!=null && data.getData()!=null) {
            Uri uri = data.getData();
            ivFoto.setImageURI(uri);
            edicion.setFotoPath(realPathFromUri(uri));
        }
    }

    private String realPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(
                uri, new String[]{MediaStore.Images.Media.DATA},
                null,null,null
        );
        if (cursor==null) return null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(idx);
        cursor.close();
        return path;
    }

    private boolean validarCampos() {
        boolean ok = true;
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre no puede estar vacío");
            ok = false;
        } else {
            edicion.setNombre(nombre);
        }
        Pais sel = (Pais)spinnerPais.getSelectedItem();
        if (sel==null) {
            Toast.makeText(this,"Debes seleccionar un país",Toast.LENGTH_SHORT).show();
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
        RequestBody rbNombre = RequestBody.create(
                MediaType.parse("text/plain"), edicion.getNombre()
        );
        RequestBody rbNombreUsuario = RequestBody.create(
                MediaType.parse("text/plain"),
                new Gson().fromJson(
                        Preferencias.recuperarUsuarioJson(this), UsuarioDTO.class
                ).getNombreUsuario()
        );
        RequestBody rbPais = RequestBody.create(
                MediaType.parse("text/plain"), edicion.getPais()
        );
        RequestBody rbDesc = RequestBody.create(
                MediaType.parse("text/plain"),
                edicion.getDescripcion() == null ? "" : edicion.getDescripcion()
        );
        MultipartBody.Part parteFoto = null;
        if (edicion.getFotoPath()!=null) {
            File fotoFile = new File(edicion.getFotoPath());
            RequestBody rbFoto = RequestBody.create(
                    MediaType.parse("image/*"), fotoFile
            );
            parteFoto = MultipartBody.Part.createFormData(
                    "foto", fotoFile.getName(), rbFoto
            );
        }
        String token = Preferencias.obtenerToken(this);
        String bearer = token!=null ? "Bearer "+token : "";

        ServicioUsuario srv = ApiCliente.getClient(this)
                .create(ServicioUsuario.class);

        srv.editarPerfil(
                bearer, idUsuario,
                rbNombre, rbNombreUsuario, rbPais, rbDesc,
                parteFoto
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    // actualizar prefs…
                    setResult(RESULT_OK);
                    Toast.makeText(EditarPerfilActivity.this,
                            "Perfil editado ok", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarPerfilActivity.this,
                            "Error en servidor: "+resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditarPerfilActivity.this,
                        "Error de red: "+t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
