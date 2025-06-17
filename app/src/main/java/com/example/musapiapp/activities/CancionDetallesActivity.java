package com.example.musapiapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musapiapp.R;

public class CancionDetallesActivity extends AppCompatActivity {
    public static final String EXTRA_CANCION = "EXTRA_CANCION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancion_detalles);
        // TODO: implementa aquí el UI de detalles de canción
    }
}
