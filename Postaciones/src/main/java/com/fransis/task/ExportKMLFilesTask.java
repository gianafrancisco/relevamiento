package com.fransis.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.fransis.adapter.PostesArrayAdapter;
import com.fransis.backend.ObraSeleccionada;
import com.fransis.helper.GpsHelperCoordinates;
import com.fransis.helper.PostesComparator;
import com.fransis.helper.SqlHelperRelevamiento;
import com.fransis.model.Obra;
import com.fransis.model.Postacion;
import com.fransis.postaciones.R;
import com.fransis.utils.Imagen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by francisco on 05/10/2016.
 */
public class ExportKMLFilesTask extends AsyncTask<Void, Integer, Integer> {

    protected ProgressDialog dialog;
    protected ExportKMLFilesTask mySelf;
    protected Boolean openEarth = false;
    private Intent sharingIntent;
    protected Context mContext;
    private ExifInterface exif;
    private GpsHelperCoordinates gps=null;

    public ExportKMLFilesTask(Context mContext, GpsHelperCoordinates gps) {
        this.mContext = mContext;
        this.gps = gps;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
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
        final String realizadoPor = "<p>Realizado por <a href='http://www.gianafracisco.com.ar'>Francisco Giana</a></p>\n";
        int progress=0;
        int progressCount=0;
        Obra o= ObraSeleccionada.getInstance().getObraSeleccionada();
        PostesArrayAdapter p = SqlHelperRelevamiento.getInstance(mContext.getApplicationContext()).getAdapterPostacion(o);
        p.sort(new PostesComparator());
        if(!openEarth.booleanValue())
            progressCount=p.getCount()*2;
        else
            progressCount=p.getCount();

        File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File fileo = new File(path, o.getNombre().trim() + ".kml");
        FileOutputStream fo = null;
        File filecsvo = new File(path, o.getNombre().trim() + ".csv");
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
        StringBuilder str = new StringBuilder();
        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://www.opengis.net/kml/2.2\"><Document>" +
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
                "<Folder><name>" + o.getNombre().trim() + "</name><open>1</open>");
        str.append("<description><p>Referencias</p>" +
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
                "</description>");
        //str+="<Style id=\"PA\"><IconStyle> <Icon> <href>icons/PA.png</href> </Icon></IconStyle></Style><Style id=\"PC\"><IconStyle> <Icon> <href>icons/PC.png</href> </Icon></IconStyle></Style><Style id=\"PMa\"><IconStyle> <Icon> <href>icons/PMa.png</href> </Icon></IconStyle></Style><Style id=\"PF\"><IconStyle> <Icon> <href>icons/PF.png</href> </Icon></IconStyle></Style><Style id=\"PMe\"><IconStyle> <Icon> <href>icons/PMe.png</href> </Icon></IconStyle></Style><Style id=\"PO\"><IconStyle> <Icon> <href>icons/PO.png</href> </Icon></IconStyle></Style><Style id=\"G\"><IconStyle> <Icon> <href>icons/G.png</href> </Icon></IconStyle></Style><Style id=\"E\"><IconStyle> <Icon> <href>icons/E.png</href> </Icon></IconStyle></Style><Style id=\"N\"><IconStyle> <Icon> <href>icons/N.png</href> </Icon></IconStyle></Style><Style id=\"MP\"><IconStyle> <Icon> <href>icons/MP.png</href> </Icon></IconStyle></Style>";
        StringBuilder csv = new StringBuilder();
        csv.append(mContext.getResources().getString(R.string.codificacion)+"," +
                mContext.getResources().getString(R.string.tipo_poste)+","+
                mContext.getResources().getString(R.string.tipo_preformada)+","+
                mContext.getResources().getString(R.string.agregar_poste)+","+
                mContext.getResources().getString(R.string.ganancia)+","+
                mContext.getResources().getString(R.string.empalme)+","+
                mContext.getResources().getString(R.string.mensula_prologada)+","+
                mContext.getResources().getString(R.string.detalles_adicionales)+","+
                mContext.getResources().getString(R.string.latitud)+","+
                mContext.getResources().getString(R.string.longitud)+"\n");

