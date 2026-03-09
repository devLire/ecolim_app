package com.example.ecolim_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class FormZonaActivity extends AppCompatActivity {

    DBHelper dbHelper;

    ImageView btnRegresar;
    TextView tvTituloFormularioZona;
    TextInputEditText etNombreZonaForm, etUbicacionZonaForm;
    AutoCompleteTextView spEstadoZonaForm;
    TextInputLayout tilEstadoZonaForm;
    MaterialButton btnAccionZona;

    int idZonaModificar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_zona);

        try {
            dbHelper = new DBHelper(this);
            vincularVistas();
            configurarDropdowns();

            // CLIC A LA ANTIGUA PARA REGRESAR
            btnRegresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // VERIFICAR MODO (Añadir o Editar)
            verificarModoEdicion();

            // CLIC A LA ANTIGUA PARA GUARDAR
            btnAccionZona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procesarGuardado();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void vincularVistas() {
        btnRegresar = findViewById(R.id.btnRegresar);
        tvTituloFormularioZona = findViewById(R.id.tvTituloFormularioZona);
        etNombreZonaForm = findViewById(R.id.etNombreZonaForm);
        etUbicacionZonaForm = findViewById(R.id.etUbicacionZonaForm);
        spEstadoZonaForm = findViewById(R.id.spEstadoZonaForm);
        tilEstadoZonaForm = findViewById(R.id.tilEstadoZonaForm);
        btnAccionZona = findViewById(R.id.btnAccionZona);
    }

    private void configurarDropdowns() {
        // Dropdown de Estados (solo necesitamos este aquí)
        String[] estados = new String[]{"Activo", "Inactivo"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        spEstadoZonaForm.setAdapter(adapterEstados);
    }

    private void verificarModoEdicion() {
        // Recibimos el intent
        if (getIntent().hasExtra("ID_ZONA_EDITAR")) {
            idZonaModificar = getIntent().getIntExtra("ID_ZONA_EDITAR", -1);
        }

        if (idZonaModificar == -1) {
            // MODO AÑADIR: Ocultar el campo de estado
            tilEstadoZonaForm.setVisibility(View.GONE);
        } else {
            // MODO EDICIÓN: Mostrar el campo de estado y cambiar textos
            tilEstadoZonaForm.setVisibility(View.VISIBLE);
            tvTituloFormularioZona.setText("Editar Zona");
            btnAccionZona.setText("Editar Zona");

            // BUSCAMOS LOS DATOS EN LA BD PARA RELLENAR LOS CAMPOS
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT * FROM " + DBHelper.TABLE_ZONA + " WHERE id_zona = ?",
                    new String[]{String.valueOf(idZonaModificar)});

            if (cursor != null && cursor.moveToFirst()) {
                etNombreZonaForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_zona")));

                // La ubicación es opcional, así que comprobamos que no sea nula por si acaso
                String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion_especifica"));
                if (ubicacion != null) {
                    etUbicacionZonaForm.setText(ubicacion);
                }

                // Configurar el estado actual (1 es Activo, 0 es Inactivo)
                int activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo"));
                spEstadoZonaForm.setText(activo == 1 ? "Activo" : "Inactivo", false);

                cursor.close();
            }
        }
    }

    private void procesarGuardado() {
        String nombre = etNombreZonaForm.getText().toString().trim();
        String ubicacion = etUbicacionZonaForm.getText().toString().trim(); // Puede quedar vacío

        // Validaciones básicas (solo el nombre es obligatorio)
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el nombre de la zona", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean exito;

        if (idZonaModificar == -1) {
            // MODO AÑADIR
            exito = dbHelper.insertarZona(nombre, ubicacion);
            if (exito) {
                Toast.makeText(this, "Zona creada exitosamente", Toast.LENGTH_SHORT).show();
            }
        } else {
            // MODO EDITAR
            String estadoTxt = spEstadoZonaForm.getText().toString();
            boolean isActivo = estadoTxt.equals("Activo");

            exito = dbHelper.actualizarZona(idZonaModificar, nombre, ubicacion, isActivo);
            if (exito) {
                Toast.makeText(this, "Zona actualizada exitosamente", Toast.LENGTH_SHORT).show();
            }
        }

        if (exito) {
            finish();
        } else {
            Toast.makeText(this, "Ocurrió un error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}