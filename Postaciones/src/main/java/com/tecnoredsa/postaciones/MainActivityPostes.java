package com.tecnoredsa.postaciones;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by francisco on 03/06/14.
 */
public class MainActivityPostes extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PostacionFragment()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.container_gps, new GpsFragment()).commit();
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem heart = menu.findItem(R.id.action_abrir_archivo);
        heart.setVisible(true);
        MenuItem compartir = menu.findItem(R.id.action_compartir);
        compartir.setVisible(true);
        MenuItem importar = menu.findItem(R.id.action_import_db);
        importar.setVisible(false);
        return true;
    }
*/
}
