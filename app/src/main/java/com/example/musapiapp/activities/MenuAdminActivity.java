package com.example.musapiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioBusqueda;
import com.example.musapiapp.util.Reproductor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuAdminActivity extends AppCompatActivity {
    private LinearLayout llResultados;
    private ServicioBusqueda srv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        // --- Volver atrás ---
        ImageButton btnVolver = findViewById(R.id.btn_volver_busqueda);
        btnVolver.setOnClickListener(v -> onBackPressed());

        ImageButton btnRegistrar = findViewById(R.id.btn_Registrar);
        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdminActivity.this, CategoriaMusicalActivity.class);
            startActivityForResult(intent, 1001); // 1001 = código arbitrario
        });


        llResultados = findViewById(R.id.ll_resultados);

        srv = ApiCliente.getClient(this)
                .create(ServicioBusqueda.class);

        llResultados.removeAllViews();
        obtenerCategorias();
    }

    private void obtenerCategorias() {
        srv.obtenerCategorias().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaCliente<List<CategoriaMusicalDTO>>> c,
                                   @NonNull Response<RespuestaCliente<List<CategoriaMusicalDTO>>> r) {
                if (r.code() == 404) {
                    Toast.makeText(MenuAdminActivity.this,
                            "No se encontraron categorías musicales",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!r.isSuccessful()) {
                    logAndToast("Categorías", r);
                    return;
                }

                var body = r.body();
                if (body == null || body.getDatos() == null || body.getDatos().isEmpty()) {
                    Toast.makeText(MenuAdminActivity.this,
                            "No hay categorías disponibles",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Aquí procesamos las categorías encontradas
                for (CategoriaMusicalDTO categoria : body.getDatos()) {
                    View item = LayoutInflater.from(MenuAdminActivity.this)
                            .inflate(R.layout.item_categoria, llResultados, false);

                    TextView tvNombre = item.findViewById(R.id.tvNombre);
                    TextView tvDescripcion = item.findViewById(R.id.tvDescripcion);
                    View btnEditar = item.findViewById(R.id.btnEditar);
                    btnEditar.setOnClickListener(v -> {
                        Intent intent = new Intent(MenuAdminActivity.this, CategoriaMusicalActivity.class);
                        intent.putExtra("MODO_EDICION", true);  // bandera para saber si es edición
                        intent.putExtra("CATEGORIA_ID", categoria.getIdCategoriaMusical());
                        intent.putExtra("CATEGORIA_NOMBRE", categoria.getNombre());
                        intent.putExtra("CATEGORIA_DESC", categoria.getDescripcion());
                        startActivityForResult(intent, 1001);
                    });


                    tvNombre.setText(categoria.getNombre());
                    tvDescripcion.setText(categoria.getDescripcion());
                    /*
                    btnVer.setOnClickListener(v -> {
                        // Navegar a una actividad que muestre las canciones de esta categoría
                        Intent i = new Intent(BusquedaActivity.this, CancionesPorCategoriaActivity.class);
                        i.putExtra("CATEGORIA_ID", categoria.getId());
                        i.putExtra("CATEGORIA_NOMBRE", categoria.getNombre());
                        startActivity(i);
                    });
                    */

                    llResultados.addView(item);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaCliente<List<CategoriaMusicalDTO>>> c,
                                  @NonNull Throwable t) {
                Log.e("Categorías", "onFailure", t);
                Toast.makeText(MenuAdminActivity.this,
                        R.string.error_conexion, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private <T> void logAndToast(String tag, Response<RespuestaCliente<T>> r) {
        try { Log.e(tag, "Error " + r.code() + ": " + r.errorBody().string()); }
        catch (IOException ignored) {}
        Toast.makeText(this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Se acaba de registrar una categoría → refrescamos la lista
            llResultados.removeAllViews();
            obtenerCategorias();
        }
    }

}