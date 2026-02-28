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
        Cursor cursor = dbHelper.obtenerRegistrosCompletos();

        if (cursor == null || cursor.getCount() == 0) {
            lista.add("No hay recolecciones registradas");
        } else {
            while (cursor.moveToNext()) {
                int idRegistro = cursor.getInt(0);
                int idUsuario = cursor.getInt(1);
                int idCategoria = cursor.getInt(2);
                double cantidad = cursor.getDouble(3);
                String fecha = cursor.getString(4);
                int idZona = cursor.getInt(5);

                String texto =
                        "Ticket #" + idRegistro + "\n" +
                                "ID Operario: " + idUsuario + " | ID Zona: " + idZona + "\n" +
                                "ID Categoría: " + idCategoria + "\n" +
                                "Cantidad: " + cantidad + " (Kg/L)\n" +
                                "Fecha: " + fecha;

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