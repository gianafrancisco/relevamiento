package com.fransis.model;

import android.graphics.Bitmap;

/**
 * Created by francisco on 03/10/2016.
 */
public class Imagen {
    private String filename;
    private Bitmap bitmap;
    private int numero;


    public Imagen(String filename, Bitmap bitmap, int numero) {
        this.filename = filename;
        this.bitmap = bitmap;
        this.numero = numero;
    }

    public String getFilename() {
        return filename;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getNumero() {
        return numero;
    }
}
