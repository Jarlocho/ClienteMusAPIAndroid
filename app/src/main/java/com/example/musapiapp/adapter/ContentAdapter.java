package com.example.musapiapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.activities.BusquedaActivity;
import com.example.musapiapp.model.ContentItem;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.Preferencias;

import java.util.List;

public class ContentAdapter
        extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    public interface Listener {
        void onDetails(ContentItem item);
        void onPlay(ContentItem item);
        void onSave(ContentItem item);
    }

    private List<ContentItem> items;
    private Listener listener;
    private Context ctx;

    public ContentAdapter(List<ContentItem> items, Context ctx, Listener listener) {
        this.items = items;
        this.listener = listener;
        this.ctx = ctx;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contenido, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ContentItem it = items.get(pos);
        // reset visibility
        h.btnGuardar.setVisibility(it.isShowSave() ? View.VISIBLE : View.GONE);
        h.btnReproducir.setVisibility(View.VISIBLE);
        h.tvAutor.setVisibility(View.VISIBLE);

        switch (it.getType()) {
            case ALBUM:
                BusquedaAlbumDTO a = (BusquedaAlbumDTO) it.getDto();
                h.tvNombre.setText(a.getNombreAlbum());
                h.tvAutor.setText(a.getNombreArtista());
                Glide.with(h.itemView.getContext())
                        .load(ApiCliente.getUrlArchivos() + a.getUrlFoto())
                        .into(h.imgFoto);
                break;
            case CANCION:
                BusquedaCancionDTO c = (BusquedaCancionDTO) it.getDto();
                h.tvNombre.setText(c.getNombre());
                h.tvAutor.setText(c.getNombreArtista());
                cargarImagen(c.getUrlFoto(), h.imgFoto);
                break;
            case ARTISTA:
                BusquedaArtistaDTO ar = (BusquedaArtistaDTO) it.getDto();
                h.tvNombre.setText(ar.getNombre());
                h.tvAutor.setText("@"+ar.getNombreUsuario());
                cargarImagen(ar.getUrlFoto(), h.imgFoto);
                break;
            case LISTA:
                ListaReproduccionDTO l = (ListaReproduccionDTO) it.getDto();
                h.tvNombre.setText(l.getNombre());
                h.tvAutor.setVisibility(View.GONE);
                h.btnReproducir.setVisibility(View.GONE);
                cargarImagen(l.getFotoPath(), h.imgFoto);
                break;
        }

        h.btnVerDetalles.setOnClickListener(v -> listener.onDetails(it));
        h.btnReproducir.setOnClickListener(v -> listener.onPlay(it));
        h.btnGuardar.setOnClickListener(v -> {
            listener.onSave(it);
            h.btnGuardar.setVisibility(View.GONE);
        });
    }

    private void cargarImagen(String url, ImageView destino) {
        if (url == null || url.isEmpty()) return;

        String token = Preferencias.obtenerToken(ctx); // O contexto si estás fuera de actividad
        String bearer = token != null ? "Bearer " + token : "";

        GlideUrl glideUrl = new GlideUrl(
                Constantes.URL_BASE + url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", bearer)
                        .build()
        );

        Glide.with(ctx)
                .load(glideUrl)
                .into(destino);
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView tvNombre, tvAutor;
        Button btnGuardar, btnVerDetalles;
        ImageButton btnReproducir;
        ViewHolder(View v) {
            super(v);
            imgFoto       = v.findViewById(R.id.imgFoto);
            tvNombre      = v.findViewById(R.id.tvNombre);
            tvAutor       = v.findViewById(R.id.tvAutor);
            btnGuardar    = v.findViewById(R.id.btnGuardar);
            btnVerDetalles= v.findViewById(R.id.btnVerDetalles);
            btnReproducir = v.findViewById(R.id.btnReproducir);
        }
    }
}
