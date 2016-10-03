package com.fransis.postaciones;

import android.os.Bundle;

/**
 * Created by francisco on 03/06/14.
 */
public class MainActivityImagen extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ImagenFragment()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container_gps, new GpsFragment()).commit();
        }
    }

}
