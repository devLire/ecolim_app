package com.example.ecolim_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GestionAdapter extends RecyclerView.Adapter<GestionAdapter.ViewHolder> {

    private ArrayList<ItemGestion> listaOriginal;
    private ArrayList<ItemGestion> listaFiltrada;
    private OnItemEditListener listener;

    public interface OnItemEditListener {
        void onEditClick(ItemGestion item);
    }

    public GestionAdapter(ArrayList<ItemGestion> lista, OnItemEditListener listener) {
        this.listaOriginal = lista;
        this.listaFiltrada = new ArrayList<>(lista); // Copia para el buscador
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemGestion item = listaFiltrada.get(position);
        holder.tvTextoItem.setText(item.getTextoMostrar());

        holder.btnEditarItem.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    // Método para el Buscador
    public void filtrar(String textoBusqueda) {
        listaFiltrada.clear();
        if (textoBusqueda.isEmpty()) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            textoBusqueda = textoBusqueda.toLowerCase();
            for (ItemGestion item : listaOriginal) {
                if (item.getTextoMostrar().toLowerCase().contains(textoBusqueda)) {
                    listaFiltrada.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTextoItem;
        ImageView btnEditarItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTextoItem = itemView.findViewById(R.id.tvTextoItem);
            btnEditarItem = itemView.findViewById(R.id.btnEditarItem);
        }
    }
}