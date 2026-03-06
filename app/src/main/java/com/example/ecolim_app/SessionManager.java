package com.example.ecolim_app;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "RecoleccionPrefs";
    private static final String KEY_DNI = "dni_usuario";

    // Guardar el DNI
    public static void guardarDNI(Context context, String dni) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DNI, dni);
        editor.apply();
    }

    // Obtener el DNI
    public static String obtenerDNI(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_DNI, null); // Retorna null si no existe
    }

    // Cerrar sesión
    public static void cerrarSesion(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().clear().apply();
    }
}