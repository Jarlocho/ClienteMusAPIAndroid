package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioRegistros;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaMusicalActivity extends AppCompatActivity {
    private ServicioRegistros srv;
    private EditText etDescripcion, etNombre;
    private TextView tvTitulo;
    private int categoriaEditandoId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_musical);

        ImageButton btnVolver = findViewById(R.id.btnVolver);
        tvTitulo = findViewById(R.id.tvTitulo);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);

        btnVolver.setOnClickListener(v -> finish());

        // Detectamos si es edición
        Intent intent = getIntent();
        boolean esEdicion = intent.getBooleanExtra("MODO_EDICION", false);

        if (esEdicion) {
            tvTitulo.setText("Editar categoría musical");
            etNombre.setText(intent.getStringExtra("CATEGORIA_NOMBRE"));
            etDescripcion.setText(intent.getStringExtra("CATEGORIA_DESC"));
            categoriaEditandoId = intent.getIntExtra("CATEGORIA_ID", -1);
        } else {
            tvTitulo.setText("Registrar categoría musical");
        }

        btnConfirmar.setOnClickListener(v -> {
            if (validarCampos()) {
                if (esEdicion) {
                    actualizarCategoria(); // nuevo método
                } else {
                    registrarCategoria();  // ya existente
                }
            } else {
                Toast.makeText(this,
                        "Por favor, completa todos los campos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void actualizarCategoria() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        CategoriaMusicalDTO categoria = new CategoriaMusicalDTO();
        categoria.setIdCategoriaMusical(categoriaEditandoId);  // Aquí el ID ya es válido
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);

        ServicioRegistros srv = ApiCliente.getClient(this).create(ServicioRegistros.class);

        // Suponiendo que tu API tenga un endpoint PUT /categorias/{id}
        Call<CategoriaMusicalDTO> call = srv.actualizarCategoria(categoriaEditandoId, categoria);
        call.enqueue(new Callback<CategoriaMusicalDTO>() {
            @Override
            public void onResponse(Call<CategoriaMusicalDTO> call, Response<CategoriaMusicalDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriaMusicalActivity.this,
                            "Categoría actualizada con éxito", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CategoriaMusicalActivity.this,
                            "Error al actualizar la categoría", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriaMusicalDTO> call, Throwable t) {
                Toast.makeText(CategoriaMusicalActivity.this,
                        "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registrarCategoria() {
        // Obtenemos los valores
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Creamos el objeto DTO
        CategoriaMusicalDTO categoria = new CategoriaMusicalDTO();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setIdCategoriaMusical(null);

        // Inicializamos Retrofit
        ServicioRegistros srv = ApiCliente.getClient(this).create(ServicioRegistros.class);

        // Llamamos al endpoint
        Call<CategoriaMusicalDTO> call = srv.registrarCategoriaMusical(categoria);
        call.enqueue(new Callback<CategoriaMusicalDTO>() {
            @Override
            public void onResponse(Call<CategoriaMusicalDTO> call, Response<CategoriaMusicalDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriaMusicalActivity.this, "Categoría registrada con éxito", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Para que MenuAdminActivity sepa que debe refrescar
                    finish();

                } else {
                    Toast.makeText(CategoriaMusicalActivity.this, "Error al registrar la categoría", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriaMusicalDTO> call, Throwable t) {
                Toast.makeText(CategoriaMusicalActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private boolean validarCampos() {
        boolean okDescripcion = true;
        boolean okNombre = true;
        if (etDescripcion.getText().toString().trim().isEmpty()) {
            etDescripcion.setError("La descripción es requerida");
            okDescripcion = false;
        }
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("El nombre es requerido");
            okNombre = false;
        }
        if (okDescripcion && okNombre) {
            return true;
        }
        else  {
            return false;
        }
    }
}