        if(p != null) {
            for (int i = 0; i < p.getCount(); i++) {
                if (isCancelled()) return -1;
                publishProgress((int) ((progress / (float)progressCount) * 100));
                progress++;
                Postacion po = (Postacion) p.getItem(i);
                String imgList="";
                String imgListHtml="";
                File f=new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+o.getNombre()+"/"+po.getPostacion_id().toString());
                //Log.v("DebugAPP",f.getAbsolutePath().toString());

                File files[] = f.listFiles();
                if(files != null) {
                    for (int j = 0; j < files.length; j++) {
                        //Log.v("DebugAPP",files[j].getName().toString());

                        try {
                            exif = new ExifInterface(files[j].getAbsoluteFile().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        imgList += "<tr><td><p style=\"" + (openEarth.booleanValue() ? Imagen.rotateCssPHtml(orientation, 220, 124) : Imagen.rotateCssPHtml(orientation, 400, 225)) + "\"><img src=\"" +
                                o.getNombre() + "/" +
                                (openEarth.booleanValue() ? po.getPostacion_id().toString() : po.getCodificacion()) + "/" +
                                files[j].getName() +
                                "\" style=\"" + (openEarth.booleanValue() ? "width:220px; height: 124px;" : "width:400px; height: 225px;") + Imagen.rotateCss(orientation) + "\"></img></p></td></tr>";

/*
                            imgListHtml += "<tr><td><p><img src=\"" +
                                    o.getNombre() + "/" +
                                    (openEarth.booleanValue()?po.getPostacion_id().toString():po.getCodificacion()) + "/" +
                                    files[j].getName() +
                                    "\" style=\"width:300px; height: 169px;\"></img></p></td></tr>";
*/

                    }
                }

                csv.append(po.getCodificacion()+",");
                csv.append(po.getTipo()+",");
                csv.append(po.getPreformada()+",");
                csv.append(po.getAgregar()+",");
                csv.append(po.getGanancia()+",");
                csv.append(po.getEmpalme()+",");
                csv.append(po.getMensula_prolongada()+",");
                csv.append("\""+po.getDetalle_adicional()+"\",");
                csv.append(gps.decimal2degree(po.getGps_latitude())+",");
                csv.append(gps.decimal2degree(po.getGps_longitude())+"\n");

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
                str.append("<Placemark>");
                str.append("<name>" + po.getCodificacion() + "</name>");
                str.append("<description>" +
                        "<table><tbody><tr><td>" +
                        realizadoPor +
                        "<p></p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.tipo_poste) + ": " + po.getTipo() + "</p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.tipo_preformada) + ": " + po.getPreformada() + "</p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.agregar_poste) + ": " + po.getAgregar() + "</p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.ganancia) + ": " + po.getGanancia() + "</p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.empalme) + ": " + po.getEmpalme() + "</p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.mensula_prologada) + ": " + po.getMensula_prolongada() + "</p>\n" +
                        "<p>"+mContext.getResources().getString(R.string.detalles_adicionales) + ": " + po.getDetalle_adicional() + "</p>\n" +
                        "</td></tr><tr><td><table style=\"padding-top: 50px;\">" +
                        imgList +
                        "</table></td></tr><tr><td>" +
                        realizadoPor +
                        "<p></p>\n" +
                        "</td></tr>" +
                        "</tbody></table>" +
                        "</description>");
                str.append("<LookAt>");
                str.append("<longitude>" + po.getGps_longitude() + "</longitude>");
                str.append("<latitude>" + po.getGps_latitude() + "</latitude>");
                str.append("<altitude>0</altitude>");
                str.append("</LookAt>");
                str.append("<styleUrl>#"+po.getImagenTipo()+"</styleUrl>");
                str.append("<Point>");
                str.append("<coordinates>" + po.getGps_longitude() + "," + po.getGps_latitude() + ",0</coordinates>");
                str.append("</Point>");
                str.append("</Placemark>");
            }
        }
        str.append("</Folder></Document></kml>");
/*
            html+="</table></body></html>";
*/
        try {

            fo.write(str.toString().getBytes());
            fo.close();
            fcsvo.write(csv.toString().getBytes());
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
            p = SqlHelperRelevamiento.getInstance(mContext.getApplicationContext()).getAdapterPostacion(o);
            path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
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
                    File f = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + o.getNombre() + "/" + po.getPostacion_id().toString());
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

                File file_db = new File(mContext.getDatabasePath("relevamiento.db").getAbsolutePath());
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
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.action_compartir) + " " + o.getNombre());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, o.getNombre() + ".kml");

            File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
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
            mContext.startActivity(Intent.createChooser(sharingIntent, mContext.getResources().getString(R.string.action_compartir)));
        }
    }
}
