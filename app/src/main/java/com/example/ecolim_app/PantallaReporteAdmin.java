package com.example.ecolim_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PantallaReporteAdmin extends AppCompatActivity {

    DBHelper dbHelper;

    ImageView btnRegresar, btnPerfil;
    TextInputEditText etFechaDesdeRep, etFechaHastaRep;
    AutoCompleteTextView spTipoResiduoRep, spZonaRep;
    LinearLayout layoutListaReportes;

    SimpleDateFormat formatoDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    ArrayList<String> nombresCategorias = new ArrayList<>();
    ArrayList<Integer> idsCategorias = new ArrayList<>();
    int idCatSeleccionada = 0;

    ArrayList<String> nombresZonas = new ArrayList<>();
    ArrayList<Integer> idsZonas = new ArrayList<>();
    int idZonaSeleccionada = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_reporte_admin);


        try {
            dbHelper = new DBHelper(this);

            vincularVistas();

            btnRegresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            btnPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PantallaReporteAdmin.this, PerfilActivity.class);
                    startActivity(intent);
                }
            });

            configurarFechasPorDefecto();
            cargarFiltros();

        } catch (Exception e) {
            Toast.makeText(this, "Error de inicialización: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // VINCULACIÓN
    private void vincularVistas() throws Exception {
        btnRegresar = findViewById(R.id.btnRegresar);
        if (btnRegresar == null) throw new Exception("btnRegresar");

        btnPerfil = findViewById(R.id.btnPerfil);

        etFechaDesdeRep = findViewById(R.id.etFechaDesdeRep);
        if (etFechaDesdeRep == null) throw new Exception("etFechaDesdeRep");

        etFechaHastaRep = findViewById(R.id.etFechaHastaRep);
        if (etFechaHastaRep == null) throw new Exception("etFechaHastaRep");

        spTipoResiduoRep = findViewById(R.id.spTipoResiduoRep);
        if (spTipoResiduoRep == null) throw new Exception("spTipoResiduoRep");

        spZonaRep = findViewById(R.id.spZonaRep);
        if (spZonaRep == null) throw new Exception("spZonaRep");

        layoutListaReportes = findViewById(R.id.layoutListaReportes);
        if (layoutListaReportes == null) throw new Exception("layoutListaReportes");
    }

    private void configurarFechasPorDefecto() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String primerDia = formatoDB.format(cal.getTime());
        String hoy = formatoDB.format(new Date());

        etFechaDesdeRep.setText(primerDia);
        etFechaHastaRep.setText(hoy);

        etFechaDesdeRep.setOnClickListener(v -> mostrarCalendario(etFechaDesdeRep));
        etFechaHastaRep.setOnClickListener(v -> mostrarCalendario(etFechaHastaRep));
    }

    private void mostrarCalendario(TextInputEditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            target.setText(fecha);

            generarReporte();

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarFiltros() {
        nombresCategorias.add("Todas las categorías");
        idsCategorias.add(0);
        nombresZonas.add("Todas las zonas");
        idsZonas.add(0);

        // Cargamos Categorías desde SQLite
        Cursor cCat = dbHelper.obtenerCategorias();
        if (cCat != null) {
            while (cCat.moveToNext()) {
                idsCategorias.add(cCat.getInt(0));
                nombresCategorias.add(cCat.getString(1));
            }
            cCat.close();
        }

        // Cargamos Zonas desde SQLite
        Cursor cZon = dbHelper.getReadableDatabase().rawQuery("SELECT id_zona, nombre_zona FROM " + DBHelper.TABLE_ZONA + " ORDER BY nombre_zona ASC", null);
        if (cZon != null) {
            while (cZon.moveToNext()) {
                idsZonas.add(cZon.getInt(0));
                nombresZonas.add(cZon.getString(1));
            }
            cZon.close();
        }

        // Conectamos los Spinners a nuestras listas
        spTipoResiduoRep.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresCategorias));
        spTipoResiduoRep.setOnItemClickListener((parent, view, position, id) -> {
            idCatSeleccionada = idsCategorias.get(position);
            generarReporte();
        });

        spZonaRep.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresZonas));
        spZonaRep.setOnItemClickListener((parent, view, position, id) -> {
            idZonaSeleccionada = idsZonas.get(position);
            generarReporte();
        });

        generarReporte();
    }

    // LÓGICA DE DIBUJADO DE LA LISTA
    private void generarReporte() {
        layoutListaReportes.removeAllViews();

        String desde = etFechaDesdeRep.getText().toString();
        String hasta = etFechaHastaRep.getText().toString();

        Cursor c = dbHelper.obtenerReportesFiltrados(desde, hasta, idCatSeleccionada, idZonaSeleccionada);

        if (c != null && c.getCount() > 0) {
            boolean fondoMoradoGrisaceo = true;

            while (c.moveToNext()) {
                String categoria = c.getString(0);
                double cant = c.getDouble(1);
                String zona = c.getString(2);
                String fecha = c.getString(3);
                String operario = c.getString(4);

                String titulo = cant + " Kg de " + categoria + " – " + zona;
                String subtitulo = "Registrado por: " + operario + " | " + fecha;

                crearFilaReporte(titulo, subtitulo, fondoMoradoGrisaceo);
                fondoMoradoGrisaceo = !fondoMoradoGrisaceo;
            }
            c.close();
        } else {
            crearFilaReporte("No hay recolecciones", "Prueba cambiando las fechas o filtros", true);
        }
    }

    private void crearFilaReporte(String titulo, String subtitulo, boolean fondoAlternado) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(48, 32, 48, 32);

        itemLayout.setBackgroundColor(Color.parseColor(fondoAlternado ? "#d1e8d1" : "#FFFFFF"));

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(titulo);
        tvTitulo.setTextColor(Color.parseColor("#333333"));
        tvTitulo.setTextSize(14);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvSub = new TextView(this);
        tvSub.setText(subtitulo);
        tvSub.setTextColor(Color.parseColor("#888888"));
        tvSub.setTextSize(12);
        tvSub.setPadding(0, 8, 0, 0);

        View divisor = new View(this);
        divisor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divisor.setBackgroundColor(Color.parseColor("#E0E0E0"));

        itemLayout.addView(tvTitulo);
        itemLayout.addView(tvSub);

        layoutListaReportes.addView(itemLayout);
        layoutListaReportes.addView(divisor);
    }
}