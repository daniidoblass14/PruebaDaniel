package com.example.prueba;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class RecyclerActivity extends AppCompatActivity implements RecycleInterface.RecycleViewOnClick {

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recy_layout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MyData> listaPersonas = obtenerPersonasDeLaBaseDeDatos(); // aquí debes implementar tu propia lógica para obtener los datos de la base de datos
        MyAdapter adapter = new MyAdapter(listaPersonas);
        recyclerView.setAdapter(adapter);

        adapter.setOnClick(new MyAdapter.onItemClick() {
            @Override
            public void onItemClick(MyData data) {
                if (data != null) {

                    Intent intentEditar = new Intent(RecyclerActivity.this, Registrar.class);
                    intentEditar.putExtra("nombre",data.getNombre());
                    intentEditar.putExtra("fecha",data.getFecha());
                    startActivity(intentEditar);
                    finish();
                }
            }
        });

        adapter.setOnLongClick(new MyAdapter.onItemLongClick() {
            @Override
            public void onItemLongClick(MyData data) {

                String nombre = data.getNombre();

                //Abrimos la base de datos, de forma escritura.
                ActivitySQLiteHelper acdbh = new ActivitySQLiteHelper(RecyclerActivity.this, "users",null,1);
                SQLiteDatabase db = acdbh.getWritableDatabase();

                try{

                    db.execSQL("DELETE FROM users WHERE user ='"+nombre+"'");
                    Intent refresh = new Intent(RecyclerActivity.this, RecyclerActivity.class);
                    startActivity(refresh);

                }catch (Exception e){
                    Log.e(TAG, "Error al eliminar usuario: " + e.getMessage());
                }


                acdbh.close();
                db.close();
                finish();
            }
        });



    }

    private List<MyData> obtenerPersonasDeLaBaseDeDatos() {

        List<MyData> listaPersonas = new ArrayList<>();

        ActivitySQLiteHelper acdbh = new ActivitySQLiteHelper(RecyclerActivity.this, "users",null,1);
        SQLiteDatabase db = acdbh.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT user, date, photo FROM users",null);

        while (cursor.moveToNext()) {
            String user = cursor.getString(0);
            String date = cursor.getString(1);
            byte[] photoBytes = cursor.getBlob(2);
            Bitmap photoBitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
            MyData data = new MyData(user, date, photoBitmap);
            listaPersonas.add(data);
        }
        // Cerrar el cursor y la base de datos
        cursor.close();
        db.close();

        return listaPersonas;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }
}
