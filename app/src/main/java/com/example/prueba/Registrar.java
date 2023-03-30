package com.example.prueba;

import android.Manifest;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Registrar extends AppCompatActivity implements View.OnClickListener {

    private Button btnCamara, btnGaleria, btnRegistrar;
    private TextInputLayout textFecha, textNombre, textPassWord;
    private TextInputEditText fechaEditText, nombreEditText, passwordEditText;
    private String fechaBaseDatos;
    private ImageView imageView;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 1001;

    private Uri imageUri;
    private String imagePath = null;

    private String nombre_recuperado;
    private String fecha_recuperado;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song);

        btnCamara = findViewById(R.id.btnCamara);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        textFecha = findViewById(R.id.textFieldFecha);
        fechaEditText = (TextInputEditText) textFecha.getEditText();

        textNombre = findViewById(R.id.textFieldUsuario);
        nombreEditText = (TextInputEditText) textNombre.getEditText();

        textPassWord = findViewById(R.id.textFieldPassword);
        passwordEditText = (TextInputEditText) textPassWord.getEditText();

        btnRegistrar.setOnClickListener(this);

        Bundle datos = this.getIntent().getExtras();
        if (this.getIntent().hasExtra("nombre")) {
            nombre_recuperado = datos.getString("nombre");
            fecha_recuperado = datos.getString("fecha");
        }


        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        fechaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                fechaEditText.setText(materialDatePicker.getHeaderText());
            }
        });

        imageView = findViewById(R.id.imageView);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Registrar.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Registrar.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnRegistrar) {

            mediaPlayer.start();

            if (this.getIntent().hasExtra("nombre")) {

                //Abrimos la base de datos, de forma escritura.
                ActivitySQLiteHelper acdbhComprobar = new ActivitySQLiteHelper(Registrar.this, "users", null, 1);
                SQLiteDatabase dbComprobar = acdbhComprobar.getWritableDatabase();

                // Consulta SQL para buscar registros con el mismo nombre
                String query = "SELECT * FROM users WHERE user = ?";
                Cursor cursor = dbComprobar.rawQuery(query, new String[]{nombre_recuperado});


                if (cursor.getCount() > 0) {
                    // Ya existe un registro con el mismo nombre

                    //Abrimos la base de datos, de forma escritura.
                    ActivitySQLiteHelper acdbhActu = new ActivitySQLiteHelper(Registrar.this, "users", null, 1);
                    SQLiteDatabase dbActu = acdbhActu.getWritableDatabase();

                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    byte[] bytes = getBytes(inputStream);

                    ContentValues values = new ContentValues();
                    values.put("user", nombreEditText.getText().toString());
                    values.put("password", passwordEditText.getText().toString());
                    values.put("date", fechaEditText.getText().toString());
                    values.put("photo", bytes);

                    int cantidadActualizada = dbComprobar.update("users", values, "user = ?", new String[]{nombre_recuperado});

                    if (cantidadActualizada > 0) {

                        Toast.makeText(getApplicationContext(), "Se ha actualizado!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registrar.this,RecyclerActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No se ha actualizado!", Toast.LENGTH_SHORT).show();
                    }

                    dbComprobar.close();
                    cursor.close();
                }

            }


            String nombre = nombreEditText.getText().toString();

            //Abrimos la base de datos, de forma escritura.
            ActivitySQLiteHelper acdbhInsert = new ActivitySQLiteHelper(Registrar.this, "users", null, 1);
            SQLiteDatabase dbInsert = acdbhInsert.getWritableDatabase();

            // Consulta SQL para buscar registros con el mismo nombre
            String consulta = "SELECT * FROM users WHERE user = ?";
            Cursor cursorConsulta = dbInsert.rawQuery(consulta, new String[]{nombre});

            if (cursorConsulta.getCount() > 0) {
                // Ya existe un registro con el mismo nombre, mostrar toast
                Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese nombre", Toast.LENGTH_SHORT).show();
            } else {
                // No existe un registro con el mismo nombre, insertar en la base de datos
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] bytes = getBytes(inputStream);

                ContentValues values = new ContentValues();
                values.put("user", nombreEditText.getText().toString());
                values.put("password", passwordEditText.getText().toString());
                values.put("date", fechaEditText.getText().toString());
                values.put("photo", bytes);

                long result = dbInsert.insert("users", null, values);

                Toast.makeText(getApplicationContext(), "Se ha registrado con existo!", Toast.LENGTH_SHORT).show();
            }

            cursorConsulta.close();
            dbInsert.close();
            finish();
        }

    }

    private byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while (true) {
            try {
                if (!((len = inputStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.prueba.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imagePath = image.getAbsolutePath();
        return image;
    }

    private void dispatchPickPictureIntent() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPictureIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Show the photo on the ImageView
                setPic();
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                // Get the Uri of the selected image
                imageUri = data.getData();
                // Set the image on the ImageView
                imageView.setImageURI(imageUri);
            }
        }
    }

    private void setPic() {

        // Establece el tamaño de destino de la imagen
        int targetW = 1000;
        int targetH = 1000;

        // Obtiene las dimensiones de la imagen original
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Calcula el factor de escala para que la imagen se ajuste al tamaño de destino
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decodifica la imagen original con el factor de escala calculado
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Advertencia");
        builder.setMessage("¿Desea volver hacia atrás?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                salir();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void salir() {

        super.onBackPressed();

    }

}
