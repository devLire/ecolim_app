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

    public static final String TABLE_ACTIVIDADES = "actividades";

    public static final String COL_ID = "id";
    public static final String COL_USUARIO = "usuario";
    public static final String COL_TIPO = "tipo_residuo";
    public static final String COL_CANTIDAD = "cantidad";
    public static final String COL_FECHA = "fecha";
    public static final String COL_OBSERVACION = "observacion";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_ACTIVIDADES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USUARIO + " TEXT, " +
                COL_TIPO + " TEXT, " +
                COL_CANTIDAD + " REAL, " +
                COL_FECHA + " TEXT, " +
                COL_OBSERVACION + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVIDADES);
        onCreate(db);
    }

    // INSERTAR CON FECHA AUTOMÁTICA
    public boolean insertarActividad(String usuario, String tipo, double cantidad, String observacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String fechaActual = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        values.put(COL_USUARIO, usuario);
        values.put(COL_TIPO, tipo);
        values.put(COL_CANTIDAD, cantidad);
        values.put(COL_FECHA, fechaActual);
        values.put(COL_OBSERVACION, observacion);

        long result = db.insert(TABLE_ACTIVIDADES, null, values);
        return result != -1;
    }

    public Cursor obtenerActividades() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ACTIVIDADES + " ORDER BY " + COL_ID + " DESC",
                null
        );
    }
}