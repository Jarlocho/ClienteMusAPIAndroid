package com.example.musapiapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.EvaluacionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.network.ServicioEvaluacion;
import com.example.musapiapp.network.ServicioUsuario;
import com.example.musapiapp.util.Preferencias;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluarArtistaDialog extends DialogFragment {
    private static final String ARG_ID_ARTISTA = "arg_id_artista";

    public static EvaluarArtistaDialog newInstance(int idArtista) {
        EvaluarArtistaDialog dlg = new EvaluarArtistaDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_ARTISTA, idArtista);
        dlg.setArguments(args);
        return dlg;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_evaluar_artista, null);

        RatingBar ratingStars = view.findViewById(R.id.ratingStars);
        EditText  etComentario = view.findViewById(R.id.etComentario);
        Button    btnEnviar    = view.findViewById(R.id.btnEnviarEval);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        btnEnviar.setOnClickListener(v -> {
            int calif = (int) ratingStars.getRating();
            String comentario = etComentario.getText().toString().trim();
            int idArtista = requireArguments().getInt(ARG_ID_ARTISTA);
            enviarEvaluacion(idArtista, calif, comentario);
            dialog.dismiss();
        });

        return dialog;
    }

    private void enviarEvaluacion(int idArtista, int calif, String comentario) {
        String token = Preferencias.obtenerToken(requireContext());
        Log.d("EvalDialog", "Bearer token que envío: " + token);
        String bearer = token != null ? "Bearer " + token : "";

        ServicioEvaluacion srvEval = ApiCliente
                .getClient(requireContext())
                .create(ServicioEvaluacion.class);

        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setIdUsuario(new Gson().fromJson(
                Preferencias.recuperarUsuarioJson(requireContext()),
                com.example.musapiapp.dto.UsuarioDTO.class
        ).getIdUsuario());
        dto.setIdArtista(idArtista);
        dto.setCalificacion(calif);
        dto.setComentario(comentario);

        srvEval.registrar(bearer, dto)
                .enqueue(new Callback<RespuestaCliente<String>>(){
                    @Override
                    public void onResponse(Call<RespuestaCliente<String>> call,
                                           Response<RespuestaCliente<String>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            Toast.makeText(getContext(),
                                    resp.body().getMensaje(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String body = "";
                            try {
                                body = resp.errorBody() != null
                                        ? resp.errorBody().string()
                                        : "nulo";
                            } catch (IOException ignored) {}
                            Log.e("EvalDialog",
                                    "Error "+resp.code()+" — body: "+body);   // ← Y aquí
                            Toast.makeText(getContext(),
                                    "Error "+resp.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaCliente<String>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Fallo de red",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
