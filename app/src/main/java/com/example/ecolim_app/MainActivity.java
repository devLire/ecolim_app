package com.example.ecolim_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    EditText txtDNI, txtContrasena;
    Button btnLogin;
    DBHelper dbHelper;
    String dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aplicar transparencia a la barra de estado
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Desactivar modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Aplicar color oscuro a los íconos de la barra de estado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_main);

        txtDNI = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DBHelper(this);
        dni = SessionManager.obtenerDNI(this);

        Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dni);

        if (cursor != null && cursor.moveToFirst()) {
            String rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"));

            if (rol.equals("Operario")){
                Intent intent = new Intent(this, HomeUsuarioActivity.class);
                startActivity(intent);
                finish();
            } else if (rol.equals("Supervisor")) {
                Intent intent = new Intent(this, HomeAdminActivity.class);
                startActivity(intent);
                finish();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Limpieza de datos
                String dni = txtDNI.getText().toString().trim();
                String password = txtContrasena.getText().toString().trim();

                // Validación de campos vacios
                if (dni.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor completa ambos campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3. Consultamos a la base de datos
                boolean accesoPermitido = dbHelper.verificarCredenciales(dni, password);

                if (accesoPermitido) {


                    Toast.makeText(
                            MainActivity.this,
                            "Login correcto",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Guardar el DNI de manera global
                    SessionManager.guardarDNI(MainActivity.this, dni);

                    Cursor cursor = dbHelper.obtenerDatosUsuarioLogueado(dni);

                    if (cursor != null && cursor.moveToFirst()) {
                        String rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"));

                        if (rol.equals("Operario")){
                            Intent intent = new Intent(MainActivity.this, HomeUsuarioActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (rol.equals("Supervisor")) {
                            Intent intent = new Intent(MainActivity.this, HomeAdminActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }
}