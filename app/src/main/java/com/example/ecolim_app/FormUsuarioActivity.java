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

public class FormUsuarioActivity extends AppCompatActivity {

    DBHelper dbHelper;

    ImageView btnRegresar;
    TextView tvTituloFormularioUsuario;
    TextInputEditText etNombreUsuarioForm, etDniUsuarioForm, etPassUsuarioForm;
    AutoCompleteTextView spRolUsuarioForm, spEstadoUsuarioForm;
    TextInputLayout tilEstadoUsuarioForm;
    MaterialButton btnAccionUsuario;

    int idUsuarioModificar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_usuario);

        try {
            dbHelper = new DBHelper(this);
            vincularVistas();
            configurarDropdowns();

            btnRegresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // VERIFICAR MODO
            verificarModoEdicion();

            btnAccionUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procesarGuardado();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            if (v != null) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });
    }

    private void vincularVistas() {
        btnRegresar = findViewById(R.id.btnRegresar);
        tvTituloFormularioUsuario = findViewById(R.id.tvTituloFormularioUsuario);
        etNombreUsuarioForm = findViewById(R.id.etNombreUsuarioForm);
        etDniUsuarioForm = findViewById(R.id.etDniUsuarioForm);
        etPassUsuarioForm = findViewById(R.id.etPassUsuarioForm);
        spRolUsuarioForm = findViewById(R.id.spRolUsuarioForm);
        spEstadoUsuarioForm = findViewById(R.id.spEstadoUsuarioForm);
        tilEstadoUsuarioForm = findViewById(R.id.tilEstadoUsuarioForm);
        btnAccionUsuario = findViewById(R.id.btnAccionUsuario);
    }

    private void configurarDropdowns() {
        // Dropdown de Roles
        String[] roles = new String[]{"Operario", "Supervisor"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        spRolUsuarioForm.setAdapter(adapterRoles);

        // Dropdown de Estados
        String[] estados = new String[]{"Activo", "Inactivo"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        spEstadoUsuarioForm.setAdapter(adapterEstados);
    }

    private void verificarModoEdicion() {
        // Recibimos el intent
        if (getIntent().hasExtra("ID_USUARIO_EDITAR")) {
            idUsuarioModificar = getIntent().getIntExtra("ID_USUARIO_EDITAR", -1);
        }

        if (idUsuarioModificar == -1) {
            // MODO AÑADIR: Ocultar el campo de estado
            tilEstadoUsuarioForm.setVisibility(View.GONE);
        } else {
            // MODO EDICIÓN: Mostrar el campo de estado
            tilEstadoUsuarioForm.setVisibility(View.VISIBLE);
            tvTituloFormularioUsuario.setText("Editar Usuario");
            btnAccionUsuario.setText("Editar Usuario");

            // BUSCAMOS LOS DATOS EN LA BD PARA RELLENAR LOS CAMPOS
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT * FROM " + DBHelper.TABLE_USUARIO + " WHERE id_usuario = ?",
                    new String[]{String.valueOf(idUsuarioModificar)});

            if (cursor != null && cursor.moveToFirst()) {
                etNombreUsuarioForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo")));
                etDniUsuarioForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("dni")));
                etPassUsuarioForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("contrasena")));

                spRolUsuarioForm.setText(cursor.getString(cursor.getColumnIndexOrThrow("rol")), false);

                // Configurar el estado actual
                int activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo"));
                spEstadoUsuarioForm.setText(activo == 1 ? "Activo" : "Inactivo", false);

                cursor.close();
            }
        }
    }

    private void procesarGuardado() {
        String nombre = etNombreUsuarioForm.getText().toString().trim();
        String dni = etDniUsuarioForm.getText().toString().trim();
        String pass = etPassUsuarioForm.getText().toString().trim();
        String rol = spRolUsuarioForm.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || dni.isEmpty() || pass.isEmpty() || rol.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.length() < 8) {
            Toast.makeText(this, "El DNI debe tener mínimo 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dni.length() > 21) {
            Toast.makeText(this, "El DNI debe tener máximo 20 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        // PREVENIR DUPLICADOS DE DNI
        if (dbHelper.existeDNI(dni, idUsuarioModificar)) {
            Toast.makeText(this, "Error: Este DNI ya está registrado por otro usuario", Toast.LENGTH_LONG).show();
            return;
        }

        boolean exito;

        if (idUsuarioModificar == -1) {
            // MODO AÑADIR (La BD asignará Activo=1 por defecto)
            exito = dbHelper.insertarUsuario(nombre, dni, pass, rol);
            if (exito) {
                Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
            }
        } else {
            // MODO EDITAR
            String estadoTxt = spEstadoUsuarioForm.getText().toString();
            boolean isActivo = estadoTxt.equals("Activo");

            exito = dbHelper.actualizarUsuario(idUsuarioModificar, nombre, dni, pass, rol, isActivo);
            if (exito) {
                Toast.makeText(this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
            }
        }

        if (exito) {
            finish();
        } else {
            Toast.makeText(this, "Ocurrió un error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}