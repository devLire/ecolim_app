package com.example.ecolim_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeUsuarioActivity extends AppCompatActivity {

    String dni;
    TextView labelBienvenida;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_usuario);

        dni = SessionManager.obtenerDNI(this);
        dbHelper = new DBHelper(this);

        labelBienvenida = findViewById(R.id.labelBienvenida);

        Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dni);

        if (cursor != null && cursor.moveToFirst()) {
            int idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));
            String rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"));

            labelBienvenida.setText("¡Bienvenido " + nombre + "!");

            cursor.close(); // ¡No olvides cerrar el cursor!
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}