package com.fransis.postaciones;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fransis.adapter.ImagenesArrayAdapter;
import com.fransis.backend.ObraSeleccionada;
import com.fransis.backend.PostacionSeleccionada;
import com.fransis.helper.SqlHelperRelevamiento;
import com.fransis.model.Obra;
import com.fransis.model.Postacion;
import com.fransis.utils.Imagen;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImagenFragment extends DialogFragment {


    private GridView gridview = null;
    private ImageView imageView = null;
    private Obra obra;
    private Postacion postacion;
    public ImagenFragment() {
        obra = ObraSeleccionada.getInstance().getObraSeleccionada();
        postacion = PostacionSeleccionada.getInstance().getPostacion();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_imagenes, container, false);
        gridview = (GridView) rootView.findViewById(R.id.imagenes_gridview);
        imageView = (ImageView) rootView.findViewById(R.id.iv_presentacion);

        File f = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + obra.getNombre() + "/" + postacion.getPostacion_id().toString());
        File files[] = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains(".jpg");
            }
        });
        ImagenesArrayAdapter imagenesArrayAdapter = new ImagenesArrayAdapter(getActivity(), R.layout.item_imagen);
        ExifInterface exif = null;
        if(files != null && files.length > 0){
            for(File fi: files){

                try {
                    exif = new ExifInterface(fi.getAbsoluteFile().toString());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap thumbImage = Imagen.rotateBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fi.getAbsoluteFile().toString()), 512, 512), orientation);
                    imagenesArrayAdapter.add(thumbImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        gridview.setAdapter(imagenesArrayAdapter);
        if(!imagenesArrayAdapter.isEmpty()){
            imageView.setImageBitmap((Bitmap) gridview.getItemAtPosition(0));
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Bitmap f = (Bitmap) gridview.getItemAtPosition(position);
                imageView.setImageBitmap(f);
            }
        });
        registerForContextMenu(gridview);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.imagen_menu, menu);
    }


    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.action_imagen_eliminar:
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }


}