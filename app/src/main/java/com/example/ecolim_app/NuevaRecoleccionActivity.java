package com.example.ecolim_app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NuevaRecoleccionActivity extends AppCompatActivity {

    // 1. Controles de la Interfaz
    AutoCompleteTextView spZona, spCategoria;
    EditText etCantidad;
    MaterialButton btnAnadirLista, btnGuardarRegistro;
    TableLayout tablaMateriales;
    ImageView btnRegresar;

    // 2. Base de datos y Sesión
    DBHelper dbHelper;
    String dniLogueado;
    int idUsuarioLogueado = -1;

    // 3. Listas para los Combobox (Spinners)
    ArrayList<String> nombresZonas = new ArrayList<>();
    ArrayList<Integer> idsZonas = new ArrayList<>();

    ArrayList<String> nombresCategorias = new ArrayList<>();
    ArrayList<Integer> idsCategorias = new ArrayList<>();

    int idZonaSeleccionada = -1;
    String nombreZonaSeleccionada = "";
    int idCategoriaSeleccionada = -1;
    String nombreCategoriaSeleccionada = "";

    // 4. Estructura del "Carrito" temporal
    class ItemCarrito {
        int idZona;
        String nombreZona;
        int idCategoria;
        String nombreCategoria;
        double cantidad;

        public ItemCarrito(int idZona, String nombreZona, int idCategoria, String nombreCategoria, double cantidad) {
            this.idZona = idZona;
            this.nombreZona = nombreZona;
            this.idCategoria = idCategoria;
            this.nombreCategoria = nombreCategoria;
            this.cantidad = cantidad;
        }
    }

    ArrayList<ItemCarrito> carritoResiduos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_recoleccion);

        btnRegresar = findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Inicializar lógica (Protegido con try-catch para evitar crashes)
        try {
            dbHelper = new DBHelper(this);
            dniLogueado = SessionManager.obtenerDNI(this);

            vincularVistas();
            obtenerIdUsuario();
            cargarDatosComboBox();
            dibujarTablaDinamica();

            btnAnadirLista.setOnClickListener(v -> agregarAlCarrito());
            btnGuardarRegistro.setOnClickListener(v -> guardarEnBaseDeDatos());

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar la vista: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        spZona = findViewById(R.id.spZona);
        spCategoria = findViewById(R.id.spCategoria);
        etCantidad = findViewById(R.id.etCantidad);
        btnAnadirLista = findViewById(R.id.btnAnadirLista);
        btnGuardarRegistro = findViewById(R.id.btnGuardarRegistro);
        tablaMateriales = findViewById(R.id.tablaMateriales);
    }

    private void obtenerIdUsuario() {
        if (dniLogueado == null || dniLogueado.isEmpty()) return;

        Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dniLogueado);
        if (cursor != null && cursor.moveToFirst()) {
            idUsuarioLogueado = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            cursor.close();
        }
    }

    private void cargarDatosComboBox() {
        // Cargar Zonas
        Cursor cZonas = dbHelper.obtenerZonasActivas();
        if (cZonas != null) {
            while (cZonas.moveToNext()) {
                idsZonas.add(cZonas.getInt(cZonas.getColumnIndexOrThrow("id_zona")));
                nombresZonas.add(cZonas.getString(cZonas.getColumnIndexOrThrow("nombre_zona")));
            }
            cZonas.close();
        }

        ArrayAdapter<String> adapterZonas = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresZonas);
        spZona.setAdapter(adapterZonas);
        spZona.setOnItemClickListener((parent, view, position, id) -> {
            idZonaSeleccionada = idsZonas.get(position);
            nombreZonaSeleccionada = nombresZonas.get(position);
        });

        // Cargar Categorías
        Cursor cCategorias = dbHelper.obtenerCategoriasActivas();
        if (cCategorias != null) {
            while (cCategorias.moveToNext()) {
                idsCategorias.add(cCategorias.getInt(cCategorias.getColumnIndexOrThrow("id_categoria")));
                nombresCategorias.add(cCategorias.getString(cCategorias.getColumnIndexOrThrow("nombre_categoria")));
            }
            cCategorias.close();
        }

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombresCategorias);
        spCategoria.setAdapter(adapterCategorias);
        spCategoria.setOnItemClickListener((parent, view, position, id) -> {
            idCategoriaSeleccionada = idsCategorias.get(position);
            nombreCategoriaSeleccionada = nombresCategorias.get(position);
        });
    }

    private void agregarAlCarrito() {
        String cantidadTxt = etCantidad.getText().toString().trim();

        if (idZonaSeleccionada == -1) {
            Toast.makeText(this, "Selecciona una zona válida de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idCategoriaSeleccionada == -1) {
            Toast.makeText(this, "Selecciona una categoría válida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cantidadTxt.isEmpty()) {
            Toast.makeText(this, "Ingresa la cantidad recolectada", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad = Double.parseDouble(cantidadTxt);
        carritoResiduos.add(new ItemCarrito(idZonaSeleccionada, nombreZonaSeleccionada, idCategoriaSeleccionada, nombreCategoriaSeleccionada, cantidad));

        etCantidad.setText("");
        dibujarTablaDinamica();
        Toast.makeText(this, "Residuo añadido a la cola", Toast.LENGTH_SHORT).show();
    }

    private void dibujarTablaDinamica() {
        if (tablaMateriales == null) return;

        tablaMateriales.removeAllViews();
        crearFilaTabla("Zona", "Categoría", "Cantidad", true, -1);

        for (int i = 0; i < carritoResiduos.size(); i++) {
            ItemCarrito item = carritoResiduos.get(i);
            crearFilaTabla(item.nombreZona, item.nombreCategoria, item.cantidad + " kg", false, i);
        }
    }

    private void crearFilaTabla(String col1, String col2, String col3, boolean esCabecera, int indexArray) {
        TableRow fila = new TableRow(this);
        TableRow.LayoutParams paramsCelda = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        paramsCelda.setMargins(1, 1, 1, 1);

        fila.addView(crearTextViewCelda(col1, esCabecera), paramsCelda);
        fila.addView(crearTextViewCelda(col2, esCabecera), paramsCelda);
        fila.addView(crearTextViewCelda(col3, esCabecera), paramsCelda);

        if (esCabecera) {
            fila.addView(crearTextViewCelda("Acción", true), paramsCelda);
        } else {
            LinearLayout layoutAccion = new LinearLayout(this);
            layoutAccion.setGravity(Gravity.CENTER);
            layoutAccion.setPadding(8, 8, 8, 8);
            layoutAccion.setBackgroundColor(Color.parseColor("#FAFAFA"));

            ImageView btnEliminar = new ImageView(this);
            btnEliminar.setImageResource(android.R.drawable.ic_menu_delete);
            btnEliminar.setColorFilter(Color.BLACK);

            btnEliminar.setOnClickListener(v -> {
                carritoResiduos.remove(indexArray);
                dibujarTablaDinamica();
            });

            layoutAccion.addView(btnEliminar);
            fila.addView(layoutAccion, paramsCelda);
        }

        tablaMateriales.addView(fila);
    }

    private TextView crearTextViewCelda(String texto, boolean esCabecera) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 16, 16, 16);
        tv.setBackgroundColor(Color.parseColor("#FAFAFA"));
        tv.setTextColor(esCabecera ? Color.BLACK : Color.DKGRAY);
        if (esCabecera) tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(12);
        return tv;
    }

    private void guardarEnBaseDeDatos() {
        if (carritoResiduos.isEmpty()) {
            Toast.makeText(this, "Añade al menos un residuo a la tabla", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idUsuarioLogueado == -1) {
            Toast.makeText(this, "Error: No se pudo identificar al operario", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<Integer> zonasUnicas = new HashSet<>();
        for (ItemCarrito item : carritoResiduos) {
            zonasUnicas.add(item.idZona);
        }

        boolean error = false;

        for (Integer idZona : zonasUnicas) {
            long idRegistro = dbHelper.insertarRegistroCabecera(idUsuarioLogueado, idZona);

            if (idRegistro != -1) {
                for (ItemCarrito item : carritoResiduos) {
                    if (item.idZona == idZona) {
                        boolean okDetalle = dbHelper.insertarDetalleResiduo(idRegistro, item.idCategoria, item.cantidad);
                        if (!okDetalle) error = true;
                    }
                }
            } else {
                error = true;
            }
        }

        if (!error) {
            Toast.makeText(this, "Recolección registrada correctamente", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Hubo un problema al guardar en la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}