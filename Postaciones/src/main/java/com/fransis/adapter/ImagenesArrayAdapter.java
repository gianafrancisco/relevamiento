package com.fransis.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fransis.backend.PostacionSeleccionada;
import com.fransis.helper.GpsHelperCoordinates;
import com.fransis.model.Obra;
import com.fransis.model.Postacion;
import com.fransis.postaciones.R;

import java.io.File;

/**
 * Created by francisco on 24/01/14.
 */
public class ImagenesArrayAdapter extends ArrayAdapter<Bitmap> {

    private Context mContext=null;
    private Obra obra = null;

    public ImagenesArrayAdapter(Context context, int resource) {
        super(context, resource);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bitmap file = (Bitmap)getItem(position);
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = vi.inflate(R.layout.item_imagen, null);
        ImageView iv_camera = (ImageView)convertView.findViewById(R.id.iv_preview);
        iv_camera.setImageBitmap(file);
        return convertView;
    }
}
