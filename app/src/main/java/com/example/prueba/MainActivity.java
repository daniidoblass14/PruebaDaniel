package com.example.prueba;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
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

    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Canal de notificación", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripción del canal de notificación");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

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
        createNotificacion();

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

    private void createNotificacion() {
        String nombre = nombreEditText.getText().toString();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Inicio de Sesión");
        builder.setContentText("Bienvenido de nuevo, "+nombre);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA,1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID,builder.build());
    }
}