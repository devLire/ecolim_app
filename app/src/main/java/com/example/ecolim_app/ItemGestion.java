package com.example.ecolim_app;

public class ItemGestion {
    private int id;
    private String textoMostrar;

    public ItemGestion(int id, String textoMostrar) {
        this.id = id;
        this.textoMostrar = textoMostrar;
    }

    public int getId() { return id; }
    public String getTextoMostrar() { return textoMostrar; }
}