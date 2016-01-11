package com.fransis.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fransis.backend.PostacionSeleccionada;
import com.fransis.helper.GpsHelperCoordinates;
import com.fransis.model.Postacion;
import com.fransis.postaciones.R;

import java.util.List;

/**
 * Created by francisco on 24/01/14.
 */
public class PostesArrayAdapter extends ArrayAdapter<Postacion> {

    Context mContext=null;
    GpsHelperCoordinates gps=null;

    public PostesArrayAdapter(Context context, int resource) {
        super(context, resource);
        mContext=context;
        gps=new GpsHelperCoordinates();
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Postacion p = (Postacion)getItem(position);
        Postacion psel = PostacionSeleccionada.getInstance().getPostacion();
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //if( convertView == null ){
            //We must create a View:
            convertView = vi.inflate(R.layout.item_postacion, null);

            TextView tv_tipo=(TextView)convertView.findViewById(R.id.item_postacion_tipo);
            TextView tv_preformada=(TextView)convertView.findViewById(R.id.item_postacion_preformada);
            TextView tv_id=(TextView)convertView.findViewById(R.id.item_postacion_numeacion);
            TextView tv_gps=(TextView)convertView.findViewById(R.id.item_postacion_coordenadas);
            tv_id.setText(p.getCodificacion());
            if(psel != null && p.getPostacion_id() == psel.getPostacion_id()) {
                convertView.setBackgroundColor(Color.rgb(0x54, 0x80, 0xd9));
            }
            tv_tipo.setText(p.getTipo());
            tv_preformada.setText(p.getPreformada());
            //tv_gps.setText(p.getGps_latitude().toString()+"\n"+p.getGps_longitude().toString());
            tv_gps.setText(gps.decimal2degree(p.getGps_latitude())+"\n"+gps.decimal2degree(p.getGps_longitude()));
            return convertView;
        //}
        //return super.getView(position, convertView, parent);
    }
}
