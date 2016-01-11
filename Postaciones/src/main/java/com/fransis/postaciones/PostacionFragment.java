package com.fransis.postaciones;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fransis.backend.ObraSeleccionada;
import com.fransis.backend.PostacionSeleccionada;
import com.fransis.helper.SqlHelperRelevamiento;
import com.fransis.model.Obra;
import com.fransis.model.Postacion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostacionFragment extends DialogFragment {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int FILE_SELECT_CODE = 0;

    private AlertDialog dialog1=null;
    private ListView my_listview=null;
    private GridView gridview=null;
    private View.OnClickListener listener= null;
    //private TextView tvobralabel = null;
    public PostacionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_postacion, container, false);
        SqlHelperRelevamiento dbRelevamiento=SqlHelperRelevamiento.getInstance(rootView.getContext());
        Button my_button = (Button)rootView.findViewById(R.id.bt_agregar_poste);
        //tvobralabel = (TextView)rootView.findViewById(R.id.postacion_fragment_obra_label);
        //tvobralabel.setText(ObraSeleccionada.getInstance().getObraSeleccionada().getNombre());
        gridview = (GridView) rootView.findViewById(R.id.postacion_gridview);


        gridview.setAdapter(dbRelevamiento.getAdapterPostacion(ObraSeleccionada.getInstance().getObraSeleccionada()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(getActivity().getApplicationContext(),"Pos: "+position,Toast.LENGTH_LONG).show();
                Postacion p = (Postacion) gridview.getItemAtPosition(position);
                PostacionSeleccionada.getInstance().setPostacion(p);
                PostacionSeleccionada.getInstance().setInsertar(false);
                showDialogAgregarPoste(false);
            }
        });
        registerForContextMenu(gridview);

        listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostacionSeleccionada.getInstance().setInsertar(true);
                showDialogAgregarPoste(PostacionSeleccionada.getInstance().getInsertar());
            }
        };
        my_button.setOnClickListener(listener);
        //getActivity().getActionBar().show();
        getActivity().getActionBar().setTitle(ObraSeleccionada.getInstance().getObraSeleccionada().getNombre());

        //GpsHelperCoordinates gps = new GpsHelperCoordinates();
        //Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
        //Float f=new Float(gps.calulatePath(dbRelevamiento.getAdapterPostacion(o)));
        //Log.v("DebugAPP",f.toString());

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.postacion_menu, menu);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == 1) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.v("DebugAPP", "File Uri: " + uri.toString());

                    //File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+ObraSeleccionada.getInstance().getObraSeleccionada().getNombre()+"/"+p.getPostacion_id().toString());
                    //Log.v("DebugAPP",file.getAbsolutePath());
                    //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    //Uri contentUri = Uri.fromFile(file);
                    //mediaScanIntent.setData(contentUri);
                    //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    //getActivity().sendBroadcast(mediaScanIntent);

                    // Get the path
//                    String path = FileUtils.getPath(this, uri);
//                    Log.v("DebugAPP", "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Postacion p=(Postacion)gridview.getItemAtPosition(info.position);
        PostacionSeleccionada.getInstance().setPostacion(p);
        gridview.setAdapter(SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).getAdapterPostacion(ObraSeleccionada.getInstance().getObraSeleccionada()));
        switch(item.getItemId()){

            case R.id.action_poste_eliminar:
                SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).deletePostacion(p);
                gridview.setAdapter(SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).getAdapterPostacion(ObraSeleccionada.getInstance().getObraSeleccionada()));
                return true;
            case R.id.action_poste_insertar:
                PostacionSeleccionada.getInstance().setInsertar(true);
                showDialogAgregarPoste(true);
                return true;
            case R.id.action_poste_editar:
                PostacionSeleccionada.getInstance().setInsertar(false);
                showDialogAgregarPoste(false);
                return true;
            case R.id.action_capturar_imagen:
                dispatchTakePictureIntent();
                return true;
            //case R.id.action_abrir_imagen:
                //File path =
                //File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+ObraSeleccionada.getInstance().getObraSeleccionada().getNombre()+"/"+p.getPostacion_id().toString());
                //Log.v("DebugAPP",file.getAbsolutePath());
                //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //Uri contentUri = Uri.fromFile(file);
                //mediaScanIntent.setData(contentUri);
                //getActivity().getApplicationContext().sendBroadcast(mediaScanIntent);


                /*
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setData(Uri.fromFile(file));
                intent.setType("images/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.setDataAndType(Uri.fromFile(file), "file/*");
                try {
                startActivityForResult(Intent.createChooser(intent,"Selecciones el archivo a abrir"),FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(getActivity().getApplicationContext(), "No se encuentra ningun explorador de archivos instalado",
                            Toast.LENGTH_SHORT).show();
                }*/
            //return true;
            case R.id.action_actualizar_coordenadas:
                actualizarCoordenadas();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String id_poste = PostacionSeleccionada.getInstance().getPostacion().getPostacion_id().toString();
        String imageFileName = id_poste+"_" + timeStamp + "_";
        String obraName= ObraSeleccionada.getInstance().getObraSeleccionada().getNombre();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File storageProjectDir = new File(storageDir.getAbsolutePath() +"/"+obraName+"/"+id_poste);
        storageProjectDir.mkdirs();
