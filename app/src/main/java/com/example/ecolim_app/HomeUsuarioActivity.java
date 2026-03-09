package com.example.ecolim_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeUsuarioActivity extends AppCompatActivity {

    String dni;
    DBHelper dbHelper;
    int idUsuarioLogueado = -1;
    TextView labelBienvenida, labelTotalRecolectado;
    ImageView btnPerfil;
    EditText etBuscar; // <-- NUESTRO BUSCADOR

    FloatingActionButton btnNuevaRecoleccion;

    ListView listActividades;
    ArrayList<String> listaResiduos;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_usuario);

        dni = SessionManager.obtenerDNI(this);
        dbHelper = new DBHelper(this);

        labelBienvenida = findViewById(R.id.labelBienvenida);
        labelTotalRecolectado = findViewById(R.id.labelTotalRecolectado);
        btnPerfil = findViewById(R.id.btnPerfil);
        listActividades = findViewById(R.id.listActividades);
        etBuscar = findViewById(R.id.etBuscar); // <-- VINCULAMOS EL BUSCADOR
        btnNuevaRecoleccion = findViewById(R.id.btnNuevaRecoleccion);

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeUsuarioActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dni);

        if (cursor != null && cursor.moveToFirst()) {
            idUsuarioLogueado = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));

            labelBienvenida.setText("¡Bienvenido " + nombre + "!");

            cursor.close();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        btnNuevaRecoleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeUsuarioActivity.this, NuevaRecoleccionActivity.class);
                startActivity(intent);
            }
        });

        // LÓGICA DEL BUSCADOR
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitamos hacer nada antes de que cambie
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cada vez que el usuario escribe o borra una letra, filtramos el adapter
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idUsuarioLogueado != -1) {
            cargarListaUsuario(idUsuarioLogueado);

            double totalHistorico = dbHelper.obtenerTotalHistoricoPorUsuario(idUsuarioLogueado);
            labelTotalRecolectado.setText("Has recolectado un total de: " + String.format(java.util.Locale.getDefault(), "%.1f", totalHistorico) + " kg");
        }
    }

    // Lógica para llenar la lista
    private void cargarListaUsuario(int idUsuario) {
        listaResiduos = new ArrayList<>();
        Cursor cursorRegistros = dbHelper.obtenerRegistrosPorUsuario(idUsuario);

        if (cursorRegistros == null || cursorRegistros.getCount() == 0) {
            listaResiduos.add("Aún no tienes recolecciones registradas");
        } else {
            while (cursorRegistros.moveToNext()) {
                String categoria = cursorRegistros.getString(0);
                double cantidad = cursorRegistros.getDouble(1);
                String zona = cursorRegistros.getString(2);
                String fecha = cursorRegistros.getString(3);

                String itemTexto = cantidad + " Kg de " + categoria + " – " + zona + "\n(" + fecha + ")";
                listaResiduos.add(itemTexto);
            }
            cursorRegistros.close();
        }

        adapter = new ArrayAdapter<String>(this, R.layout.item_residuo, R.id.tvTextoItem, listaResiduos);
        listActividades.setAdapter(adapter);

        if (etBuscar.getText().toString().trim().length() > 0) {
            adapter.getFilter().filter(etBuscar.getText().toString());
        }
    }
}