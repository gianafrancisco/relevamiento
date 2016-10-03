package com.fransis.adapter;

import android.content.Context;
import android.graphics.Color;
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
import java.util.List;

/**
 * Created by francisco on 24/01/14.
 */
public class PostesArrayAdapter extends ArrayAdapter<Postacion> {

    private Context mContext=null;
    private GpsHelperCoordinates gps=null;
    private Obra obra = null;

    public PostesArrayAdapter(Context context, int resource, Obra obra) {
        super(context, resource);
        mContext=context;
        gps=new GpsHelperCoordinates();
        this.obra = obra;
    }
/*
    public PostesArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        mContext=context;
        gps=new GpsHelperCoordinates();
    }

    public PostesArrayAdapter(Context context, int resource, Postacion[] objects) {
        super(context, resource, objects);
        mContext=context;
        gps=new GpsHelperCoordinates();
    }

    public PostesArrayAdapter(Context context, int resource, int textViewResourceId, Postacion[] objects) {
        super(context, resource, textViewResourceId, objects);
        mContext=context;
        gps=new GpsHelperCoordinates();
    }

    public PostesArrayAdapter(Context context, int resource, List<Postacion> objects) {
        super(context, resource, objects);
        mContext=context;
        gps=new GpsHelperCoordinates();
    }

    public PostesArrayAdapter(Context context, int resource, int textViewResourceId, List<Postacion> objects) {
        super(context, resource, textViewResourceId, objects);
        mContext=context;
        gps=new GpsHelperCoordinates();
    }
*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Postacion p = (Postacion)getItem(position);
        Postacion psel = PostacionSeleccionada.getInstance().getPostacion();
        File f = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+obra.getNombre()+"/"+p.getPostacion_id().toString());

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = vi.inflate(R.layout.item_postacion, null);
        TextView tv_tipo=(TextView)convertView.findViewById(R.id.item_postacion_tipo);
        TextView tv_preformada=(TextView)convertView.findViewById(R.id.item_postacion_preformada);
        TextView tv_id=(TextView)convertView.findViewById(R.id.item_postacion_numeacion);
        TextView tv_gps=(TextView)convertView.findViewById(R.id.item_postacion_coordenadas);
        ImageView iv_camera = (ImageView)convertView.findViewById(R.id.iv_camera_icon);
        File[] files = f.listFiles();
        if(files != null && files.length > 0){
            iv_camera.setVisibility(View.VISIBLE);
        }
        tv_id.setText(p.getCodificacion());
        if(psel != null && p.getPostacion_id() == psel.getPostacion_id()) {
            convertView.setBackgroundColor(Color.rgb(0x54, 0x80, 0xd9));
        }
        tv_tipo.setText(p.getTipo());
        tv_preformada.setText(p.getPreformada());

        tv_gps.setText(gps.decimal2degree(p.getGps_latitude())+"\n"+gps.decimal2degree(p.getGps_longitude()));
        return convertView;
    }
}
