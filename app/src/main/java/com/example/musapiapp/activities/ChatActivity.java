package com.example.musapiapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.MensajesChatDTO;
import com.example.musapiapp.util.Constantes;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_ID_ARTISTA     = "EXTRA_ID_ARTISTA";
    public static final String EXTRA_NOMBRE_USUARIO = "EXTRA_NOMBRE_USUARIO";

    private int    idArtista;
    private String nombreUsuario;

    private ScrollView   scrollMensajes;
    private LinearLayout llMensajes;
    private EditText     etMensaje;
    private Button       btnEnviar;
    private ImageButton  btnVolver;

    private WebSocket webSocket;
    private Gson      gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Referencias a vistas
        btnVolver      = findViewById(R.id.btn_volver_chat);
        scrollMensajes = findViewById(R.id.scroll_mensajes);
        llMensajes     = findViewById(R.id.ll_mensajes);
        etMensaje      = findViewById(R.id.etMensaje);
        btnEnviar      = findViewById(R.id.btnEnviar);

        // Recupera parámetros de la Intent
        idArtista     = getIntent().getIntExtra(EXTRA_ID_ARTISTA, -1);
        nombreUsuario = getIntent().getStringExtra(EXTRA_NOMBRE_USUARIO);

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());

        // Conectar al WebSocket
        conectarWebSocket();

        // Enviar mensaje al pulsar
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty() && webSocket != null) {
                MensajesChatDTO msg = new MensajesChatDTO(idArtista, nombreUsuario, texto);
                String json = gson.toJson(msg);
                webSocket.send(json);
                etMensaje.setText("");
            }
        });
    }

    private void conectarWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)  // Mantiene la conexión viva
                .build();

        // Construye ws://… a partir de Constantes.URL_BASE (“http://host:puerto” → “ws://host:puerto/ws”)
        String wsUrl = Constantes.URL_BASE
                .replaceFirst("^http", "ws")
                + "/ws/" + idArtista;


        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override public void onOpen(@NonNull WebSocket ws, @NonNull Response response) {
                runOnUiThread(() ->
                        mostrarEstado(nombreUsuario + " conectado")
                );
            }

            @Override public void onMessage(@NonNull WebSocket ws, @NonNull String text) {
                runOnUiThread(() -> mostrarMensaje(text));
            }

            @Override public void onMessage(@NonNull WebSocket ws, @NonNull ByteString bytes) {
                // No usado
            }

            @Override public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, Response r) {
                t.printStackTrace();
            }

            @Override public void onClosing(@NonNull WebSocket ws, int code, @NonNull String reason) {
                ws.close(1000, null);
                runOnUiThread(() ->
                        mostrarEstado(nombreUsuario + " se desconectó")
                );
            }
        });
        // No shutdown del executor para que siga vivo
    }

    private void mostrarMensaje(String json) {
        MensajesChatDTO msg = gson.fromJson(json, MensajesChatDTO.class);
        TextView tv = new TextView(this);
        tv.setText(msg.getNombreUsuario() + ": " + msg.getMensaje());
        tv.setPadding(8, 8, 8, 8);
        llMensajes.addView(tv);
        scrollMensajes.post(() ->
                scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    private void mostrarEstado(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setPadding(8, 4, 8, 4);
        tv.setAlpha(0.7f);
        llMensajes.addView(tv);
        scrollMensajes.post(() ->
                scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "App closed");
        }
    }
}
