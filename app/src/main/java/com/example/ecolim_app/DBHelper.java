package com.example.ecolim_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ecolim.db";
    private static final int DATABASE_VERSION = 1;

    // NOMBRES DE LAS TABLAS
    public static final String TABLE_USUARIO = "Usuario";
    public static final String TABLE_CATEGORIA = "Categoria_Residuo";
    public static final String TABLE_ZONA = "Zona_Limpieza";
    public static final String TABLE_REGISTRO = "Registro_Recoleccion";
    public static final String TABLE_DETALLE = "Detalle_Residuo_Recoleccion";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Activación de las FK
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Creación de la DB
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tablas PRINCIPALES

        // 1. Tabla Usuario
        db.execSQL("CREATE TABLE " + TABLE_USUARIO + " (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_completo VARCHAR(40), " +
                "dni VARCHAR(20) UNIQUE, " +
                "contrasena TEXT, " +
                "rol VARCHAR(20) CHECK (rol IN ('Operario', 'Supervisor')), " + // 'Operario' o 'Supervisor'
                "activo BOOLEAN DEFAULT TRUE)");

        // 2. Tabla Categoría
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIA + " (" +
                "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_categoria VARCHAR(25), " +
                "unidad_medida VARCHAR(10), " +
                "activo BOOLEAN DEFAULT TRUE)");

        // 3. Tabla Zona de Limpieza
        db.execSQL("CREATE TABLE " + TABLE_ZONA + " (" +
                "id_zona INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_zona VARCHAR(50), " +
                "ubicacion_especifica VARCHAR(100), " +
                "activo BOOLEAN DEFAULT TRUE)");

        // TABLA HÍBRIDA

        // 4. Tabla Registro
        db.execSQL("CREATE TABLE " + TABLE_REGISTRO + " (" +
                "id_registro INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER, " +
                "id_zona INTEGER, " +
                "fecha_hora TEXT, " +
                "FOREIGN KEY(id_usuario) REFERENCES " + TABLE_USUARIO + "(id_usuario), " +
                "FOREIGN KEY(id_zona) REFERENCES " + TABLE_ZONA + "(id_zona))");


        // TABLA SECUNDARIA

        // 5. Tabla Detalle
        db.execSQL("CREATE TABLE " + TABLE_DETALLE + " (" +
                "id_detalle INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_registro INTEGER, " +
                "id_categoria INTEGER, " +
                "cantidad DOUBLE, " +
                "FOREIGN KEY(id_registro) REFERENCES " + TABLE_REGISTRO + "(id_registro), " +
                "FOREIGN KEY(id_categoria) REFERENCES " + TABLE_CATEGORIA + "(id_categoria))");

        // SEEDING

        // 1. Insertar Usuarios
        db.execSQL("INSERT INTO " + TABLE_USUARIO + " (nombre_completo, dni, contrasena, rol) VALUES ('Ernesto Pérez', '12345678', '1234', 'Operario')");
        db.execSQL("INSERT INTO " + TABLE_USUARIO + " (nombre_completo, dni, contrasena, rol) VALUES ('Admin ECOLIM', '1234', 'admin', 'Supervisor')");

        // 2. Insertar Categorías de Residuos
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Plástico', 'Kg')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Orgánico', 'Kg')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Papel', 'Kg')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Peligroso', 'Litros')");

        // 3. Insertar Zonas de Limpieza
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Almacén Principal', 'Planta Baja')");
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Comedor', 'Piso 1')");
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Oficinas Administrativas', 'Piso 2')");
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Patio Trasero', 'Exterior')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETALLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    // MÉTODO PARA EL LOGIN

    public boolean verificarCredenciales(String dniIngresado, String passIngresada) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Buscamos si existe un registro que coincida con el DNI y Contraseña ingresados
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE dni = ? AND contrasena = ?", new String[]{dniIngresado, passIngresada});

        boolean existe = cursor.getCount() > 0; // Si retorna 1 es porque fue una autenticación correcta (DNI y contraseña existen)
        cursor.close();

        return existe;
    }

    // MÉTODO PARA OBTENER DATOS DEL USUARIO LOGUEADO
    public Cursor obtenerDatosUsuarioLogueado(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultamos todos los campos del usuario que coincida con el DNI ingresado
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE dni = ?", new String[]{dni});
    }


    // =========================================================================
    // MÉTODOS CRUD - TABLA USUARIO (Uso exclusivo del Supervisor)
    // =========================================================================

    // C - Crear Usuario
    public boolean insertarUsuario(String nombreCompleto, String dni, String contrasena, String rol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre_completo", nombreCompleto);
        values.put("dni", dni);
        values.put("contrasena", contrasena);
        values.put("rol", rol); // Debe ser 'Operario' o 'Supervisor'

        long result = db.insert(TABLE_USUARIO, null, values);
        return result != -1;
    }

    // R - Obtener todos los Usuarios
    public Cursor obtenerUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " ORDER BY nombre_completo ASC", null);
    }

    // U - Actualizar datos de un Usuario
    public boolean actualizarUsuario(int idUsuario, String nombreCompleto, String dni, String contrasena, String rol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre_completo", nombreCompleto);
        values.put("dni", dni);
        values.put("contrasena", contrasena);
        values.put("rol", rol);

        // Actualizar donde el id_usuario coincida
        int rowsAffected = db.update(TABLE_USUARIO, values, "id_usuario = ?", new String[]{String.valueOf(idUsuario)});
        return rowsAffected > 0;
    }

    // D - Eliminar Usuario
    public boolean eliminarUsuario(int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USUARIO, "id_usuario = ?", new String[]{String.valueOf(idUsuario)});
        return rowsDeleted > 0;
    }

    // =========================================================================
    // MÉTODOS CRUD - TABLA CATEGORÍA (Catálogo dinámico)
    // =========================================================================

    // C - Crear nueva categoría de residuo
    public boolean insertarCategoria(String nombreCategoria, String unidadMedida) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre_categoria", nombreCategoria);
        values.put("unidad_medida", unidadMedida); // 'Kg', 'Litros', 'Unidades'

        long result = db.insert(TABLE_CATEGORIA, null, values);
        return result != -1;
    }

    // R - Obtener categorías
    public Cursor obtenerCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIA + " ORDER BY nombre_categoria ASC", null);
    }

    // U - Actualizar categoría
    public boolean actualizarCategoria(int idCategoria, String nombreCategoria, String unidadMedida) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre_categoria", nombreCategoria);
        values.put("unidad_medida", unidadMedida);

        int rowsAffected = db.update(TABLE_CATEGORIA, values, "id_categoria = ?", new String[]{String.valueOf(idCategoria)});
        return rowsAffected > 0;
    }

    // PASO 1: Insertar la Cabecera
    public long insertarRegistroCabecera(int idUsuario, int idZona) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put("id_usuario", idUsuario);
        values.put("id_zona", idZona);
        values.put("fecha_hora", fechaActual);
        values.put("estado_sincronizacion", 0);

        return db.insert(TABLE_REGISTRO, null, values);
    }

    // PASO 2: Insertar el Detalle
    public boolean insertarDetalleResiduo(long idRegistro, int idCategoria, double cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id_registro", idRegistro);
        values.put("id_categoria", idCategoria);
        values.put("cantidad", cantidad);

        long result = db.insert(TABLE_DETALLE, null, values);
        return result != -1;
    }

    // =========================================================================
    // MÉTODOS CRUD - LECTURA DE REGISTROS - HomeActivity
    // =========================================================================

    public Cursor obtenerRegistrosCompletos() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Hacemos un JOIN simple para unir la cabecera (fecha, usuario, zona) con el detalle (cantidad, categoría)
        String query = "SELECT r.id_registro, r.id_usuario, d.id_categoria, d.cantidad, r.fecha_hora, r.id_zona " +
                "FROM " + TABLE_REGISTRO + " r " +
                "INNER JOIN " + TABLE_DETALLE + " d ON r.id_registro = d.id_registro " +
                "ORDER BY r.id_registro DESC";

        return db.rawQuery(query, null);
    }
}