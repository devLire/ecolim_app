package com.example.ecolim_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class GestionAdminActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ImageView btnRegresar, btnPerfil;
    TabLayout tabLayoutGestion;
    TextInputEditText etBuscadorGestion;
    RecyclerView listaGestion;
    FloatingActionButton btnAgregarGestion;

    GestionAdapter adapter;
    ArrayList<ItemGestion> listaDatos = new ArrayList<>();

    // 0 = Usuarios, 1 = Categorías, 2 = Zonas
    int tabActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_admin);

        dbHelper = new DBHelper(this);

        vincularVistas();
        configurarTabs();
        configurarBuscador();

        // Cargar los datos de la primera pestaña por defecto
        cargarDatos(0);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestionAdminActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        // FAB
        btnAgregarGestion.setOnClickListener(v -> {
            if (tabActual == 0) { // 0 = Usuarios
                Intent intent = new Intent(GestionAdminActivity.this, FormUsuarioActivity.class);
                startActivity(intent);
            }
            if (tabActual == 1) { // 1 = Categorías
                Intent intent = new Intent(GestionAdminActivity.this, FormCategoriaActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            if (v != null) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos(tabActual);
    }

    private void vincularVistas() {
        btnRegresar = findViewById(R.id.btnRegresar);
        tabLayoutGestion = findViewById(R.id.tabLayoutGestion);
        etBuscadorGestion = findViewById(R.id.etBuscadorGestion);
        listaGestion = findViewById(R.id.listaGestion);
        btnAgregarGestion = findViewById(R.id.btnAgregarGestion);
        btnPerfil = findViewById(R.id.btnPerfil);
    }

    private void configurarTabs() {
        tabLayoutGestion.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabActual = tab.getPosition();
                etBuscadorGestion.setText("");
                cargarDatos(tabActual);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void configurarBuscador() {
        etBuscadorGestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filtrar(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarDatos(int tabIndex) {
        listaDatos.clear();
        Cursor cursor = null;

        if (tabIndex == 0) {
            // USUARIOS
            cursor = dbHelper.obtenerUsuarios();
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));
                String dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"));
                listaDatos.add(new ItemGestion(id, nombre + " - " + dni));
            }
        } else if (tabIndex == 1) {
            // CATEGORÍAS
            cursor = dbHelper.obtenerCategorias();
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_categoria"));
                String unidad = cursor.getString(cursor.getColumnIndexOrThrow("unidad_medida"));
                listaDatos.add(new ItemGestion(id, nombre + " (" + unidad + ")"));
            }
        } else if (tabIndex == 2) {
            // ZONAS
            cursor = dbHelper.obtenerZonas();
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_zona"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_zona"));
                listaDatos.add(new ItemGestion(id, nombre));
            }
        }

        if (cursor != null) cursor.close();

        // Configurar el Adapter
        adapter = new GestionAdapter(listaDatos, item -> {
            if (tabActual == 0) { // 0 = Usuarios
                Intent intent = new Intent(GestionAdminActivity.this, FormUsuarioActivity.class);
                intent.putExtra("ID_USUARIO_EDITAR", item.getId());
                startActivity(intent);
            }
            if (tabActual == 1) { // 1 = Categorías
                Intent intent = new Intent(GestionAdminActivity.this, FormCategoriaActivity.class);
                intent.putExtra("ID_CATEGORIA_EDITAR", item.getId());
                startActivity(intent);
            }
        });

        listaGestion.setAdapter(adapter);
    }
}