package com.example.ecolim_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView listActividades;
    Button btnAgregar;
    DBHelper dbHelper;
    ArrayList<String> lista;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listActividades = findViewById(R.id.listActividades);
        btnAgregar = findViewById(R.id.btnAgregar);

        dbHelper = new DBHelper(this);

        cargarActividades();

        btnAgregar.setOnClickListener(v -> {
            startActivity(new Intent(this, AddActivity.class));
        });
    }

    private void cargarActividades() {

        lista = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerActividades();

        if (cursor == null || cursor.getCount() == 0) {
            lista.add("No hay actividades registradas");
        } else {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String usuario = cursor.getString(1);
                String tipo = cursor.getString(2);
                double cantidad = cursor.getDouble(3);
                String fecha = cursor.getString(4);
                String observacion = cursor.getString(5);

                String texto =
                        "ID: " + id + "\n" +
                                "Usuario: " + usuario + "\n" +
                                "Tipo: " + tipo + "\n" +
                                "Cantidad: " + cantidad + " kg\n" +
                                "Fecha: " + fecha + "\n" +
                                "Obs: " + observacion;

                lista.add(texto);
            }
        }

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lista
        );

        listActividades.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarActividades();
    }
}