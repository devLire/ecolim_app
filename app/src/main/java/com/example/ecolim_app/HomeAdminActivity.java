package com.example.ecolim_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeAdminActivity extends AppCompatActivity {

    // Variables de sesión y BD
    String dni;
    DBHelper dbHelper;

    // Vistas de la Interfaz
    ImageView btnPerfil;
    TextView labelBienvenida, labelRecoleccion;
    EditText etFechaDesde, etFechaHasta;

    // Contenedores Dinámicos
    TableLayout tablaDinamica;
    LinearLayout layoutPorcentajes;

    MaterialButton btnReportes, btnGestionDatos;

    // Formato de fecha para SQLite y Array de colores para el gráfico
    SimpleDateFormat formatoDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final String[] COLORES_GRAFICO = {"#FF8A8A", "#C5E1A5", "#FFCC80", "#81D4FA", "#CE93D8", "#FFF59D", "#BCAAA4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);

        dbHelper = new DBHelper(this);
        dni = SessionManager.obtenerDNI(this);

        // 1. Enlazamos las vistas del XML con Java
        vincularVistas();

        // 2. Validamos que el administrador exista
        validarSesion();

        // 3. Cargamos el total del día de hoy en la tarjeta verde
        String fechaHoy = formatoDB.format(new Date());
        double totalHoy = dbHelper.obtenerTotalKilosPorFecha(fechaHoy);
        labelRecoleccion.setText("Hoy se recolectó " + String.format(Locale.getDefault(), "%.1f", totalHoy) + "kg");

        // 4. Configuramos el evento de clic para los calendarios
        configurarSelectoresDeFecha();

        // 5. Establecemos las fechas por defecto (Desde el día 1 del mes actual, hasta hoy)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String primerDiaMes = formatoDB.format(cal.getTime());

        etFechaDesde.setText(primerDiaMes);
        etFechaHasta.setText(fechaHoy);

        // 6. Dibujamos la tabla y la barra por primera vez
        actualizarTablaDatosDinamica(primerDiaMes, fechaHoy);

        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this, PantallaReporteAdmin.class);
                startActivity(intent);
            }
        });

        btnGestionDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this, GestionAdminActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void vincularVistas() {
        btnPerfil = findViewById(R.id.btnPerfil);
        labelBienvenida = findViewById(R.id.labelBienvenida);
        labelRecoleccion = findViewById(R.id.labelRecoleccion);
        etFechaDesde = findViewById(R.id.etFechaDesde);
        etFechaHasta = findViewById(R.id.etFechaHasta);
        tablaDinamica = findViewById(R.id.tablaDinamica);
        layoutPorcentajes = findViewById(R.id.layoutPorcentajes);
        btnReportes = findViewById(R.id.btnReportes);
        btnGestionDatos = findViewById(R.id.btnGestionDatos);

        btnPerfil.setOnClickListener(v -> {
            startActivity(new Intent(HomeAdminActivity.this, PerfilActivity.class));
        });
    }

    private void validarSesion() {
        Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dni);
        if (cursor != null && cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));
            labelBienvenida.setText("¡Bienvenido, " + nombre + "!");
            cursor.close();
        } else {
            // Si hay error en la sesión, lo devolvemos al Login
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void configurarSelectoresDeFecha() {
        etFechaDesde.setOnClickListener(v -> mostrarCalendario(etFechaDesde));
        etFechaHasta.setOnClickListener(v -> mostrarCalendario(etFechaHasta));
    }

    private void mostrarCalendario(EditText editTextTarget) {
        Calendar calendario = Calendar.getInstance();

        // Extraer la fecha actual del EditText para iniciar el calendario en ese día
        String fechaActualTxt = editTextTarget.getText().toString();
        if (!fechaActualTxt.isEmpty()) {
            try {
                Date fechaGuardada = formatoDB.parse(fechaActualTxt);
                if (fechaGuardada != null) {
                    calendario.setTime(fechaGuardada);
                }
            } catch (Exception e) {
                // Si el formato es incorrecto, tomar el día actual
                e.printStackTrace();
            }
        }

        int yearInicial = calendario.get(Calendar.YEAR);
        int monthInicial = calendario.get(Calendar.MONTH);
        int dayInicial = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Se suma +1 al mes porque en Java los meses van de 0 a 11
            String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editTextTarget.setText(fechaSeleccionada);

            actualizarTablaDatosDinamica(etFechaDesde.getText().toString(), etFechaHasta.getText().toString());

        }, yearInicial, monthInicial, dayInicial);

        dpd.show();
    }

    private void actualizarTablaDatosDinamica(String fechaInicio, String fechaFin) {
        // Limpiamos los contenedores visuales
        tablaDinamica.removeAllViews();
        layoutPorcentajes.removeAllViews();

        double totalGeneral = 0;
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<Double> cantidades = new ArrayList<>();

        // Traemos la data agrupada desde la base de datos
        Cursor cursor = dbHelper.obtenerKilosPorCategoriaRango(fechaInicio, fechaFin);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                nombres.add(cursor.getString(0)); // nombre_categoria
                double cantidad = cursor.getDouble(1); // total_kg
                cantidades.add(cantidad);
                totalGeneral += cantidad;
            }
            cursor.close();
        }

        // 1. Dibujar Cabecera
        crearFilaTabla("Material", "Cantidad", true);

        // 2. Validar si no hay datos en esas fechas
        if (totalGeneral == 0) {
            crearFilaTabla("Sin datos", "0.0 Kg", false);

            // Dibujar una barra gris vacía
            TextView barraVacia = new TextView(this);
            barraVacia.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            barraVacia.setBackgroundColor(Color.LTGRAY);
            barraVacia.setGravity(Gravity.CENTER);
            barraVacia.setText("No hay recolecciones en estas fechas");
            barraVacia.setTextColor(Color.WHITE);
            layoutPorcentajes.addView(barraVacia);
            return;
        }

        // 3. Recorrer los datos para las filas y la barra porcentual
        for (int i = 0; i < nombres.size(); i++) {
            String nombreCat = nombres.get(i);
            double cantidadCat = cantidades.get(i);

            // Fila de la tabla
            crearFilaTabla(nombreCat, String.format(Locale.getDefault(), "%.1f Kg", cantidadCat), false);

            // Bloque de la barra de progreso
            double porcentaje = (cantidadCat / totalGeneral) * 100;
            TextView bloqueGrafico = new TextView(this);

            // El peso de la vista (layout_weight) es igual al porcentaje matemático
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, (float) porcentaje);
            bloqueGrafico.setLayoutParams(params);

            bloqueGrafico.setBackgroundColor(Color.parseColor(COLORES_GRAFICO[i % COLORES_GRAFICO.length]));
            bloqueGrafico.setGravity(Gravity.CENTER);
            bloqueGrafico.setText(nombreCat + "\n" + String.format(Locale.getDefault(), "%.1f%%", porcentaje));
            bloqueGrafico.setTextColor(Color.BLACK);
            bloqueGrafico.setTextSize(10);
            bloqueGrafico.setTypeface(null, Typeface.BOLD);

            layoutPorcentajes.addView(bloqueGrafico);
        }

        // 4. Fila final con el total general
        crearFilaTabla("TOTAL", String.format(Locale.getDefault(), "%.1f Kg", totalGeneral), true);
    }

    private void crearFilaTabla(String textoIzq, String textoDer, boolean esCabecera) {
        TableRow fila = new TableRow(this);

        TableRow.LayoutParams paramsCelda = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        paramsCelda.setMargins(1, 1, 1, 1);

        // Columna Izquierda
        TextView tvIzq = new TextView(this);
        tvIzq.setText(textoIzq);
        tvIzq.setGravity(Gravity.CENTER);
        tvIzq.setPadding(24, 24, 24, 24);
        tvIzq.setBackgroundColor(Color.WHITE);
        tvIzq.setTextColor(Color.BLACK);
        if (esCabecera) tvIzq.setTypeface(null, Typeface.BOLD);

        // Columna Derecha
        TextView tvDer = new TextView(this);
        tvDer.setText(textoDer);
        tvDer.setGravity(Gravity.CENTER);
        tvDer.setPadding(24, 24, 24, 24);
        tvDer.setBackgroundColor(Color.WHITE);
        tvDer.setTextColor(Color.BLACK);
        if (esCabecera) tvDer.setTypeface(null, Typeface.BOLD);

        fila.addView(tvIzq, paramsCelda);
        fila.addView(tvDer, paramsCelda);

        tablaDinamica.addView(fila);
    }
}