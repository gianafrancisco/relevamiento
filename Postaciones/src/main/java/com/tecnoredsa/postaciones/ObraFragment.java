package com.tecnoredsa.postaciones;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tecnoredsa.backend.ObraSeleccionada;
import com.tecnoredsa.helper.SqlHelperRelevamiento;
import com.tecnoredsa.model.Obra;

import java.io.File;

public class ObraFragment extends DialogFragment {
    AlertDialog dialog1=null;
    ListView my_listview=null;
    public ObraFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity().getActionBar().hide();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        SqlHelperRelevamiento dbRelevamiento=SqlHelperRelevamiento.getInstance(rootView.getContext());
        my_listview = (ListView)rootView.findViewById(R.id.listView_Obra);
        Button my_button = (Button)rootView.findViewById(R.id.bt_agregar_obra);
        ArrayAdapter<Obra> adapter=dbRelevamiento.getAdapterObra();
        my_listview.setAdapter(adapter);
        registerForContextMenu(my_listview);
        my_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialog = inflater.inflate(R.layout.dialog_agregar_obra, null);
                builder.setView(dialog);
                builder.setTitle(R.string.agregar_obra);
                builder.setPositiveButton(R.string.agregar_obra,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText my_editText=(EditText)dialog1.findViewById(R.id.te_nombre_nueva_obra);
                        String s=my_editText.getText().toString();
                        Log.v("DebugAPP", s);
                        if(s != ""){
                            Obra n=new Obra(s,0);
                            SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).addObra(n);
                            my_listview.setAdapter(SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).getAdapterObra());
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog1 = builder.create();
                dialog1.show();
            }
        });
        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ObraSeleccionada.getInstance().setObraSeleccionada((Obra)my_listview.getItemAtPosition(position));
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new PostacionFragment()).commit();
                Intent postesActivity = new Intent(getActivity(),MainActivityPostes.class);
                startActivity(postesActivity);

            }
        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.obra_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Obra o=(Obra)my_listview.getItemAtPosition(info.position);
        ObraSeleccionada.getInstance().setObraSeleccionada(o);
        switch(item.getItemId()){
            case R.id.action_obra_eliminar:
                Log.v("DebugAPP",o.getNombre()+" "+o.getId());
                SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).deleteObra(o);
                my_listview.setAdapter(SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).getAdapterObra());
                ObraSeleccionada.getInstance().setObraSeleccionada(null);
                return true;
            case R.id.action_obra_abrir:
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new PostacionFragment()).commit();
                Intent postesActivity = new Intent(getActivity(),MainActivityPostes.class);
                startActivity(postesActivity);
                return true;
            case R.id.action_cambiar_nombre:
                showDialogCambiarNombre();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void showDialogCambiarNombre(){
        Obra n=ObraSeleccionada.getInstance().getObraSeleccionada();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.dialog_agregar_obra, null);
        EditText my_editText=(EditText)v.findViewById(R.id.te_nombre_nueva_obra);
        my_editText.setText(n.getNombre().toString());
        builder.setView(v);
        builder.setTitle(R.string.action_cambiar_nombre);
        builder.setPositiveButton(R.string.action_cambiar_nombre,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText my_editText=(EditText)dialog1.findViewById(R.id.te_nombre_nueva_obra);
                String s=my_editText.getText().toString();
                Log.v("DebugAPP", s);
                if(s != ""){
                    Obra n=ObraSeleccionada.getInstance().getObraSeleccionada();
                    File path = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File dir = new File(path, n.getNombre());
                    n.setNombre(s);
                    SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).updateObra(n);
                    ObraSeleccionada.getInstance().setObraSeleccionada(n);
                    File newdir = new File(path, n.getNombre());
                    if(!dir.isDirectory()){
                        dir.mkdirs();
                    }
                    dir.renameTo(newdir);
                    my_listview.setAdapter(SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).getAdapterObra());
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog1 = builder.create();
        dialog1.show();
    }
}