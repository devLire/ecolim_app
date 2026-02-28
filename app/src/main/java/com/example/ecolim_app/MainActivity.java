package com.example.ecolim_app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etPassword;
    Button btnLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aplicar transparencia a la barra de estado
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Aplicar color oscuro a los íconos de la barra de estado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 2. Inicializamos la base de datos
        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Limpieza de datos
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validación de campos vacios
                if (usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor completa ambos campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3. Consultamos a la base de datos real
                boolean accesoPermitido = dbHelper.verificarCredenciales(usuario, password);

                if (accesoPermitido) {

                    Toast.makeText(
                            MainActivity.this,
                            "Login correcto",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Redirigir al Home
                    Intent intent = new Intent(
                            MainActivity.this,
                            HomeActivity.class
                    );
                    startActivity(intent);

                    // Cerrar login
                    finish();

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