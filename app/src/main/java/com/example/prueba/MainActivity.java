package com.example.prueba;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private Button btnRegistrar, btnIniciar;
    private TextInputLayout textNombre,textPassword;
    private TextInputEditText nombreEditText,passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegistrar = (Button) findViewById(R.id.Resgistrar);
        btnIniciar = (Button) findViewById(R.id.IniciarSesion);

        textNombre = (TextInputLayout) findViewById(R.id.textFieldUsuario);
        nombreEditText = (TextInputEditText) textNombre.getEditText();

        textPassword = (TextInputLayout) findViewById(R.id.textFieldPassword);
        passwordEditText = (TextInputEditText) textPassword.getEditText();

        Intent intentRegistrar = new Intent(this,Registrar.class);


        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    String nombre = nombreEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    iniciarSesion(nombre,password);
                    return true;
                }
                return false;
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivity(intentRegistrar);
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(nombreEditText.getText().toString().equals("") && passwordEditText.getText().toString().equals("")){

                    btnIniciar.setEnabled(true);
                }
                else {

                    String nombre = nombreEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    iniciarSesion(nombre,password);


                }


            }
        });

    }

    private void iniciarSesion(String nombre, String password) {

        Intent intentInicio = new Intent(this,RecyclerActivity.class);

        //Abrimos la base de datos, de forma escritura.
        ActivitySQLiteHelper acdbhInsert = new ActivitySQLiteHelper(MainActivity.this, "users",null,1);
        SQLiteDatabase dbInsert = acdbhInsert.getWritableDatabase();

        // Consulta SQL para buscar registros con el mismo nombre
        String query = "SELECT * FROM users WHERE user = ? AND password = ?";
        Cursor cursor = dbInsert.rawQuery(query, new String[]{nombre,password});


        if(cursor.getCount() > 0) {

            Toast.makeText(getApplicationContext(), "BIENVENIDO, " + nombre, Toast.LENGTH_SHORT).show();
            startActivity(intentInicio);
        }else{

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("ERROR");
            builder.setMessage("No se ha encontrado ningún usuario."+"\nIntentelo de nuevo");

            builder.setPositiveButton("Aceptar", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        nombreEditText.setText("");
        passwordEditText.setText("");
    }
}