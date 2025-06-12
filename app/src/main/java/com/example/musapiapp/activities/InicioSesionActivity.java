package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.SolicitudInicioSesion;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesionActivity extends AppCompatActivity {

    private EditText inputCorreoLogin;
    private EditText inputContrasenaLogin;
    private Button btnIniciarSesion;
    private Button btnIrARegistro;
    private Button btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputCorreoLogin    = findViewById(R.id.inputCorreoLogin);
        inputContrasenaLogin= findViewById(R.id.inputContrasenaLogin);
        btnIniciarSesion    = findViewById(R.id.btnIniciarSesion);
        btnIrARegistro      = findViewById(R.id.btnIrARegistro);
        btnSalir            = findViewById(R.id.btnSalir);

        ImageView imgLogo = findViewById(R.id.imgLogoLogin);
        imgLogo.animate().alpha(1f).setDuration(2500).start();

        btnIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesionActivity.this, RegistroActivity.class);
            startActivityForResult(intent, 1);
        });

        btnSalir.setOnClickListener(v -> finish());

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = inputCorreoLogin.getText().toString().trim();
        String contr = inputContrasenaLogin.getText().toString().trim();

        if (correo.isEmpty() || contr.isEmpty()) {
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        ServicioUsuario servicio = ApiCliente.getClient(this)
                .create(ServicioUsuario.class);
        servicio.iniciarSesion(new SolicitudInicioSesion(correo, contr))
                .enqueue(new Callback<RespuestaCliente<UsuarioDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente<UsuarioDTO>> call,
                                           Response<RespuestaCliente<UsuarioDTO>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            UsuarioDTO u = response.body().getDatos();

                            Preferencias.guardarToken(InicioSesionActivity.this, u.getToken());
                            Toast.makeText(InicioSesionActivity.this,
                                    "Bienvenido " + u.getNombreUsuario(),
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(InicioSesionActivity.this, MenuPrincipalActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(InicioSesionActivity.this,
                                    "Credenciales incorrectas",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<UsuarioDTO>> call,
                                          Throwable t) {
                        Toast.makeText(InicioSesionActivity.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null
                && data.getBooleanExtra("registro_exitoso", false)) {
            Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show();
        }
    }
}
