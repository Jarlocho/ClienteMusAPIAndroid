package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.model.Pais;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.UsuarioRegistro;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText inputNombreUsuario, inputCorreo, inputContrasena;
    private Spinner spinnerPais;
    private Button btnRegistrar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inputNombreUsuario = findViewById(R.id.inputNombreUsuario);
        inputCorreo        = findViewById(R.id.inputCorreo);
        inputContrasena    = findViewById(R.id.inputContrasena);
        spinnerPais        = findViewById(R.id.spinnerPais);
        btnRegistrar       = findViewById(R.id.btnRegistrar);
        btnCancelar        = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> finish());

        List<Pais> paises = new ArrayList<>(Arrays.asList(
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
                new Pais("ZW", "Zimbabue")
        ));

        Collator collator = Collator.getInstance(new Locale("es", "ES"));
        collator.setStrength(Collator.PRIMARY);
        Collections.sort(paises, (p1, p2) ->
                collator.compare(p1.getNombre(), p2.getNombre())
        );
        ArrayAdapter<Pais> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paises
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);

        ImageView imgLogo = findViewById(R.id.imgLogo);
        imgLogo.animate()
                .alpha(1f)
                .setDuration(2500)
                .start();

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String usuarioText = inputNombreUsuario.getText().toString().trim();
        String correo      = inputCorreo.getText().toString().trim();
        String contrasena  = inputContrasena.getText().toString().trim();
        Pais paisObj       = (Pais) spinnerPais.getSelectedItem();

        if (usuarioText.isEmpty() || correo.isEmpty() ||
                contrasena.isEmpty() || paisObj == null) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioRegistro nuevoUsuario = new UsuarioRegistro(
                usuarioText,
                usuarioText,
                correo,
                paisObj.getCodigo(),
                contrasena
        );

        ServicioUsuario servicio = ApiCliente
                .getClient(this)
                .create(ServicioUsuario.class);

        servicio.registrarUsuario(nuevoUsuario).enqueue(new Callback<RespuestaCliente<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<RespuestaCliente<UsuarioDTO>> call,
                                   Response<RespuestaCliente<UsuarioDTO>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDatos() != null) {

                    // 1) Obtén el DTO
                    UsuarioDTO u = response.body().getDatos();

                    // 2) Guarda token y usuario JSON
                    Preferencias.guardarToken(RegistroActivity.this, u.getToken());
                    String usuarioJson = new Gson().toJson(u);
                    Preferencias.guardarUsuarioJson(RegistroActivity.this, usuarioJson);

                    // 3) Ahora sí navega al menú principal
                    Intent intent = new Intent(RegistroActivity.this, MenuPrincipalActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = "Código: " + response.code();
                    try {
                        msg += "\n" + response.errorBody().string();
                    } catch (IOException e) {
                        msg += "\nError leyendo respuesta";
                    }
                    Toast.makeText(RegistroActivity.this,
                            "Error al registrar:\n" + msg,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaCliente<UsuarioDTO>> call, Throwable t) {
                Toast.makeText(RegistroActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
