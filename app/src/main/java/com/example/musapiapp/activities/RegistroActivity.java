package com.example.musapiapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.UsuarioRegistro;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioUsuario;
import android.content.Intent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    EditText inputNombre, inputNombreUsuario, inputCorreo, inputPais, inputContrasena;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inputNombre = findViewById(R.id.inputNombre);
        inputNombreUsuario = findViewById(R.id.inputNombreUsuario);
        inputCorreo = findViewById(R.id.inputCorreo);
        inputPais = findViewById(R.id.inputPais);
        inputContrasena = findViewById(R.id.inputContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = inputNombre.getText().toString().trim();
        String nombreUsuario = inputNombreUsuario.getText().toString().trim();
        String correo = inputCorreo.getText().toString().trim();
        String pais = inputPais.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString().trim();

        if (nombre.isEmpty() || nombreUsuario.isEmpty() || correo.isEmpty() || pais.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioRegistro nuevoUsuario = new UsuarioRegistro(
                nombre, nombreUsuario, correo, pais, contrasena
        );

        ServicioUsuario servicio = ApiCliente.getClient(this).create(ServicioUsuario.class);
        Call<RespuestaCliente<UsuarioDTO>> llamada = servicio.registrarUsuario(nuevoUsuario);

        llamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaCliente<UsuarioDTO>> call, Response<RespuestaCliente<UsuarioDTO>> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent();
                    intent.putExtra("registro_exitoso", true);
                    setResult(RESULT_OK, intent);
                    finish(); // Cierra esta pantalla y avisa a MainActivity

                } else {
                    Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaCliente<UsuarioDTO>> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
