package com.example.musapiapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        inputCorreo = findViewById(R.id.inputCorreo);
        inputContrasena = findViewById(R.id.inputContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });
        Button btnIrARegistro = findViewById(R.id.btnIrARegistro);

        btnIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivityForResult(intent, 1);
        });

    }

    private void iniciarSesion() {
        String correo = inputCorreo.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SolicitudInicioSesion solicitud = new SolicitudInicioSesion(correo, contrasena);

        ServicioUsuario servicio = ApiCliente.getClient(MainActivity.this).create(ServicioUsuario.class);
        Call<RespuestaCliente<UsuarioDTO>> llamada = servicio.iniciarSesion(solicitud);

        llamada.enqueue(new Callback<RespuestaCliente<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<RespuestaCliente<UsuarioDTO>> call, Response<RespuestaCliente<UsuarioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioDTO usuario = response.body().getDatos();
                    Preferencias.guardarToken(MainActivity.this, usuario.getToken());
                    Toast.makeText(MainActivity.this, "Bienvenido " + usuario.getNombreUsuario(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaCliente<UsuarioDTO>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("registro_exitoso", false)) {
                Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show();
            }
        }
    }


}