/*        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES); */
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageProjectDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.v("DebugAPP",photoFile.getAbsoluteFile().toString());
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void actualizarCoordenadas(){
        Postacion p = PostacionSeleccionada.getInstance().getPostacion();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(loc == null){
            Toast.makeText(getActivity().getApplicationContext(),"Habilite el GPS la hubicación no pudo ser calculada",Toast.LENGTH_LONG).show();
        }else {
                p.setGps_latitude(loc.getLatitude());
                p.setGps_longitude(loc.getLongitude());
                Obra o = ObraSeleccionada.getInstance().getObraSeleccionada();
                SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).updatePostacion(o,p);
                Toast.makeText(getActivity().getApplicationContext(),"La posicion GPS ha sido actualizada en la postacion "+p.getCodificacion(),Toast.LENGTH_LONG).show();
        }
    }

    private void showDialogAgregarPoste(Boolean insertar){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.dialog_agregar_poste, null);
//        EditText editTextField=(EditText)v.findViewById(R.id.et_poste_descripcion);
//        InputMethodManager imm = (InputMethodManager)getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(editTextField.getWindowToken(), 0);

        if(!insertar){
            Postacion p = PostacionSeleccionada.getInstance().getPostacion();
            RadioGroup rg_tipo_poste =(RadioGroup) v.findViewById(R.id.rg_tipo_poste);
            RadioGroup rg_tipo_preformada =(RadioGroup) v.findViewById(R.id.rg_tipo_preformada);
            CheckBox cb_ganancia = (CheckBox) v.findViewById(R.id.cb_poste_ganancia);
            CheckBox cb_nuevo = (CheckBox) v.findViewById(R.id.cb_poste_agregar_nuevo);
            CheckBox cb_empalme = (CheckBox) v.findViewById(R.id.cb_poste_empalme);
            CheckBox cb_mensula = (CheckBox) v.findViewById(R.id.cb_poste_mensula_prolongada);
            EditText et_detalles = (EditText)v.findViewById(R.id.et_poste_descripcion);
            if(p.getTipo().equals("Cemento")){
                rg_tipo_poste.check(R.id.rb_poste_cemento);
            }else if(p.getTipo().equals("Madera")){
                rg_tipo_poste.check(R.id.rb_poste_madera);
            }else if(p.getTipo().equals("Alumbrado público")){
                rg_tipo_poste.check(R.id.rb_poste_alumnbrado_publico);
            }else if(p.getTipo().equals("Mensula")){
                rg_tipo_poste.check(R.id.rb_poste_menzula);
            }else if(p.getTipo().equals("Fachada")){
                rg_tipo_poste.check(R.id.rb_poste_fachada);
            }else{
                rg_tipo_poste.check(R.id.rb_poste_otro);
            }
            if(p.getPreformada().equals("Retención")){
                rg_tipo_preformada.check(R.id.rb_poste_retencion);
            }else if(p.getTipo().equals("Suspensión")){
                rg_tipo_preformada.check(R.id.rb_poste_suspension);
            }else if(p.getTipo().equals("Tensor")){
                rg_tipo_preformada.check(R.id.rb_poste_tensor);
            }

            //rg_tipo_preformada.check((p.getPreformada().equals("Retención"))?R.id.rb_poste_retencion:R.id.rb_poste_suspension);
            cb_ganancia.setChecked((p.getGanancia().equals("Si"))?true:false);
            cb_empalme.setChecked((p.getEmpalme().equals("Si"))?true:false);
            cb_mensula.setChecked((p.getMensula_prolongada().equals("Si"))?true:false);
            cb_nuevo.setChecked((p.getAgregar().equals("Si"))?true:false);
            et_detalles.setText(p.getDetalle_adicional());
            builder.setTitle(R.string.action_editar);
        }else
            builder.setTitle(R.string.agregar_poste);
        builder.setView(v);


        builder.setPositiveButton(((PostacionSeleccionada.getInstance().getInsertar())?R.string.agregar_poste:R.string.action_editar),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup rg_tipo_poste =(RadioGroup) dialog1.findViewById(R.id.rg_tipo_poste);
                RadioGroup rg_tipo_preformada =(RadioGroup) dialog1.findViewById(R.id.rg_tipo_preformada);
                CheckBox cb_ganancia = (CheckBox) dialog1.findViewById(R.id.cb_poste_ganancia);
                CheckBox cb_nuevo = (CheckBox) dialog1.findViewById(R.id.cb_poste_agregar_nuevo);
                CheckBox cb_empalme = (CheckBox) dialog1.findViewById(R.id.cb_poste_empalme);
                CheckBox cb_mensula = (CheckBox) dialog1.findViewById(R.id.cb_poste_mensula_prolongada);
                EditText et_detalles = (EditText)dialog1.findViewById(R.id.et_poste_descripcion);
                Postacion p;
                if(PostacionSeleccionada.getInstance().getInsertar())
                    p=new Postacion(0);
                else
                    p=PostacionSeleccionada.getInstance().getPostacion();

                Resources res=getResources();
                switch(rg_tipo_poste.getCheckedRadioButtonId()){
                    case R.id.rb_poste_cemento:
                        p.setTipo(res.getString(R.string.tipo_cemento));
                        break;
                    case R.id.rb_poste_madera:
                        p.setTipo(res.getString(R.string.tipo_madera));
                        break;
                    case R.id.rb_poste_alumnbrado_publico:
                        p.setTipo(res.getString(R.string.tipo_alumbrado));
                        break;
                    case R.id.rb_poste_fachada:
                        p.setTipo(res.getString(R.string.tipo_fachada));
                        break;
                    case R.id.rb_poste_menzula:
                        p.setTipo(res.getString(R.string.tipo_mensula));
                        break;
                    default:
                        p.setTipo(res.getString(R.string.tipo_otro));
                        break;
                }
                switch(rg_tipo_preformada.getCheckedRadioButtonId()){
                    case R.id.rb_poste_retencion:
                        p.setPreformada(res.getString(R.string.tipo_preformada_retencion));
                        break;
                    case R.id.rb_poste_suspension:
                        p.setPreformada(res.getString(R.string.tipo_preformada_suspension));
                        break;
                    case R.id.rb_poste_tensor:
                        p.setPreformada(res.getString(R.string.tipo_preformada_tensor));
                        break;
                }
                p.setGanancia(cb_ganancia.isChecked()?"Si":"No");
                p.setEmpalme(cb_empalme.isChecked()?"Si":"No");
                p.setMensula_prolongada(cb_mensula.isChecked()?"Si":"No");
                p.setAgregar(cb_nuevo.isChecked()?"Si":"No");
                p.setDetalle_adicional(et_detalles.getText().toString());
                Obra o=ObraSeleccionada.getInstance().getObraSeleccionada();
                p.setObra_id(o.getId());
                if(PostacionSeleccionada.getInstance().getInsertar()){
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);
                    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc == null){
                        Toast.makeText(getActivity().getApplicationContext(),"Habilite el GPS la hubicación no pudo ser calculada",Toast.LENGTH_LONG).show();
                    }else {
                        p.setGps_latitude(loc.getLatitude());
                        p.setGps_longitude(loc.getLongitude());
                        SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).addPoste(o,p);
                    }
                }else{
                    SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).updatePostacion(o,p);
                }
                gridview.setAdapter(SqlHelperRelevamiento.getInstance(getActivity().getApplicationContext()).getAdapterPostacion(ObraSeleccionada.getInstance().getObraSeleccionada()));
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