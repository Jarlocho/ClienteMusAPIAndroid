package com.example.musapiapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.SolicitudInicioSesion;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText inputCorreo, inputContrasena;
    Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgLogo = findViewById(R.id.imgLogoLogin);
        imgLogo.animate().alpha(1f).setDuration(2500).start();
        EditText inputCorreo = findViewById(R.id.inputCorreoLogin);
        EditText inputContrasena = findViewById(R.id.inputContrasenaLogin);
        Button btnIniciar = findViewById(R.id.btnIniciarSesion);
        Button btnRegistro = findViewById(R.id.btnIrARegistro);
        Button btnSalir = findViewById(R.id.btnSalir);

        imgLogo.animate().alpha(1f).setDuration(2500).start();

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivityForResult(intent, 1);
        });

        btnSalir.setOnClickListener(v -> finish());

        btnIniciar.setOnClickListener(v -> {
            String correo = inputCorreo.getText().toString().trim();
            String contr = inputContrasena.getText().toString().trim();

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
                            if (response.isSuccessful() && response.body()!=null && response.body().getDatos()!=null) {
                                UsuarioDTO u = response.body().getDatos();
                                Preferencias.guardarToken(MainActivity.this, u.getToken());
                                Toast.makeText(MainActivity.this,
                                        "Bienvenido " + u.getNombreUsuario(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Credenciales incorrectas",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<RespuestaCliente<UsuarioDTO>> call, Throwable t) {
                            Toast.makeText(MainActivity.this,
                                    "Error de conexión: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK) {
            if (data!=null && data.getBooleanExtra("registro_exitoso", false)) {
                Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show();
            }
        }
    }


}
