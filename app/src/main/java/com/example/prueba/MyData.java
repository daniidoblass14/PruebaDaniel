package com.example.prueba;

import android.graphics.Bitmap;

public class MyData {

    private String nombre;
    private String fecha;
    private Bitmap image;

    public MyData(String title, String fecha, Bitmap imageResource) {
        this.nombre = title;
        this.fecha = fecha;
        this.image = imageResource;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Bitmap getImage() {

        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImageBitmap() {
        return image;
    }
}
