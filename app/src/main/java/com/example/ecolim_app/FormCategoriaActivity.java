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

public class FormCategoriaActivity extends AppCompatActivity {

    DBHelper dbHelper;

    ImageView btnRegresar;
    TextView tvTituloFormularioCategoria;
    TextInputEditText etNombreCategoriaForm;
    AutoCompleteTextView spUnidadMedidaForm, spEstadoCategoriaForm;
    TextInputLayout tilEstadoCategoriaForm;
    MaterialButton btnAccionCategoria;

    int idCategoriaModificar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_categoria);

        try {
            dbHelper = new DBHelper(this);
            vincularVistas();
            configurarDropdowns();

            // CLIC TRADICIONAL PARA REGRESAR
            btnRegresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // VERIFICAR MODO (Añadir o Editar)
            verificarModoEdicion();

            // CLIC TRADICIONAL PARA GUARDAR
            btnAccionCategoria.setOnClickListener(new View.OnClickListener() {
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
        tvTituloFormularioCategoria = findViewById(R.id.tvTituloFormularioCategoria);
        etNombreCategoriaForm = findViewById(R.id.etNombreCategoriaForm);
        spUnidadMedidaForm = findViewById(R.id.spUnidadMedidaForm);
        spEstadoCategoriaForm = findViewById(R.id.spEstadoCategoriaForm);
        tilEstadoCategoriaForm = findViewById(R.id.tilEstadoCategoriaForm);
        btnAccionCategoria = findViewById(R.id.btnAccionCategoria);
    }

    private void configurarDropdowns() {
        // Dropdown de Unidad de Medida
        String[] unidades = new String[]{"Kg", "Litros", "Unidades"};
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, unidades);
        spUnidadMedidaForm.setAdapter(adapterUnidades);

        // Dropdown de Estados
        String[] estados = new String[]{"Activo", "Inactivo"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        spEstadoCategoriaForm.setAdapter(adapterEstados);
    }

    private void verificarModoEdicion() {
        // Recibimos el intent
        if (getIntent().hasExtra("ID_CATEGORIA_EDITAR")) {
            idCategoriaModificar = getIntent().getIntExtra("ID_CATEGORIA_EDITAR", -1);
        }

        if (idCategoriaModificar == -1) {
            // MODO AÑADIR: Ocultar el campo de estado
            tilEstadoCategoriaForm.setVisibility(View.GONE);
        } else {
            // MODO EDICIÓN: Mostrar el campo de estado y cambiar textos
            tilEstadoCategoriaForm.setVisibility(View.VISIBLE);
            tvTituloFormularioCategoria.setText("Editar Categoría");
            btnAccionCategoria.setText("Editar Categoría");

            // BUSCAMOS LOS DATOS EN LA BD PARA RELLENAR LOS CAMPOS
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT * FROM " + DBHelper.TABLE_CATEGORIA + " WHERE id_categoria = ?",
                    new String[]{String.valueOf(idCategoriaModificar)});

            if (cursor != null && cursor.moveToFirst()) {
                etNombreCategoriaForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_categoria")));

                spUnidadMedidaForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("unidad_medida")), false);

                // Configurar el estado actual (1 es Activo, 0 es Inactivo)
                int activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo"));
                spEstadoCategoriaForm.setText(activo == 1 ? "Activo" : "Inactivo", false);

                cursor.close();
            }
        }
    }

    private void procesarGuardado() {
        String nombre = etNombreCategoriaForm.getText().toString().trim();
        String unidad = spUnidadMedidaForm.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || unidad.isEmpty()) {
            Toast.makeText(this, "Por favor, completa el nombre y la unidad", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean exito;

        if (idCategoriaModificar == -1) {
            // MODO AÑADIR
            exito = dbHelper.insertarCategoria(nombre, unidad);
            if (exito) {
                Toast.makeText(this, "Categoría creada exitosamente", Toast.LENGTH_SHORT).show();
            }
        } else {
            // MODO EDITAR
            String estadoTxt = spEstadoCategoriaForm.getText().toString();
            boolean isActivo = estadoTxt.equals("Activo");

            exito = dbHelper.actualizarCategoria(idCategoriaModificar, nombre, unidad, isActivo);
            if (exito) {
                Toast.makeText(this, "Categoría actualizada exitosamente", Toast.LENGTH_SHORT).show();
            }
        }

        if (exito) {
            finish();
        } else {
            Toast.makeText(this, "Ocurrió un error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}