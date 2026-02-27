package com.example.ecolim_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usuario = etUsuario.getText().toString();
                String password = etPassword.getText().toString();

                if (usuario.equals("admin") && password.equals("1234")) {

                    Toast.makeText(
                            MainActivity.this,
                            "Login correcto 🔥",
                            Toast.LENGTH_SHORT
                    ).show();

                    // 👉 IR AL HOME
                    Intent intent = new Intent(
                            MainActivity.this,
                            HomeActivity.class
                    );
                    startActivity(intent);

                    // Opcional: cerrar login
                    finish();

                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Usuario o contraseña incorrectos ❌",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }
}