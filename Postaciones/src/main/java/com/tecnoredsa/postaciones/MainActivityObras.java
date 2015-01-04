package com.tecnoredsa.postaciones;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by francisco on 03/06/14.
 */
public class MainActivityObras extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ObraFragment()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container_gps, new GpsFragment()).commit();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_generar_html);
        item.setVisible(false);
        item = menu.findItem(R.id.action_generar_kml);
        item.setVisible(false);
        MenuItem heart = menu.findItem(R.id.action_abrir_archivo);
        heart.setVisible(false);
        MenuItem compartir = menu.findItem(R.id.action_compartir);
        compartir.setVisible(false);
        MenuItem importar = menu.findItem(R.id.action_import_db);
        importar.setVisible(true);
        return true;

    }

}
