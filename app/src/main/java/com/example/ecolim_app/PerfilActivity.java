package com.example.ecolim_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PerfilActivity extends AppCompatActivity {

    String dni;
    DBHelper dbHelper;
    TextView labelBienvenidaUsuario;
    TextInputEditText labelNombreUsuario, labelDniUsuario, labelRolUsuario;
    MaterialButton btnCerrarSesion;
    ImageView btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        dbHelper = new DBHelper(this);
        dni = SessionManager.obtenerDNI(this);

        labelBienvenidaUsuario = findViewById(R.id.labelBienvenidaUsuario);
        labelNombreUsuario = findViewById(R.id.labelNombreUsuario);
        labelDniUsuario = findViewById(R.id.labelDniUsuario);
        labelRolUsuario = findViewById(R.id.labelRolUsuario);

        btnRegresar = findViewById(R.id.btnRegresar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dni);

        if (cursor != null && cursor.moveToFirst()) {
            int idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));
            String rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"));

            labelBienvenidaUsuario.setText("¡Bienvenido " + nombre + "!");
            labelNombreUsuario.setText(nombre);
            labelDniUsuario.setText(dni);
            labelRolUsuario.setText(rol);

            cursor.close();
        }

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                SessionManager.cerrarSesion(PerfilActivity.this);
                // Limpiar la pila de screens
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}