package com.tecnoredsa.postaciones;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.tecnoredsa.adapter.PostesArrayAdapter;
import com.tecnoredsa.backend.ObraSeleccionada;
import com.tecnoredsa.helper.PostesComparator;
import com.tecnoredsa.helper.SqlHelperRelevamiento;
import com.tecnoredsa.model.Obra;
import com.tecnoredsa.model.Postacion;

import android.util.Log;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;
    private LocationListener locationListener=null;
    private LocationManager locationManager=null;
    final private int MY_FILECHOOSER_CODE = 1;
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
                new ExportKMLFilesTask().execute();

            return true;

            }
        }else if (id == R.id.action_generar_html) {
            Obra o = ObraSeleccionada.getInstance().getObraSeleccionada();
            if (o == null) {
                Toast.makeText(getApplicationContext(), R.string.seleccione_una_obra, Toast.LENGTH_LONG).show();
            } else {
                String shareBody = null;
                new MakeHTMLFilesTask().execute();
                return true;

            }
        }else if (id == R.id.action_generar_kml) {
                Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
                if(o == null){
                    Toast.makeText(getApplicationContext(),R.string.seleccione_una_obra,Toast.LENGTH_LONG).show();
                }else{
                    String shareBody=null;
                    new MakeKMLFilesTask().execute();
                    return true;

                }
        }else if(id == R.id.action_abrir_archivo){
            Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
            if(o == null){
                Toast.makeText(getApplicationContext(),R.string.seleccione_una_obra,Toast.LENGTH_LONG).show();
            }else {

                new ViewKMLFilesTask().execute();
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


    private class MakeKMLFilesTask extends ExportKMLFilesTask{

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setMessage("Generando archivos kml.");
            mySelf=this;
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySelf.cancel(true);
                }
            });
            dialog.show();
            openEarth = true;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            if(result == 0) {

            }

        }
    }

    private class ViewKMLFilesTask extends ExportKMLFilesTask{

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setMessage("Generando archivos para abrir.");
            mySelf=this;
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySelf.cancel(true);
                }
            });
            dialog.show();
            openEarth = true;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //super.onPostExecute(result);

            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            if(result == 0) {
                Obra o = ObraSeleccionada.getInstance().getObraSeleccionada();
                File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, o.getNombre() + ".kml");
                if (file.exists() && file.canRead()) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.google-earth.kml+xml");
                    startActivity(intent);
                }
            }

        }
    }


    private class ExportKMLFilesTask extends AsyncTask<Void, Integer, Integer> {

        protected ProgressDialog dialog;
        protected ExportKMLFilesTask mySelf;
        protected Boolean openEarth = false;
        private Intent sharingIntent;
        /*protected DialogInterface.OnClickListener onCancel= new DialogInterface.OnClickListener(

        );*/

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setMessage("Generando archivos para exportar...");
            mySelf=this;
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySelf.cancel(true);
                }
            });
            dialog.show();
            super.onPreExecute();
        }

        protected Integer doInBackground(Void... urls) {
            int progress=0;
            int progressCount=0;
            Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
            PostesArrayAdapter p = SqlHelperRelevamiento.getInstance(getApplicationContext()).getAdapterPostacion(o);
            p.sort(new PostesComparator());
            if(!openEarth.booleanValue())
                progressCount=p.getCount()*2;
            else
                progressCount=p.getCount();

            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File fileo = new File(path, o.getNombre() + ".kml");
            FileOutputStream fo = null;
            File filecsvo = new File(path, o.getNombre() + ".csv");
            FileOutputStream fcsvo = null;
/*
            File filehtmlo = new File(path, o.getNombre() + ".html");
            FileOutputStream fhtmlo = null;
*/
            //Log.v("DebugAPP", fileo.getAbsoluteFile().toString());
            //Log.v("DebugAPP", fileo.getAbsolutePath().toString());
            try {
                fileo.createNewFile();
                fo = new FileOutputStream(fileo);
                filecsvo.createNewFile();
                fcsvo = new FileOutputStream(filecsvo);
/*
                filehtmlo.createNewFile();
                fhtmlo = new FileOutputStream(filehtmlo);
*/

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
/*
            String html="<html><body style=\"font-family:Verdana,sans-serif;\"><table>";
*/
            String str = "<kml><Document>" +
                    "<Style id=\"PA\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/triangle.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"PC\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"PMa\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/placemark_square.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"PF\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/open-diamond.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"PMe\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/polygon.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"PO\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/arrow.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"G\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/target.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"E\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/cross-hairs.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"N\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png</href> </Icon></IconStyle></Style>" +
                    "<Style id=\"MP\"><IconStyle> <Icon> <href>http://maps.google.com/mapfiles/kml/shapes/info-i.png</href> </Icon></IconStyle></Style>" +
                    "<Folder><name>" + o.getNombre() + "</name><open>1</open>";
            str+="<description><p>Referencias</p>" +
                    "<table><thead><tr><th>Icono</th><th>Codigo</th><th>Descripcion</th></tr></thead>"+
                    "<tbody>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/triangle.png' /></td><td>PA</td><td>Poste de alumbrado</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png' /></td><td>PC</td><td>Poste de cemento</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/placemark_square.png' /></td><td>PMa</td><td>Poste de madera</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/open-diamond.png' /></td><td>PF</td><td>Fachada</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/polygon.png' /></td><td>PMe</td><td>Mensula</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/arrow.png' /></td><td>PO</td><td>Otro tipo de poste</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/target.png' /></td><td>G</td><td>Ganancia</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/cross-hairs.png' /></td><td>E</td><td>Empalme</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png' /></td><td>N</td><td>Poste nuevo</td></tr>"+
                    "<tr><td><img src='http://maps.google.com/mapfiles/kml/shapes/info-i.png' /></td><td>MP</td><td>Mensula prolongada</td></tr>"+
                    "</tbody>"+
                    "</table>"+
                    "</description>";
            //str+="<Style id=\"PA\"><IconStyle> <Icon> <href>icons/PA.png</href> </Icon></IconStyle></Style><Style id=\"PC\"><IconStyle> <Icon> <href>icons/PC.png</href> </Icon></IconStyle></Style><Style id=\"PMa\"><IconStyle> <Icon> <href>icons/PMa.png</href> </Icon></IconStyle></Style><Style id=\"PF\"><IconStyle> <Icon> <href>icons/PF.png</href> </Icon></IconStyle></Style><Style id=\"PMe\"><IconStyle> <Icon> <href>icons/PMe.png</href> </Icon></IconStyle></Style><Style id=\"PO\"><IconStyle> <Icon> <href>icons/PO.png</href> </Icon></IconStyle></Style><Style id=\"G\"><IconStyle> <Icon> <href>icons/G.png</href> </Icon></IconStyle></Style><Style id=\"E\"><IconStyle> <Icon> <href>icons/E.png</href> </Icon></IconStyle></Style><Style id=\"N\"><IconStyle> <Icon> <href>icons/N.png</href> </Icon></IconStyle></Style><Style id=\"MP\"><IconStyle> <Icon> <href>icons/MP.png</href> </Icon></IconStyle></Style>";
            String  csv = "Codificación," +
                    getResources().getString(R.string.tipo_poste)+","+
                    getResources().getString(R.string.tipo_preformada)+","+
                    getResources().getString(R.string.agregar_poste)+","+
                    getResources().getString(R.string.ganancia)+","+
                    getResources().getString(R.string.empalme)+","+
                    getResources().getString(R.string.mensula_prologada)+","+
                    getResources().getString(R.string.detalles_adicionales)+"\n";

            if(p != null) {
                for (int i = 0; i < p.getCount(); i++) {
                    if (isCancelled()) return -1;
                    publishProgress((int) ((progress / (float)progressCount) * 100));
                    progress++;
                    Postacion po = (Postacion) p.getItem(i);
                    String imgList="";
                    String imgListHtml="";
                    File f=new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+o.getNombre()+"/"+po.getPostacion_id().toString());
                    //Log.v("DebugAPP",f.getAbsolutePath().toString());

                    File files[] = f.listFiles();
                    if(files != null) {
                        for (int j = 0; j < files.length; j++) {
                            //Log.v("DebugAPP",files[j].getName().toString());
                            imgList += "<tr><td><p><img src=\"" +
                                    o.getNombre() + "/" +
                                    (openEarth.booleanValue()?po.getPostacion_id().toString():po.getCodificacion()) + "/" +
                                    files[j].getName() +
                                    "\" style=\""+(openEarth.booleanValue()?"width:220px; height: 124px;":"width:400px; height: 225px;")+"\"></img></p></td></tr>";
/*
                            imgListHtml += "<tr><td><p><img src=\"" +
                                    o.getNombre() + "/" +
                                    (openEarth.booleanValue()?po.getPostacion_id().toString():po.getCodificacion()) + "/" +
                                    files[j].getName() +
                                    "\" style=\"width:300px; height: 169px;\"></img></p></td></tr>";
*/

                        }
                    }

                    csv+=po.getCodificacion()+",";
                    csv+=po.getTipo()+",";
                    csv+=po.getPreformada()+",";
                    csv+=po.getAgregar()+",";
                    csv+=po.getGanancia()+",";
                    csv+=po.getEmpalme()+",";
                    csv+=po.getMensula_prolongada()+",";
                    csv+="\""+po.getDetalle_adicional()+"\"\n";

/*                    if(!imgList.equals("")) {

                        String detalle = getResources().getString(R.string.tipo_poste) + ": " + po.getTipo() + "</br>\n" +
                                getResources().getString(R.string.tipo_preformada) + ": " + po.getPreformada() + "</br>\n" +
                                getResources().getString(R.string.agregar_poste) + ": " + po.getAgregar() + "</br>\n" +
                                getResources().getString(R.string.ganancia) + ": " + po.getGanancia() + "</br>\n" +
                                getResources().getString(R.string.empalme) + ": " + po.getEmpalme() + "</br>\n" +
                                getResources().getString(R.string.mensula_prologada) + ": " + po.getMensula_prolongada() + "</br>\n" +
                                getResources().getString(R.string.detalles_adicionales) + ": " + po.getDetalle_adicional() + "</br>\n";


                        html += "<tr><td colspan=2><h3 style=\"background: #efefef\">" + po.getCodificacion() + "</h3></td></tr>";
                        html += "<tr><td style=\"vertical-align:text-top;\">" + detalle + "</td><td><table>" + imgListHtml + "</table></td></tr>";
                    }
*/
                    str += "<Placemark>";
                    str += "<name>" + po.getCodificacion() + "</name>";
                    str += "<description>" +
                            "<table><tbody><tr><td>" +
                            "<p>Realizado por <a href='http://www.tecnoredsa.com.ar'>Tecnored S.A.</a></p>\n" +
                            "<p></p>\n" +
                            "<p>"+getResources().getString(R.string.tipo_poste) + ": " + po.getTipo() + "</p>\n" +
                            "<p>"+getResources().getString(R.string.tipo_preformada) + ": " + po.getPreformada() + "</p>\n" +
                            "<p>"+getResources().getString(R.string.agregar_poste) + ": " + po.getAgregar() + "</p>\n" +
                            "<p>"+getResources().getString(R.string.ganancia) + ": " + po.getGanancia() + "</p>\n" +
                            "<p>"+getResources().getString(R.string.empalme) + ": " + po.getEmpalme() + "</p>\n" +
                            "<p>"+getResources().getString(R.string.mensula_prologada) + ": " + po.getMensula_prolongada() + "</p>\n" +
                            "<p>"+getResources().getString(R.string.detalles_adicionales) + ": " + po.getDetalle_adicional() + "</p>\n" +
                            "</td></tr>" +
                            imgList +
                            "<tr><td>" +
                            "<p>Realizado por <a href='http://www.tecnoredsa.com.ar'>Tecnored S.A.</a></p>\n" +
                            "<p></p>\n" +
                            "</td></tr>" +
                            "</tbody></table>" +
                            "</description>";
                    str += "<LookAt>";
                    str += "<longitude>" + po.getGps_longitude() + "</longitude>";
                    str += "<latitude>" + po.getGps_latitude() + "</latitude>";
                    str += "<altitude>0</altitude>";
                    str += "</LookAt>";
                    str+="<styleUrl>#"+po.getImagenTipo()+"</styleUrl>";
                    str += "<Point>";
                    str += "<coordinates>" + po.getGps_longitude() + "," + po.getGps_latitude() + ",0</coordinates>";
                    str += "</Point>";
                    str += "</Placemark>";
                }
            }
            str += "</Folder></Document></kml>";
/*
            html+="</table></body></html>";
*/
            try {

                fo.write(str.getBytes());
                fo.close();
                fcsvo.write(csv.getBytes());
                fcsvo.close();
/*
                fhtmlo.write(html.getBytes());
                fhtmlo.close();
*/
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*

            Generacion KMZ

            */
            if(!openEarth.booleanValue()) {
                p = SqlHelperRelevamiento.getInstance(getApplicationContext()).getAdapterPostacion(o);
                path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                fileo = new File(path, o.getNombre() + ".kmz");
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(fileo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ZipOutputStream zout = new ZipOutputStream(fout);
                byte[] buffer = new byte[1024];
                if (p != null) {
                    for (int i = 0; i < p.getCount(); i++) {
                        if (isCancelled()) return -1;
                        publishProgress((int) ((progress / (float) progressCount) * 100));
                        progress++;
                        Postacion po = (Postacion) p.getItem(i);
                        String imgList = "";
                        File f = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + o.getNombre() + "/" + po.getPostacion_id().toString());
                        //Log.v("DebugAPP", f.getAbsolutePath().toString());

                        File files[] = f.listFiles();
                        if (files != null) {
                            for (int j = 0; j < files.length; j++) {
                                FileInputStream fin = null;
                                try {
                                    fin = new FileInputStream(files[j]);
                                    //Log.v("DebugAPP", files[j].getName());
                                    zout.putNextEntry(new ZipEntry(o.getNombre() + "/" + po.getCodificacion() + "/" + files[j].getName()));
                                    int length;
                                    while ((length = fin.read(buffer)) > 0) {
                                        zout.write(buffer, 0, length);
                                    }
                                    zout.closeEntry();
                                    fin.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    File file_kml = new File(path, o.getNombre() + ".kml");
                    //Log.v("DebugAPP",file_kml.getName());
                    FileInputStream fin = null;
                    try {
                        fin = new FileInputStream(file_kml);
                        zout.putNextEntry(new ZipEntry(file_kml.getName()));
                        int length;
                        while ((length = fin.read(buffer)) > 0) {
                            zout.write(buffer, 0, length);
                        }
                        zout.closeEntry();
                        fin.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File file_html = new File(path, o.getNombre() + ".html");
                    fin = null;
                    try {
                        fin = new FileInputStream(file_html);
                        zout.putNextEntry(new ZipEntry(file_html.getName()));
                        int length;
                        while ((length = fin.read(buffer)) > 0) {
                            zout.write(buffer, 0, length);
                        }
                        zout.closeEntry();
                        fin.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File file_db = new File(getDatabasePath("relevamiento.db").getAbsolutePath());
                    Log.v("DebugAPP", file_db.getAbsolutePath());
                    fin = null;
                    try {
                        fin = new FileInputStream(file_db);
                        zout.putNextEntry(new ZipEntry(file_db.getName()));
                        int length;
                        while ((length = fin.read(buffer)) > 0) {
                            zout.write(buffer, 0, length);
                        }
                        zout.closeEntry();
                        fin.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                try {
                    zout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Integer result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            if(result == 0) {
                Obra o = ObraSeleccionada.getInstance().getObraSeleccionada();
                sharingIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("application/octet-stream");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.action_compartir) + " " + o.getNombre());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, o.getNombre() + ".kml");

                File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                ArrayList<Uri> u = new ArrayList<Uri>();
                File file = new File(path, o.getNombre() + ".kml");
                if (file.exists() && file.canRead()) {
                    u.add(Uri.fromFile(file));
                }
                File filecsv = new File(path, o.getNombre() + ".csv");
                if (filecsv.exists() && filecsv.canRead()) {
                    u.add(Uri.fromFile(filecsv));
                }
                File filekmz = new File(path, o.getNombre() + ".kmz");
                if (filekmz.exists() && filekmz.canRead()) {
                    u.add(Uri.fromFile(filekmz));
                }
                if (u.size() > 0) {
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, u);
                }
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_compartir)));
            }
        }
    }


    private class MakeHTMLFilesTask extends AsyncTask<Void, Integer, Integer> {

        protected ProgressDialog dialog;
        protected MakeHTMLFilesTask mySelf;
        protected Boolean openEarth = false;
        private Intent sharingIntent;
        /*protected DialogInterface.OnClickListener onCancel= new DialogInterface.OnClickListener(

        );*/

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setMessage("Generando html...");
            mySelf=this;
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySelf.cancel(true);
                }
            });
            dialog.show();
            super.onPreExecute();
        }

        protected Integer doInBackground(Void... urls) {
            int progress=0;
            int progressCount=0;
            Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
            PostesArrayAdapter p = SqlHelperRelevamiento.getInstance(getApplicationContext()).getAdapterPostacion(o);
            p.sort(new PostesComparator());
            progressCount=p.getCount();


            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File filehtmlo = new File(path, o.getNombre() + ".html");
            FileOutputStream fhtmlo = null;
            try {
                filehtmlo.createNewFile();
                fhtmlo = new FileOutputStream(filehtmlo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String html="<html><body style=\"font-family:Verdana,sans-serif;\"><table>";


            if(p != null) {
                for (int i = 0; i < p.getCount(); i++) {
                    if (isCancelled()) return -1;
                    publishProgress((int) ((progress / (float)progressCount) * 100));
                    progress++;
                    Postacion po = (Postacion) p.getItem(i);
                    String imgListHtml="";
                    File f=new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+o.getNombre()+"/"+po.getPostacion_id().toString());

                    File files[] = f.listFiles();
                    if(files != null) {
                        for (int j = 0; j < files.length; j++) {
                            //(openEarth.booleanValue()?po.getPostacion_id().toString():po.getCodificacion())
                            if(files[j].length() > 0) {
                                imgListHtml += "<tr><td><p><img src=\"" +
                                        o.getNombre() + "/" +
                                        po.getCodificacion() + "/" +
                                        files[j].getName() +
                                        "\" style=\"width:300px; height: 169px;\"></img></p></td></tr>";
                            }
                        }
                    }

                    if(!imgListHtml.equals("")) {

                        String detalle = getResources().getString(R.string.tipo_poste) + ": " + po.getTipo() + "</br>\n" +
                                getResources().getString(R.string.tipo_preformada) + ": " + po.getPreformada() + "</br>\n" +
                                getResources().getString(R.string.agregar_poste) + ": " + po.getAgregar() + "</br>\n" +
                                getResources().getString(R.string.ganancia) + ": " + po.getGanancia() + "</br>\n" +
                                getResources().getString(R.string.empalme) + ": " + po.getEmpalme() + "</br>\n" +
                                getResources().getString(R.string.mensula_prologada) + ": " + po.getMensula_prolongada() + "</br>\n" +
                                getResources().getString(R.string.detalles_adicionales) + ": " + po.getDetalle_adicional() + "</br>\n";


                        html += "<tr><td colspan=2><h3 style=\"background: #efefef\">" + po.getCodificacion() + "</h3></td></tr>";
                        html += "<tr><td style=\"vertical-align:text-top;\">" + detalle + "</td><td><table>" + imgListHtml + "</table></td></tr>";
                    }

                }
            }
            html+="</table></body></html>";
            try {

                fhtmlo.write(html.getBytes());
                fhtmlo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Integer result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            if(result == 0) {
                Obra o = ObraSeleccionada.getInstance().getObraSeleccionada();
                sharingIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("application/octet-stream");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.action_compartir) + " " + o.getNombre());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, o.getNombre() + ".html");

                File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                ArrayList<Uri> u = new ArrayList<Uri>();
                File file = new File(path, o.getNombre() + ".html");
                if (file.exists() && file.canRead()) {
                    u.add(Uri.fromFile(file));
                }
                if (u.size() > 0) {
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, u);
                }
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_compartir)));
            }
        }
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