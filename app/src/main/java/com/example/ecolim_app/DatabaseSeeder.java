package com.example.ecolim_app;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseSeeder {

    public static void poblarDatos(SQLiteDatabase db) {

        // 1. Insertar Usuarios
        db.execSQL("INSERT INTO Usuario (nombre_completo, dni, contrasena, rol, activo) VALUES ('Ernesto Pérez', '12345678', '1234', 'Operario', 1)"); // ID 1
        db.execSQL("INSERT INTO Usuario (nombre_completo, dni, contrasena, rol, activo) VALUES ('Admin ECOLIM', '1234', 'admin', 'Supervisor', 1)"); // ID 2
        db.execSQL("INSERT INTO Usuario (nombre_completo, dni, contrasena, rol, activo) VALUES ('María Gómez', '87654321', '1234', 'Operario', 1)"); // ID 3

        // 2. Insertar Categorías
        db.execSQL("INSERT INTO Categoria_Residuo (nombre_categoria, unidad_medida, activo) VALUES ('Plástico', 'Kg', 1)"); // ID 1
        db.execSQL("INSERT INTO Categoria_Residuo (nombre_categoria, unidad_medida, activo) VALUES ('Orgánico', 'Kg', 1)"); // ID 2
        db.execSQL("INSERT INTO Categoria_Residuo (nombre_categoria, unidad_medida, activo) VALUES ('Papel', 'Kg', 1)"); // ID 3
        db.execSQL("INSERT INTO Categoria_Residuo (nombre_categoria, unidad_medida, activo) VALUES ('Peligroso', 'Litros', 1)"); // ID 4

        // 3. Insertar Zonas
        db.execSQL("INSERT INTO Zona_Limpieza (nombre_zona, ubicacion_especifica, activo) VALUES ('Almacén Principal', 'Planta Baja', 1)"); // ID 1
        db.execSQL("INSERT INTO Zona_Limpieza (nombre_zona, ubicacion_especifica, activo) VALUES ('Comedor', 'Piso 1', 1)"); // ID 2
        db.execSQL("INSERT INTO Zona_Limpieza (nombre_zona, ubicacion_especifica, activo) VALUES ('Oficinas Administrativas', 'Piso 2', 1)"); // ID 3
        db.execSQL("INSERT INTO Zona_Limpieza (nombre_zona, ubicacion_especifica, activo) VALUES ('Patio Trasero', 'Exterior', 1)"); // ID 4

        // 4. GENERACIÓN DE RECOLECCIONES

        int idRegistroActual = 1;

        for (int dia = 1; dia <= 11; dia++) {

            String diaString = (dia < 10) ? "0" + dia : String.valueOf(dia);
            String fechaBase = "2026-03-" + diaString;

            // --- TURNO MAÑANA: Ernesto (ID 1) en el Comedor (ID 2) ---
            db.execSQL("INSERT INTO Registro_Recoleccion (id_usuario, id_zona, fecha_hora) VALUES (1, 2, '" + fechaBase + " 08:30:00')");
            // Genera entre 1.0 y 4.0 kg de plástico
            double cantPlastico = Math.round((1.0 + Math.random() * 3.0) * 10.0) / 10.0;
            db.execSQL("INSERT INTO Detalle_Residuo_Recoleccion (id_registro, id_categoria, cantidad) VALUES (" + idRegistroActual + ", 1, " + cantPlastico + ")");
            // Genera entre 0.5 y 2.5 kg de orgánico
            double cantOrganico = Math.round((0.5 + Math.random() * 2.0) * 10.0) / 10.0;
            db.execSQL("INSERT INTO Detalle_Residuo_Recoleccion (id_registro, id_categoria, cantidad) VALUES (" + idRegistroActual + ", 2, " + cantOrganico + ")");
            idRegistroActual++;

            // --- TURNO TARDE: María (ID 3) en Oficinas (ID 3) ---
            db.execSQL("INSERT INTO Registro_Recoleccion (id_usuario, id_zona, fecha_hora) VALUES (3, 3, '" + fechaBase + " 15:00:00')");
            double cantPapel = Math.round((2.0 + Math.random() * 4.0) * 10.0) / 10.0;
            db.execSQL("INSERT INTO Detalle_Residuo_Recoleccion (id_registro, id_categoria, cantidad) VALUES (" + idRegistroActual + ", 3, " + cantPapel + ")");
            idRegistroActual++;

            // --- TURNO NOCHE: Operario alternado en el Patio (ID 4) ---
            int idOperarioNoche = (dia % 2 == 0) ? 1 : 3; // Si el día es par va Ernesto, si es impar va María
            db.execSQL("INSERT INTO Registro_Recoleccion (id_usuario, id_zona, fecha_hora) VALUES (" + idOperarioNoche + ", 4, '" + fechaBase + " 18:45:00')");
            double cantPeligroso = Math.round((0.5 + Math.random() * 1.5) * 10.0) / 10.0;
            db.execSQL("INSERT INTO Detalle_Residuo_Recoleccion (id_registro, id_categoria, cantidad) VALUES (" + idRegistroActual + ", 4, " + cantPeligroso + ")");
            double cantPlasticoNoche = Math.round((1.0 + Math.random() * 2.0) * 10.0) / 10.0;
            db.execSQL("INSERT INTO Detalle_Residuo_Recoleccion (id_registro, id_categoria, cantidad) VALUES (" + idRegistroActual + ", 1, " + cantPlasticoNoche + ")");
            idRegistroActual++;
        }
    }
}