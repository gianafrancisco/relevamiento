package com.fransis.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.fransis.backend.ObraSeleccionada;
import com.fransis.helper.GpsHelperCoordinates;
import com.fransis.model.Obra;

import java.io.File;

/**
 * Created by francisco on 05/10/2016.
 */
public class ViewKMLFilesTask extends ExportKMLFilesTask{

    public ViewKMLFilesTask(Context mContext, GpsHelperCoordinates gps) {
        super(mContext, gps);
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
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
            File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, o.getNombre() + ".kml");
            if (file.exists() && file.canRead()) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.google-earth.kml+xml");
                mContext.startActivity(intent);
            }
        }

    }
}