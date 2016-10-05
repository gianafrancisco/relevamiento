package com.fransis.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.fransis.helper.GpsHelperCoordinates;

/**
 * Created by francisco on 05/10/2016.
 */
public class MakeKMLFilesTask extends ExportKMLFilesTask {

    public MakeKMLFilesTask(Context mContext, GpsHelperCoordinates gps) {
        super(mContext, gps);
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
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
