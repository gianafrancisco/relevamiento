package com.tecnoredsa.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.tecnoredsa.helper.SqlHelperRelevamiento;
import com.tecnoredsa.model.Obra;
import com.tecnoredsa.postaciones.R;

/**
 * Created by francisco on 22/01/14.
 */
public class DialogAgregarObra extends AlertDialog {
    public DialogAgregarObra(Context context) {
        super(context);

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //LayoutInflater inflater = context.getApplicationContext().getLagetLayoutInflater();
        //setView(inflater.inflate(R.layout.dialog_agregar_obra, null));
        //setTitle(R.string.agregar_obra);

        /*setButton(BUTTON_POSITIVE, R.string.agregar_obra, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LayoutInflater inflater = getc.getLayoutInflater();
                final View inflator = inflater.inflate(R.layout.dialog_agregar_obra, null);
                EditText my_editText = (EditText) dialog1.findViewById(R.id.te_nombre_nueva_obra);
                String s = my_editText.getText().toString();
                Log.v("DebugAPP", s);
                if (s != "") {
                    Obra n = new Obra(s, 0);
                    SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).addObra(n);
                }
            }
        });
        setButton(BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog1 = builder.create();
        dialog1.show();*/
    }


}
