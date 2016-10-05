package com.fransis.postaciones;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;

import com.fransis.adapter.PostesArrayAdapter;
import com.fransis.backend.ObraSeleccionada;
import com.fransis.helper.GpsHelperCoordinates;
import com.fransis.helper.PostesComparator;
import com.fransis.helper.SqlHelperRelevamiento;
import com.fransis.model.Obra;
import com.fransis.model.Postacion;
import com.fransis.task.ExportKMLFilesTask;
import com.fransis.task.MakeHTMLFilesTask;
import com.fransis.task.MakeKMLFilesTask;
import com.fransis.task.ViewKMLFilesTask;
import com.fransis.utils.Imagen;

import android.util.Log;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;
    private LocationListener locationListener=null;
    private LocationManager locationManager=null;
    private GpsHelperCoordinates gps=null;
    final private int MY_FILECHOOSER_CODE = 1;
    private ExifInterface exif = null;
    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.v("DebugAPP","Instanciamos la base de datos");
        SqlHelperRelevamiento dbRelevamiento=SqlHelperRelevamiento.getInstance(getApplicationContext());

        locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        gps = new GpsHelperCoordinates();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_compartir);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_compartir) {

            Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
            if(o == null){
                Toast.makeText(getApplicationContext(),R.string.seleccione_una_obra,Toast.LENGTH_LONG).show();
            }else{
                String shareBody=null;
                new ExportKMLFilesTask(this, gps).execute();

            return true;

            }
        }else if (id == R.id.action_generar_html) {
            Obra o = ObraSeleccionada.getInstance().getObraSeleccionada();
            if (o == null) {
                Toast.makeText(getApplicationContext(), R.string.seleccione_una_obra, Toast.LENGTH_LONG).show();
            } else {
                String shareBody = null;
                new MakeHTMLFilesTask(this).execute();
                return true;

            }
        }else if (id == R.id.action_generar_kml) {
                Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
                if(o == null){
                    Toast.makeText(getApplicationContext(),R.string.seleccione_una_obra,Toast.LENGTH_LONG).show();
                }else{
                    String shareBody=null;
                    new MakeKMLFilesTask(this, gps).execute();
                    return true;

                }
        }else if(id == R.id.action_abrir_archivo){
            Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
            if(o == null){
                Toast.makeText(getApplicationContext(),R.string.seleccione_una_obra,Toast.LENGTH_LONG).show();
            }else {

                new ViewKMLFilesTask(this, gps).execute();
            }
        }else if(id== R.id.action_import_db){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, MY_FILECHOOSER_CODE);
        }
        else if(id== R.id.action_exportar_db){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("application/octet-stream");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Base de datos");
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            CopyFile(getDatabasePath("relevamiento.db").getAbsolutePath().toString(),storageDir+"/relevamiento_"+timeStamp+".db");
            File file = new File(storageDir+"/relevamiento_"+timeStamp+".db");
            ArrayList<Uri> u = new ArrayList<Uri>();
            if (file.exists() && file.canRead()) {
                u.add(Uri.fromFile(file));
            }
            sharingIntent.putExtra(Intent.EXTRA_STREAM, u);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_compartir)));
        }
        return super.onOptionsItemSelected(item);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_FILECHOOSER_CODE:
                if (resultCode == RESULT_OK) {
                    Uri filename = data.getData();
                    Log.v("DebugAPP",filename.getPath());
                    File file = new File(filename.getPath());
                    File file_db = new File(getDatabasePath("relevamiento.db").getAbsolutePath());
                    byte[] buffer = new byte[1024];
                    if (file.exists() && file.canRead()) {
                        FileInputStream fin = null;
                        FileOutputStream fout = null;
                        try {
                            file_db.createNewFile();
                            fout= new FileOutputStream(file_db);
                            fin = new FileInputStream(file);
                            int length;
                            while((length = fin.read(buffer)) > 0)
                            {
                                fout.write(buffer, 0, length);
                            }
                            fout.close();
                            fin.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                break;
        }
                super.onActivityResult(requestCode, resultCode, data);
    }

    private void CopyFile(String src,String dst){
        File file = new File(src);
        File file_db = new File(dst);
        byte[] buffer = new byte[1024];
        if (file.exists() && file.canRead()) {
            FileInputStream fin = null;
            FileOutputStream fout = null;
            try {
                file_db.createNewFile();
                fout= new FileOutputStream(file_db);
                fin = new FileInputStream(file);
                int length;
                while((length = fin.read(buffer)) > 0)
                {
                    fout.write(buffer, 0, length);
                }
                fout.close();
                fin.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}