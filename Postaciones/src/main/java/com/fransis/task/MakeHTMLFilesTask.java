package com.fransis.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.fransis.adapter.PostesArrayAdapter;
import com.fransis.backend.ObraSeleccionada;
import com.fransis.helper.PostesComparator;
import com.fransis.helper.SqlHelperRelevamiento;
import com.fransis.model.Obra;
import com.fransis.model.Postacion;
import com.fransis.postaciones.R;
import com.fransis.utils.Imagen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by francisco on 05/10/2016.
 */
public class MakeHTMLFilesTask extends AsyncTask<Void, Integer, Integer> {

    protected ProgressDialog dialog;
    protected MakeHTMLFilesTask mySelf;
    protected Boolean openEarth = false;
    private Intent sharingIntent;
    private Context mContext;
    private ExifInterface exif;

    public MakeHTMLFilesTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
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
        Obra o= ObraSeleccionada.getInstance().getObraSeleccionada();
        PostesArrayAdapter p = SqlHelperRelevamiento.getInstance(mContext.getApplicationContext()).getAdapterPostacion(o);
        p.sort(new PostesComparator());
        progressCount=p.getCount();


        File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
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
                File f=new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+o.getNombre()+"/"+po.getPostacion_id().toString());

                File files[] = f.listFiles();
                if(files != null) {
                    for (int j = 0; j < files.length; j++) {
                        //(openEarth.booleanValue()?po.getPostacion_id().toString():po.getCodificacion())
                        try {
                            exif = new ExifInterface(files[j].getAbsoluteFile().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        if(files[j].length() > 0) {
                            imgListHtml += "<tr><td><p style=\"" + Imagen.rotateCssPHtml(orientation, 300, 169) + "\"><img src=\"" +
                                    o.getNombre() + "/" +
                                    po.getCodificacion() + "/" +
                                    files[j].getName() +
                                    "\" style=\"width:300px; height: 169px;" + Imagen.rotateCss(orientation) + "\"></img></p></td></tr>";
                        }
                    }
                }

                if(!imgListHtml.equals("")) {

                    String detalle = mContext.getResources().getString(R.string.tipo_poste) + ": " + po.getTipo() + "</br>\n" +
                            mContext.getResources().getString(R.string.tipo_preformada) + ": " + po.getPreformada() + "</br>\n" +
                            mContext.getResources().getString(R.string.agregar_poste) + ": " + po.getAgregar() + "</br>\n" +
                            mContext.getResources().getString(R.string.ganancia) + ": " + po.getGanancia() + "</br>\n" +
                            mContext.getResources().getString(R.string.empalme) + ": " + po.getEmpalme() + "</br>\n" +
                            mContext.getResources().getString(R.string.mensula_prologada) + ": " + po.getMensula_prolongada() + "</br>\n" +
                            mContext.getResources().getString(R.string.detalles_adicionales) + ": " + po.getDetalle_adicional() + "</br>\n";


                    html += "<tr><td colspan=2><h3 style=\"background: #efefef\">" + po.getCodificacion() + "</h3></td></tr>";
                    html += "<tr><td style=\"vertical-align:text-top;\">" + detalle + "</td><td><table style=\"padding-top: 50px;\">" + imgListHtml + "</table></td></tr>";
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
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.action_compartir) + " " + o.getNombre());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, o.getNombre() + ".html");

            File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            ArrayList<Uri> u = new ArrayList<Uri>();
            File file = new File(path, o.getNombre() + ".html");
            if (file.exists() && file.canRead()) {
                u.add(Uri.fromFile(file));
            }
            if (u.size() > 0) {
                sharingIntent.putExtra(Intent.EXTRA_STREAM, u);
            }
            mContext.startActivity(Intent.createChooser(sharingIntent, mContext.getResources().getString(R.string.action_compartir)));
        }
    }
}
