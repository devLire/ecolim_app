package com.example.ecolim_app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    EditText etCantidad;
    Spinner spZona, spTipo;
    Button btnGuardar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etCantidad = findViewById(R.id.etCantidad);
        spZona = findViewById(R.id.spZona);
        spTipo = findViewById(R.id.spTipo);
        btnGuardar = findViewById(R.id.btnGuardar);

        dbHelper = new DBHelper(this);

        // CONFIGURACIÓN DE LOS MENÚS DESPLEGABLES (SPINNERS)

        String[] categorias = {"Plástico", "Orgánico", "Papel", "Peligroso"};
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias
        );
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterCategorias);

        String[] zonas = {"Almacén Principal", "Comedor", "Oficinas Administrativas", "Patio Trasero"};
        ArrayAdapter<String> adapterZonas = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, zonas
        );
        adapterZonas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZona.setAdapter(adapterZonas);

        // BOTÓN GUARDAR
        btnGuardar.setOnClickListener(v -> {

            String cantidadTxt = etCantidad.getText().toString().trim();

            if (cantidadTxt.isEmpty()) {
                Toast.makeText(this, "Completa la cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad = Double.parseDouble(cantidadTxt);

            int idUsuarioLogueado = 1;

            int idZonaSeleccionada = spZona.getSelectedItemPosition() + 1;
            int idCategoriaSeleccionada = spTipo.getSelectedItemPosition() + 1;

            long idNuevoRegistro = dbHelper.insertarRegistroCabecera(idUsuarioLogueado, idZonaSeleccionada);

            if (idNuevoRegistro != -1) {
                boolean ok = dbHelper.insertarDetalleResiduo(idNuevoRegistro, idCategoriaSeleccionada, cantidad);

                if (ok) {
                    Toast.makeText(this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la pantalla y vuelve atrás
                } else {
                    Toast.makeText(this, "Error al guardar el detalle", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error al crear el ticket", Toast.LENGTH_SHORT).show();
            }
        });
    }
}