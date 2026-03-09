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
        db.execSQL("INSERT INTO " + TABLE_USUARIO + " (nombre_completo, dni, contrasena, rol) VALUES ('Ernesto Pérez', '12345678', '1234', 'Operario')"); // ID: 1
        db.execSQL("INSERT INTO " + TABLE_USUARIO + " (nombre_completo, dni, contrasena, rol) VALUES ('Admin ECOLIM', '1234', 'admin', 'Supervisor')"); // ID: 2
        db.execSQL("INSERT INTO " + TABLE_USUARIO + " (nombre_completo, dni, contrasena, rol) VALUES ('María Gómez', '87654321', '1234', 'Operario')"); // ID: 3

        // 2. Insertar Categorías de Residuos
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Plástico', 'Kg')"); // ID: 1
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Orgánico', 'Kg')"); // ID: 2
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Papel', 'Kg')");    // ID: 3
        db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nombre_categoria, unidad_medida) VALUES ('Peligroso', 'Litros')"); // ID: 4

        // 3. Insertar Zonas de Limpieza
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Almacén Principal', 'Planta Baja')"); // ID: 1
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Comedor', 'Piso 1')"); // ID: 2
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Oficinas Administrativas', 'Piso 2')"); // ID: 3
        db.execSQL("INSERT INTO " + TABLE_ZONA + " (nombre_zona, ubicacion_especifica) VALUES ('Patio Trasero', 'Exterior')"); // ID: 4

        // 4. Insertar Registros (Cabecera de la recolección)
        db.execSQL("INSERT INTO " + TABLE_REGISTRO + " (id_usuario, id_zona, fecha_hora) VALUES (1, 2, '2026-03-07 08:30:00')"); // ID: 1
        db.execSQL("INSERT INTO " + TABLE_REGISTRO + " (id_usuario, id_zona, fecha_hora) VALUES (3, 1, '2026-03-07 09:15:00')"); // ID: 2
        db.execSQL("INSERT INTO " + TABLE_REGISTRO + " (id_usuario, id_zona, fecha_hora) VALUES (1, 4, '2026-03-07 10:00:00')"); // ID: 3
        db.execSQL("INSERT INTO " + TABLE_REGISTRO + " (id_usuario, id_zona, fecha_hora) VALUES (3, 3, '2026-03-07 14:20:00')"); // ID: 4

        // 5. Insertar Detalles de los Residuos recolectados
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (1, 1, 2.5)");
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (1, 2, 1.2)");
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (2, 3, 5.0)");
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (3, 4, 2.0)");
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (3, 1, 1.5)");
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (4, 3, 3.0)");
        db.execSQL("INSERT INTO " + TABLE_DETALLE + " (id_registro, id_categoria, cantidad) VALUES (4, 2, 0.5)");
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
        // Solo permite loguearse si activo = 1
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE dni = ? AND contrasena = ? AND activo = 1", new String[]{dniIngresado, passIngresada});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // MÉTODO PARA OBTENER DATOS DEL USUARIO LOGUEADO
    public Cursor obtenerDatosUsuarioLogueado(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE dni = ?", new String[]{dni});
    }

    // Home - Usuario

    public double obtenerTotalHistoricoPorUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(d.cantidad) FROM " + TABLE_DETALLE + " d " +
                "INNER JOIN " + TABLE_REGISTRO + " r ON d.id_registro = r.id_registro " +
                "WHERE r.id_usuario = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public Cursor obtenerRegistrosPorUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.nombre_categoria, d.cantidad, z.nombre_zona, r.fecha_hora " +
                "FROM " + TABLE_REGISTRO + " r " +
                "INNER JOIN " + TABLE_DETALLE + " d ON r.id_registro = d.id_registro " +
                "INNER JOIN " + TABLE_CATEGORIA + " c ON d.id_categoria = c.id_categoria " +
                "INNER JOIN " + TABLE_ZONA + " z ON r.id_zona = z.id_zona " +
                "WHERE r.id_usuario = ? " +
                "ORDER BY r.id_registro DESC";
        return db.rawQuery(query, new String[]{String.valueOf(idUsuario)});
    }

    // Home - Admin

    public double obtenerTotalKilosPorFecha(String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(d.cantidad) FROM " + TABLE_DETALLE + " d " +
                "INNER JOIN " + TABLE_REGISTRO + " r ON d.id_registro = r.id_registro " +
                "WHERE date(r.fecha_hora) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fecha});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public Cursor obtenerKilosPorCategoriaRango(String fechaDesde, String fechaHasta) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.nombre_categoria, SUM(d.cantidad) as total_kg " +
                "FROM " + TABLE_DETALLE + " d " +
                "INNER JOIN " + TABLE_REGISTRO + " r ON d.id_registro = r.id_registro " +
                "INNER JOIN " + TABLE_CATEGORIA + " c ON d.id_categoria = c.id_categoria " +
                "WHERE date(r.fecha_hora) BETWEEN ? AND ? " +
                "GROUP BY c.id_categoria";
        return db.rawQuery(query, new String[]{fechaDesde, fechaHasta});
    }

    // Pantalla Reporte
    public Cursor obtenerReportesFiltrados(String fechaDesde, String fechaHasta, int idCategoria, int idZona) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder query = new StringBuilder(
                "SELECT c.nombre_categoria, d.cantidad, z.nombre_zona, r.fecha_hora, u.nombre_completo " +
                        "FROM " + TABLE_DETALLE + " d " +
                        "INNER JOIN " + TABLE_REGISTRO + " r ON d.id_registro = r.id_registro " +
                        "INNER JOIN " + TABLE_CATEGORIA + " c ON d.id_categoria = c.id_categoria " +
                        "INNER JOIN " + TABLE_ZONA + " z ON r.id_zona = z.id_zona " +
                        "INNER JOIN " + TABLE_USUARIO + " u ON r.id_usuario = u.id_usuario " +
                        "WHERE date(r.fecha_hora) BETWEEN ? AND ? "
        );

        java.util.ArrayList<String> args = new java.util.ArrayList<>();
        args.add(fechaDesde);
        args.add(fechaHasta);

        if (idCategoria > 0) {
            query.append("AND c.id_categoria = ? ");
            args.add(String.valueOf(idCategoria));
        }
        if (idZona > 0) {
            query.append("AND z.id_zona = ? ");
            args.add(String.valueOf(idZona));
        }

        query.append("ORDER BY r.fecha_hora DESC");
        return db.rawQuery(query.toString(), args.toArray(new String[0]));
    }


    // =========================================================================
    // MÉTODOS CRUD - TABLA USUARIO
    // =========================================================================

    public boolean insertarUsuario(String nombreCompleto, String dni, String contrasena, String rol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_completo", nombreCompleto);
        values.put("dni", dni);
        values.put("contrasena", contrasena);
        values.put("rol", rol);
        long result = db.insert(TABLE_USUARIO, null, values);
        return result != -1;
    }

    // R - Obtener TODOS los Usuarios (Para Gestión Admin, muestra activos e inactivos)
    public Cursor obtenerUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USUARIO + " ORDER BY nombre_completo ASC", null);
    }

    public boolean actualizarUsuario(int idUsuario, String nombreCompleto, String dni, String contrasena, String rol, boolean activo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_completo", nombreCompleto);
        values.put("dni", dni);
        values.put("contrasena", contrasena);
        values.put("rol", rol);
        values.put("activo", activo ? 1 : 0);
        int rowsAffected = db.update(TABLE_USUARIO, values, "id_usuario = ?", new String[]{String.valueOf(idUsuario)});
        return rowsAffected > 0;
    }

    public boolean existeDNI(String dni, int idUsuarioActual) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] args;
        if (idUsuarioActual == -1) {
            query = "SELECT 1 FROM " + TABLE_USUARIO + " WHERE dni = ?";
            args = new String[]{dni};
        } else {
            query = "SELECT 1 FROM " + TABLE_USUARIO + " WHERE dni = ? AND id_usuario != ?";
            args = new String[]{dni, String.valueOf(idUsuarioActual)};
        }
        Cursor cursor = db.rawQuery(query, args);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // =========================================================================
    // MÉTODOS CRUD - TABLA CATEGORÍA
    // =========================================================================

    public boolean insertarCategoria(String nombreCategoria, String unidadMedida) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_categoria", nombreCategoria);
        values.put("unidad_medida", unidadMedida);
        long result = db.insert(TABLE_CATEGORIA, null, values);
        return result != -1;
    }

    // R - Obtener TODAS las categorías (Para Gestión Admin y Reportes)
    public Cursor obtenerCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIA + " ORDER BY nombre_categoria ASC", null);
    }

    public boolean actualizarCategoria(int idCategoria, String nombreCategoria, String unidadMedida, boolean activo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_categoria", nombreCategoria);
        values.put("unidad_medida", unidadMedida);
        values.put("activo", activo ? 1 : 0);
        int rowsAffected = db.update(TABLE_CATEGORIA, values, "id_categoria = ?", new String[]{String.valueOf(idCategoria)});
        return rowsAffected > 0;
    }

    // =========================================================================
    // MÉTODOS CRUD ZONAS
    // =========================================================================

    public boolean insertarZona(String nombreZona, String ubicacionEspecifica) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_zona", nombreZona);
        values.put("ubicacion_especifica", ubicacionEspecifica);
        long result = db.insert(TABLE_ZONA, null, values);
        return result != -1;
    }

    // R - Obtener TODAS las Zonas (Para Gestión Admin y Reportes)
    public Cursor obtenerZonas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ZONA + " ORDER BY nombre_zona ASC", null);
    }

    public boolean actualizarZona(int idZona, String nombreZona, String ubicacionEspecifica, boolean activo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_zona", nombreZona);
        values.put("ubicacion_especifica", ubicacionEspecifica);
        values.put("activo", activo ? 1 : 0);
        int rowsAffected = db.update(TABLE_ZONA, values, "id_zona = ?", new String[]{String.valueOf(idZona)});
        return rowsAffected > 0;
    }

    // =========================================================================
    // MÉTODOS DE REGISTRO / DETALLE
    // =========================================================================

    public long insertarRegistroCabecera(int idUsuario, int idZona) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put("id_usuario", idUsuario);
        values.put("id_zona", idZona);
        values.put("fecha_hora", fechaActual);
        return db.insert(TABLE_REGISTRO, null, values);
    }

    public boolean insertarDetalleResiduo(long idRegistro, int idCategoria, double cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_registro", idRegistro);
        values.put("id_categoria", idCategoria);
        values.put("cantidad", cantidad);
        long result = db.insert(TABLE_DETALLE, null, values);
        return result != -1;
    }

    public Cursor obtenerRegistrosCompletos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.id_registro, r.id_usuario, d.id_categoria, d.cantidad, r.fecha_hora, r.id_zona " +
                "FROM " + TABLE_REGISTRO + " r " +
                "INNER JOIN " + TABLE_DETALLE + " d ON r.id_registro = d.id_registro " +
                "ORDER BY r.id_registro DESC";
        return db.rawQuery(query, null);
    }

    // =========================================================================
    // MÉTODOS EXCLUSIVOS PARA EL OPERARIO (NUEVA RECOLECCIÓN - SOLO ACTIVOS)
    // =========================================================================

    public Cursor obtenerCategoriasActivas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIA + " WHERE activo = 1 ORDER BY nombre_categoria ASC", null);
    }

    public Cursor obtenerZonasActivas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ZONA + " WHERE activo = 1 ORDER BY nombre_zona ASC", null);
    }
}