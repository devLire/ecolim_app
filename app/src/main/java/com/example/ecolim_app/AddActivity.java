package com.example.ecolim_app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    EditText etUsuario, etCantidad, etObservacion;
    Spinner spTipo;
    Button btnGuardar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etUsuario = findViewById(R.id.etUsuario);
        etCantidad = findViewById(R.id.etCantidad);
        etObservacion = findViewById(R.id.etObservacion);
        spTipo = findViewById(R.id.spTipo);
        btnGuardar = findViewById(R.id.btnGuardar);

        dbHelper = new DBHelper(this);

        String[] tipos = {"Plástico", "Orgánico", "Papel", "Peligroso"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spTipo.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> {

            String usuario = etUsuario.getText().toString().trim();
            String tipo = spTipo.getSelectedItem().toString();
            String cantidadTxt = etCantidad.getText().toString().trim();
            String observacion = etObservacion.getText().toString().trim();

            if (usuario.isEmpty() || cantidadTxt.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad = Double.parseDouble(cantidadTxt);

            boolean ok = dbHelper.insertarActividad(
                    usuario,
                    tipo,
                    cantidad,
                    observacion
            );

            if (ok) {
                Toast.makeText(this, "Guardado ✔", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error ❌", Toast.LENGTH_SHORT).show();
            }
        });
    }
}